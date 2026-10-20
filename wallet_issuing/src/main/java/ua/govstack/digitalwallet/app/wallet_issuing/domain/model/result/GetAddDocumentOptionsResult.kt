package ua.govstack.digitalwallet.app.wallet_issuing.domain.model.result

import ua.govstack.digitalwallet.app.wallet_core.documents.model.ScopedDocumentDomain

sealed class GetAddDocumentOptionsResult {
    data class Success(val options: List<ScopedDocumentDomain>) :
        GetAddDocumentOptionsResult()

    data class NoOptions(val errorMsg: String) : GetAddDocumentOptionsResult()
    data class Failure(val error: String) : GetAddDocumentOptionsResult()
}
