package ua.govstack.digitalwallet.app.wallet_core.issuance.model

import eu.europa.ec.eudi.wallet.document.DocumentId
import ua.govstack.digitalwallet.app.wallet_core.documents.model.FormatType

sealed class IssueDocumentsResult {
    data class Success(val documentIds: List<DocumentId>) : IssueDocumentsResult()
    data class DeferredSuccess(val deferredDocuments: Map<DocumentId, FormatType>) :
        IssueDocumentsResult()

    data class PartialSuccess(
        val documentIds: List<DocumentId>,
        val nonIssuedDocuments: Map<String, String>,
    ) : IssueDocumentsResult()

    data class Failure(val errorMessage: String) : IssueDocumentsResult()
}