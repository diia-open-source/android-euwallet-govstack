package ua.govstack.digitalwallet.app.wallet_issuing.domain.model.result

import eu.europa.ec.eudi.wallet.document.DocumentId
import ua.govstack.digitalwallet.app.wallet_core.issuance.model.DeviceAuthenticationResult

sealed class IssueDocumentsResult {
    data class Success(
        val documentIds: List<DocumentId>,
    ) : IssueDocumentsResult()

    data object DeferredSuccess : IssueDocumentsResult()

    data class Failure(val errorMessage: String) : IssueDocumentsResult()
}
