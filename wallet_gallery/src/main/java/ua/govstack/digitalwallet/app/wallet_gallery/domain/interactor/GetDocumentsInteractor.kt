package ua.govstack.digitalwallet.app.wallet_gallery.domain.interactor

import androidx.work.WorkManager
import eu.europa.ec.eudi.wallet.document.DocumentId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import ua.govstack.digitalwallet.app.core.di.actions.GlobalActionDeferredIssuingEvent
import ua.govstack.digitalwallet.app.core.models.common.DeferredIssuingEvent
import ua.govstack.digitalwallet.app.core.models.document.DocumentCard
import ua.govstack.digitalwallet.app.core.util.DispatcherProvider
import ua.govstack.digitalwallet.app.core.util.event.UiDataEvent
import ua.govstack.digitalwallet.app.wallet_core.documents.model.DeleteWalletDocumentResult
import ua.govstack.digitalwallet.app.wallet_core.issuance.deferred.DeferredIssuingWork
import ua.govstack.digitalwallet.app.wallet_core.revocation.RevocationWork
import ua.govstack.digitalwallet.app.wallet_gallery.data.repository.DocumentsRepository
import ua.govstack.digitalwallet.app.wallet_gallery.domain.transformer.filterValid
import ua.govstack.digitalwallet.app.wallet_gallery.domain.transformer.toDocumentCards

sealed class GetDocumentResult {
    data class Success(val documents: List<DocumentCard>) : GetDocumentResult()
    data object Empty : GetDocumentResult()
}

sealed class DeleteDocumentResult {
    data object DocumentsDeleted : DeleteDocumentResult()
    data class Failure(val errorMessage: String) : DeleteDocumentResult()
}

interface GetDocumentsInteractor  {
    val documents: Flow<GetDocumentResult>
    val deferredIssuingEvents: Flow<UiDataEvent<DeferredIssuingEvent>>
    val documentToDelete: DocumentCard?

    suspend fun fetchDocument()
    suspend fun deleteDocument(documentId: DocumentId): Flow<DeleteDocumentResult>
    fun setupRevocationWork()
}

internal class GetDocumentInteractorImpl(
    private val documentsRepository: DocumentsRepository,
    private val workManager: WorkManager,
    private val dispatcherProvider: DispatcherProvider,
    @param:GlobalActionDeferredIssuingEvent
    private val deferredIssuingEvent: MutableSharedFlow<UiDataEvent<DeferredIssuingEvent>?>,
) : GetDocumentsInteractor {

    override val documentToDelete: DocumentCard? = null

    override val documents: Flow<GetDocumentResult>
        get() = documentsRepository.documentsData
            .map { it.filterValid() }
            .map { docs -> docs.toDocumentCards() }
            .map {
                when {
                    it.isEmpty() -> GetDocumentResult.Empty
                    else -> GetDocumentResult.Success(it)
                }
            }
            .flowOn(dispatcherProvider.ioDispatcher())

    override val deferredIssuingEvents: Flow<UiDataEvent<DeferredIssuingEvent>>
        get() = deferredIssuingEvent
            .filterNotNull()


    override suspend fun fetchDocument() {
        documentsRepository.fetchDocuments()
        DeferredIssuingWork.enqueue(workManager)
    }

    override suspend fun deleteDocument(documentId: DocumentId): Flow<DeleteDocumentResult> {
        return documentsRepository.deleteDocument(documentId).map { result ->
            when (result) {
                is DeleteWalletDocumentResult.Success -> DeleteDocumentResult.DocumentsDeleted
                is DeleteWalletDocumentResult.Failure ->
                    DeleteDocumentResult.Failure(result.errorMessage)
            }
        }.flowOn(dispatcherProvider.ioDispatcher())
    }

    override fun setupRevocationWork() {
        RevocationWork.enqueue(
            workManager
        )
    }
}
