package ua.govstack.digitalwallet.app.wallet_sharing.data.repository

import androidx.activity.ComponentActivity
import eu.europa.ec.eudi.iso18013.transfer.response.DisclosedDocument
import eu.europa.ec.eudi.iso18013.transfer.response.DisclosedDocuments
import eu.europa.ec.eudi.wallet.document.Document
import eu.europa.ec.eudi.wallet.document.IssuedDocument
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onSubscription
import ua.govstack.digitalwallet.app.wallet_core.documents.store.DocumentStatusStore
import ua.govstack.digitalwallet.app.wallet_core.documents.store.DocumentStore
import ua.govstack.digitalwallet.app.wallet_sharing.data.manager.SharingSessionManager
import ua.govstack.digitalwallet.app.wallet_sharing.domain.model.RequestUriConfig
import ua.govstack.digitalwallet.app.wallet_sharing.domain.model.SendRequestedDocumentsResult
import ua.govstack.digitalwallet.app.wallet_sharing.domain.model.TransferEventResult
import ua.govstack.digitalwallet.app.wallet_sharing.domain.model.WalletCoreResult
import ua.govstack.digitalwallet.app.wallet_sharing.domain.model.toDomainConfig

interface ProximityRepository {
    val verifierName: String?
    val verifierIsTrusted: Boolean?
    val events: Flow<TransferEventResult>

    fun setConfig(config: RequestUriConfig)
    fun stopPresentation()
    fun startQrEngagement(): Flow<TransferEventResult>
    fun toggleNfcEngagement(activity: ComponentActivity, toggle: Boolean)
    fun observeSentDocumentsRequest(): Flow<WalletCoreResult>
    fun sendRequestedDocuments(): SendRequestedDocumentsResult
    fun updateRequestedDocuments(documents: DisclosedDocuments)
    fun getAllIssuedDocuments(): List<IssuedDocument>
    fun getDocumentById(id: String): Document?
    fun getDisclosedDocuments(): MutableList<DisclosedDocument>?
    suspend fun isDocumentRevoked(documentId: String): Boolean
}

internal class ProximityRepositoryImpl(
    private val sharingSessionManager: SharingSessionManager,
    private val documentStore: DocumentStore,
    private val documentStatusStore: DocumentStatusStore,
) : ProximityRepository {

    override val verifierName: String?
        get() = sharingSessionManager.verifierName

    override val verifierIsTrusted: Boolean?
        get() = sharingSessionManager.verifierIsTrusted

    override val events: Flow<TransferEventResult>
        get() = sharingSessionManager.events

    override fun setConfig(config: RequestUriConfig) {
        sharingSessionManager.setConfig(config.toDomainConfig())
    }

    override fun stopPresentation() {
        sharingSessionManager.stopPresentation()
    }

    override fun startQrEngagement(): Flow<TransferEventResult> {
        return sharingSessionManager.events
            .onSubscription { sharingSessionManager.startQrEngagement() }
    }

    override fun toggleNfcEngagement(activity: ComponentActivity, toggle: Boolean) {
        sharingSessionManager.toggleNfcEngagement(activity, toggle)
    }

    override fun observeSentDocumentsRequest(): Flow<WalletCoreResult> {
        return sharingSessionManager.observeSentDocumentsRequest()
    }

    override fun sendRequestedDocuments(): SendRequestedDocumentsResult {
        return sharingSessionManager.sendRequestedDocuments()
    }

    override fun updateRequestedDocuments(documents: DisclosedDocuments) {
        sharingSessionManager.updateRequestedDocuments(documents.toMutableList())
    }

    override fun getAllIssuedDocuments(): List<IssuedDocument> {
        return documentStore.getAllIssuedDocuments()
    }

    override fun getDocumentById(id: String): Document? {
        return documentStore.getDocumentById(id)
    }

    override fun getDisclosedDocuments(): MutableList<DisclosedDocument>? {
        return sharingSessionManager.disclosedDocuments
    }

    override suspend fun isDocumentRevoked(documentId: String): Boolean {
        return documentStatusStore.isDocumentRevoked(documentId)
    }
}
