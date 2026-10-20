package ua.govstack.digitalwallet.app.wallet_gallery.data.repository

import eu.europa.ec.eudi.wallet.document.DeferredDocument
import eu.europa.ec.eudi.wallet.document.Document
import eu.europa.ec.eudi.wallet.document.DocumentId
import eu.europa.ec.eudi.wallet.document.IssuedDocument
import eu.europa.ec.eudi.wallet.document.format.MsoMdocFormat
import eu.europa.ec.eudi.wallet.document.format.SdJwtVcFormat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ua.govstack.digitalwallet.app.core.models.document.DocumentWithMetadata
import ua.govstack.digitalwallet.app.core.network.Http
import ua.govstack.digitalwallet.app.wallet_core.documents.model.DeleteWalletDocumentResult
import ua.govstack.digitalwallet.app.wallet_core.documents.model.DocOrder
import ua.govstack.digitalwallet.app.wallet_core.documents.store.DocumentStore
import ua.govstack.digitalwallet.app.wallet_gallery.domain.model.DocManualOptions
import ua.govstack.digitalwallet.app.wallet_core.documents.model.DocumentIdentifier
import ua.govstack.digitalwallet.app.wallet_gallery.domain.provider.EUDocOrderProvider
import ua.govstack.digitalwallet.app.wallet_gallery.domain.transformer.DeferredDocumentTransformer
import ua.govstack.digitalwallet.app.wallet_gallery.domain.transformer.MdlTransformer
import ua.govstack.digitalwallet.app.wallet_gallery.domain.transformer.PidTransformer

interface DocumentsRepository {
    val documentsData: Flow<List<DocumentWithMetadata>>

    val isDataLoading: Flow<Boolean>

    suspend fun getDocumentsWithMetadata(): List<DocumentWithMetadata>

    suspend fun fetchDocuments()

    fun deleteDocument(documentId: DocumentId): Flow<DeleteWalletDocumentResult>

    fun saveDocTypeOrder(docOrders: List<DocOrder>)

    fun invalidate()
}

internal class DocumentsRepositoryImpl(
    private val scope: CoroutineScope,
    private val documentStore: DocumentStore,
    private val pidTransformer: PidTransformer,
    private val mdlTransformer: MdlTransformer,
    private val deferredDocumentTransformer: DeferredDocumentTransformer,
    private val orderProvider: EUDocOrderProvider,
) : DocumentsRepository {

    private val _isDataLoading = MutableStateFlow(false)
    override val isDataLoading: Flow<Boolean>
        get() = _isDataLoading

    override val documentsData: Flow<List<DocumentWithMetadata>>
        get() = documentStore.documentsData.map { documents ->
            prepareDataToPublish(buildDocumentsForPublish(documents))
        }

    private var invalidateDocJob: Job? = null

    override suspend fun getDocumentsWithMetadata(): List<DocumentWithMetadata> {
        val documents = documentStore.documentsData.first()
        return prepareDataToPublish(buildDocumentsForPublish(documents))
    }

    override suspend fun fetchDocuments() {
        _isDataLoading.value = true
        try {
            documentStore.refreshDocuments()
        } finally {
            _isDataLoading.value = false
        }
    }

    override fun deleteDocument(documentId: DocumentId): Flow<DeleteWalletDocumentResult> {
        return documentStore.deleteDocument(documentId)
    }

    override fun saveDocTypeOrder(docOrders: List<DocOrder>) {
        scope.launch {
            if (docOrders.isEmpty()) return@launch
            orderProvider.saveDocTypeOrder(docOrders)
            invalidate()
        }
    }

    override fun invalidate() {
        if (invalidateDocJob?.isActive == true) {
            return
        }
        invalidateDocJob = scope.launch {
            fetchDocuments()
        }
    }

    private fun prepareDataToPublish(data: List<DocumentWithMetadata>): List<DocumentWithMetadata> {
        return data.takeIf { it.isNotEmpty() }?.let {
            it + DocumentWithMetadata(
                document = DocManualOptions(),
                timestamp = "",
                expirationDate = "",
                status = Http.HTTP_200,
                type = "error_placeholder",
                order = DocumentWithMetadata.LAST_DOC_ORDER + 1
            )
        } ?: data
    }

    private suspend fun buildDocumentsForPublish(
        documents: List<Document>,
    ): List<DocumentWithMetadata> {
        val orders = orderProvider.getEUDocOrders()
        val revokedDocumentIds = documentStore.getRevokedDocuments().map { it.identifier }

        val issuedDocuments = documents
            .filterIsInstance<IssuedDocument>()
            .filter { it.getCredentials().isNotEmpty() }
            .mapNotNull { item ->
                val isRevoked = revokedDocumentIds.any { it == item.id }

                val documentFormatAsString = when (val format = item.format) {
                    is MsoMdocFormat -> format.docType
                    is SdJwtVcFormat -> format.vct
                }

                val mappingResult = when (documentFormatAsString) {
                    DocumentIdentifier.MdocPid.formatType -> pidTransformer.transformToPid(
                        document = item,
                        order = orders[item.id]?.toInt() ?: 0,
                        isRevoked = isRevoked
                    )

                    DocumentIdentifier.SdJwtPid.formatType -> pidTransformer.transformToPid(
                        document = item,
                        order = orders[item.id]?.toInt() ?: 0,
                        isRevoked = isRevoked
                    )

                    DocumentIdentifier.MdocMdl.formatType -> mdlTransformer.transformToMdl(
                        document = item,
                        order = orders[item.id]?.toInt() ?: 0,
                        isRevoked = isRevoked
                    )

                    else -> null
                }

                mappingResult?.getData()?.firstOrNull()?.let { data ->
                    DocumentWithMetadata(
                        document = data,
                        timestamp = "",
                        expirationDate = "",
                        status = data.getStatus(),
                        type = data.getItemType(),
                        order = data.getDocOrder()
                    )
                }
            }

        val deferredDocuments = documents
            .filterIsInstance<DeferredDocument>()
            .mapNotNull { item ->
                val documentFormatAsString = when (val format = item.format) {
                    is MsoMdocFormat -> format.docType
                    is SdJwtVcFormat -> format.vct
                }

                val mappingResult = when (documentFormatAsString) {
                    DocumentIdentifier.MdocPid.formatType,
                    DocumentIdentifier.SdJwtPid.formatType -> deferredDocumentTransformer.transformToPid(
                        document = item,
                        order = orders[item.id]?.toInt() ?: 0
                    )

                    DocumentIdentifier.MdocMdl.formatType -> deferredDocumentTransformer.transformToMdl(
                        document = item,
                        order = orders[item.id]?.toInt() ?: 0
                    )

                    else -> null
                }

                mappingResult?.getData()?.firstOrNull()?.let { data ->
                    DocumentWithMetadata(
                        document = data,
                        timestamp = "",
                        expirationDate = "",
                        status = data.getStatus(),
                        type = data.getItemType(),
                        order = data.getDocOrder()
                    )
                }
            }

        return (issuedDocuments + deferredDocuments).sortedBy { it.order }
    }
}
