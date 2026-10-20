package ua.govstack.digitalwallet.app.wallet_issuing.domain.model.result

import eu.europa.ec.eudi.wallet.document.DocumentId
import ua.govstack.digitalwallet.app.wallet_core.issuance.model.DeviceAuthenticationResult
import ua.govstack.digitalwallet.app.wallet_core.documents.model.DocumentIdentifier

sealed class IssueDocumentOfferResult {
    data class Success(
        val documentIds: List<DocumentId>,
    ) : IssueDocumentOfferResult()

    data object DeferredSuccess : IssueDocumentOfferResult()

    data class Failure(val errorMessage: String) : IssueDocumentOfferResult()

    data class AlreadyExists(val type: DocumentIdentifier) : IssueDocumentOfferResult()
}
