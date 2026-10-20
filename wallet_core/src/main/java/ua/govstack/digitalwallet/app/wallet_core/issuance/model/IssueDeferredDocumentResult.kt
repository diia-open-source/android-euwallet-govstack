package ua.govstack.digitalwallet.app.wallet_core.issuance.model

import eu.europa.ec.eudi.wallet.document.DocumentId

sealed class IssueDeferredDocumentResult {
    data class Issued(
        val deferredDocumentData: DeferredDocumentDataDomain,
    ) : IssueDeferredDocumentResult()

    data class NotReady(
        val deferredDocumentData: DeferredDocumentDataDomain,
    ) : IssueDeferredDocumentResult()

    data class Failed(
        val documentId: DocumentId,
        val errorMessage: String,
    ) : IssueDeferredDocumentResult()

    data class Expired(
        val documentId: DocumentId,
    ) : IssueDeferredDocumentResult()
}