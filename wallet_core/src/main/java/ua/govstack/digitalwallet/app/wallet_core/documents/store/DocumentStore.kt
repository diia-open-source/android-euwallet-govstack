package ua.govstack.digitalwallet.app.wallet_core.documents.store

import eu.europa.ec.eudi.wallet.EudiWallet
import eu.europa.ec.eudi.wallet.document.DeferredDocument
import eu.europa.ec.eudi.wallet.document.Document
import eu.europa.ec.eudi.wallet.document.DocumentId
import eu.europa.ec.eudi.wallet.document.IssuedDocument
import kotlinx.coroutines.flow.Flow
import eu.europa.ec.eudi.wallet.document.format.MsoMdocFormat
import eu.europa.ec.eudi.wallet.document.format.SdJwtVcFormat
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import ua.govstack.digitalwallet.app.core.util.extensions.safeAsync
import ua.govstack.digitalwallet.app.wallet_core.database.dao.RevokedDocumentDao
import ua.govstack.digitalwallet.app.wallet_core.documents.model.DeleteAllWalletDocumentsResult
import ua.govstack.digitalwallet.app.wallet_core.documents.model.DeleteWalletDocumentResult
import ua.govstack.digitalwallet.app.wallet_core.wallet.configs.WalletCoreConfig
import ua.govstack.digitalwallet.app.wallet_core.documents.model.DocumentCategories
import ua.govstack.digitalwallet.app.wallet_core.documents.model.DocumentIdentifier
import ua.govstack.digitalwallet.app.wallet_core.database.model.RevokedDocument

interface DocumentStore {
    val documentsData: Flow<List<Document>>

    fun getAllDocuments(): List<Document>

    fun getAllIssuedDocuments(): List<IssuedDocument>

    fun getAllDocumentsByType(documentIdentifiers: List<DocumentIdentifier>): List<IssuedDocument>

    fun getDocumentById(documentId: DocumentId): Document?

    fun getMainPidDocument(): IssuedDocument?

    fun deleteDocument(documentId: DocumentId): Flow<DeleteWalletDocumentResult>

    fun deleteAllDocuments(): Flow<DeleteAllWalletDocumentsResult>

    fun getAllDocumentCategories(): DocumentCategories

    fun isDocumentsExist(): Boolean

    fun refreshDocuments()

    suspend fun getRevokedDocuments() : List<RevokedDocument>

    suspend fun clear()
}

