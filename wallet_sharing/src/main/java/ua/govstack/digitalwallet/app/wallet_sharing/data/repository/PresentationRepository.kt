package ua.govstack.digitalwallet.app.wallet_sharing.data.repository

import eu.europa.ec.eudi.iso18013.transfer.response.DisclosedDocument
import eu.europa.ec.eudi.iso18013.transfer.response.DisclosedDocuments
import eu.europa.ec.eudi.wallet.document.Document
import eu.europa.ec.eudi.wallet.document.IssuedDocument
import kotlinx.coroutines.flow.Flow
import ua.govstack.digitalwallet.app.wallet_sharing.domain.model.SendRequestedDocumentsResult
import ua.govstack.digitalwallet.app.wallet_sharing.domain.model.TransferEventResult
import ua.govstack.digitalwallet.app.wallet_core.documents.store.DocumentStatusStore
import ua.govstack.digitalwallet.app.wallet_core.documents.store.DocumentStore
import ua.govstack.digitalwallet.app.wallet_sharing.domain.model.RequestUriConfig
import ua.govstack.digitalwallet.app.wallet_sharing.domain.model.toDomainConfig
import ua.govstack.digitalwallet.app.wallet_sharing.domain.model.WalletCoreResult
import ua.govstack.digitalwallet.app.wallet_sharing.data.manager.SharingSessionManager
import java.net.URI

interface PresentationRepository {
    val events: Flow<TransferEventResult>
    val verifierName: String?
    val verifierIsTrusted: Boolean?
    val redirectUri: URI?

    fun observeSentDocumentsRequest(): Flow<WalletCoreResult>
    fun sendRequestedDocuments(): SendRequestedDocumentsResult
    fun updateRequestedDocuments(documents: DisclosedDocuments)
    fun setConfig(config: RequestUriConfig)
    fun stopPresentation()
    fun getAllIssuedDocuments(): List<IssuedDocument>
    fun getDocumentById(id: String): Document?
    fun getDisclosedDocuments(): MutableList<DisclosedDocument>?
    suspend fun isDocumentRevoked(id: String): Boolean
}

internal class PresentationRepositoryImpl(
    private val sharingSessionManager: SharingSessionManager,
    private val documentStore: DocumentStore,
    private val documentStatusStore: DocumentStatusStore,
) : PresentationRepository {

    override val events: Flow<TransferEventResult>
        get() = sharingSessionManager.events

    override val verifierName: String?
        get() = sharingSessionManager.verifierName

    override val verifierIsTrusted: Boolean?
        get() = sharingSessionManager.verifierIsTrusted

    override val redirectUri: URI?
        get() = sharingSessionManager.redirectUri

    override fun observeSentDocumentsRequest(): Flow<WalletCoreResult> {
        return sharingSessionManager.observeSentDocumentsRequest()
    }

    override fun sendRequestedDocuments(): SendRequestedDocumentsResult {
        return sharingSessionManager.sendRequestedDocuments()
    }

    override fun updateRequestedDocuments(documents: DisclosedDocuments) {
        sharingSessionManager.updateRequestedDocuments(documents.toMutableList())
    }

    override fun setConfig(config: RequestUriConfig) {
        sharingSessionManager.setConfig(config.toDomainConfig())
    }

    override fun stopPresentation() {
        sharingSessionManager.stopPresentation()
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

    override suspend fun isDocumentRevoked(id: String): Boolean {
        return documentStatusStore.isDocumentRevoked(id)
    }
}
