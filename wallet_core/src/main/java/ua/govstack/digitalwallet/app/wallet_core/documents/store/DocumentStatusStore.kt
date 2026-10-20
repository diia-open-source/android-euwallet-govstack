package ua.govstack.digitalwallet.app.wallet_core.documents.store

import eu.europa.ec.eudi.statium.Status
import eu.europa.ec.eudi.wallet.EudiWallet
import eu.europa.ec.eudi.wallet.document.CreateDocumentSettings
import eu.europa.ec.eudi.wallet.document.IssuedDocument
import ua.govstack.digitalwallet.app.wallet_core.database.dao.RevokedDocumentDao

interface DocumentStatusStore {
    suspend fun getRevokedDocumentIds(): List<String>

    suspend fun isDocumentRevoked(id: String): Boolean

    suspend fun resolveDocumentStatus(document: IssuedDocument): Result<Status>

    suspend fun isDocumentLowOnCredentials(document: IssuedDocument): Boolean
}

internal class DocumentStatusStoreImpl(
    private val eudiWallet: EudiWallet,
    private val revokedDocumentDao: RevokedDocumentDao,
) : DocumentStatusStore {

    override suspend fun isDocumentLowOnCredentials(document: IssuedDocument): Boolean {
        val documentRemainingCredentials = document.credentialsCount()

        return document.credentialPolicy == CreateDocumentSettings.CredentialPolicy.OneTimeUse
                && documentRemainingCredentials <= 1
    }

    override suspend fun getRevokedDocumentIds(): List<String> =
        revokedDocumentDao.retrieveAll().map { it.identifier }

    override suspend fun isDocumentRevoked(id: String): Boolean =
        revokedDocumentDao.retrieve(id) != null

    override suspend fun resolveDocumentStatus(document: IssuedDocument): Result<Status> =
        eudiWallet.resolveStatus(document)
}