internal class DocumentStoreImpl(
    private val eudiWallet: EudiWallet,
    private val walletCoreConfig: WalletCoreConfig,
    private val revokedDocumentDao: RevokedDocumentDao,
) : DocumentStore {

    private val genericErrorMessage
        get() = "Something went wrong"

    private val _documentsData = MutableStateFlow<List<Document>>(emptyList())
    override val documentsData: Flow<List<Document>>
        get() = combine(
            _documentsData,
            revokedDocumentDao.observeAll()
        ) { documents, _ ->
            // Re-emit the current wallet documents whenever the revoked-documents table changes
            // so gallery/status consumers rebuild their models with the latest revocation flags.
            documents
        }

    init {
        refreshDocuments()
    }

    override fun getAllDocuments(): List<Document> =
        eudiWallet.getDocuments { it is IssuedDocument || it is DeferredDocument }

    override fun getAllIssuedDocuments(): List<IssuedDocument> =
        eudiWallet.getDocuments().filterIsInstance<IssuedDocument>()

    override fun getAllDocumentsByType(documentIdentifiers: List<DocumentIdentifier>): List<IssuedDocument> =
        getAllDocuments()
            .filterIsInstance<IssuedDocument>()
            .filter {
                when (it.format) {
                    is MsoMdocFormat -> documentIdentifiers.any { id ->
                        id.formatType == (it.format as MsoMdocFormat).docType
                    }

                    is SdJwtVcFormat -> documentIdentifiers.any { id ->
                        id.formatType == (it.format as SdJwtVcFormat).vct
                    }
                }
            }

    override fun getDocumentById(documentId: DocumentId): Document? {
        return eudiWallet.getDocumentById(documentId = documentId)
    }

    override fun getMainPidDocument(): IssuedDocument? =
        getAllDocumentsByType(
            documentIdentifiers = listOf(
                DocumentIdentifier.MdocPid,
                DocumentIdentifier.SdJwtPid
            )
        ).minByOrNull { it.createdAt }

    override fun deleteDocument(documentId: DocumentId): Flow<DeleteWalletDocumentResult> =
        executeDeleteDocument(documentId = documentId)

    private fun executeDeleteDocument(documentId: DocumentId): Flow<DeleteWalletDocumentResult> = flow {
        eudiWallet.deleteDocumentById(documentId = documentId)
            .kotlinResult
            .onSuccess {
                revokedDocumentDao.delete(documentId)
                refreshDocuments()
                emit(DeleteWalletDocumentResult.Success)
            }
            .onFailure {
                emit(
                    DeleteWalletDocumentResult.Failure(
                        errorMessage = it.message
                            ?: genericErrorMessage
                    )
                )
            }
    }.safeAsync {
        DeleteWalletDocumentResult.Failure(
            errorMessage = it.message ?: genericErrorMessage
        )
    }

    override fun deleteAllDocuments(): Flow<DeleteAllWalletDocumentsResult> =
        flow {

            val allDocuments = getAllDocuments()
            val mainPidDocument = getMainPidDocument()

            mainPidDocument?.let { safeMainPidDocument ->

                val restOfDocuments = allDocuments.filterNot { doc ->
                    doc.id == safeMainPidDocument.id
                }

                var restOfAllDocsDeleted = true
                var restOfAllDocsDeletedFailureReason = ""

                restOfDocuments.forEach { document ->

                    executeDeleteDocument(
                        documentId = document.id,
                    ).collect { deleteDocumentResult ->
                        when (deleteDocumentResult) {
                            is DeleteWalletDocumentResult.Failure -> {
                                restOfAllDocsDeleted = false
                                restOfAllDocsDeletedFailureReason =
                                    deleteDocumentResult.errorMessage
                            }

                            is DeleteWalletDocumentResult.Success -> {}
                        }
                    }
                }

                if (restOfAllDocsDeleted) {
                    executeDeleteDocument(
                        documentId = safeMainPidDocument.id,
                    ).collect { deleteMainPidDocumentResult ->
                        when (deleteMainPidDocumentResult) {
                            is DeleteWalletDocumentResult.Failure -> emit(
                                DeleteAllWalletDocumentsResult.Failure(
                                    errorMessage = deleteMainPidDocumentResult.errorMessage
                                )
                            )

                            is DeleteWalletDocumentResult.Success -> emit(
                                DeleteAllWalletDocumentsResult.Success
                            )
                        }
                    }
                } else {
                    emit(DeleteAllWalletDocumentsResult.Failure(errorMessage = restOfAllDocsDeletedFailureReason))
                }
            } ?: emit(
                DeleteAllWalletDocumentsResult.Failure(
                    errorMessage = genericErrorMessage
                )
            )
        }.safeAsync {
            DeleteAllWalletDocumentsResult.Failure(
                errorMessage = it.message ?: genericErrorMessage
            )
        }

    override fun getAllDocumentCategories(): DocumentCategories {
        return walletCoreConfig.documentCategories
    }

    override fun isDocumentsExist(): Boolean {
        return eudiWallet.getDocuments().filterIsInstance<IssuedDocument>().isNotEmpty()
    }

    override fun refreshDocuments() {
        _documentsData.update { getAllDocuments() }
    }

    override suspend fun clear() {
        eudiWallet.documentManager.getDocuments().forEach {
            eudiWallet.documentManager.deleteDocumentById(it.id)
        }
        refreshDocuments()
    }

    override suspend fun getRevokedDocuments(): List<RevokedDocument> {
        return revokedDocumentDao.retrieveAll()
    }
}
