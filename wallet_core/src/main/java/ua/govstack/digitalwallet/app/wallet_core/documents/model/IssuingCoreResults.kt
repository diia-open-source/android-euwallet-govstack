package ua.govstack.digitalwallet.app.wallet_core.documents.model

sealed class DeleteWalletDocumentResult {
    data object Success : DeleteWalletDocumentResult()
    data class Failure(val errorMessage: String) : DeleteWalletDocumentResult()
}

sealed class DeleteAllWalletDocumentsResult {
    data object Success : DeleteAllWalletDocumentsResult()
    data class Failure(val errorMessage: String) : DeleteAllWalletDocumentsResult()
}

sealed class FetchScopedDocumentsResult {
    data class Success(val documents: List<ScopedDocumentDomain>) :
        FetchScopedDocumentsResult()

    data class Failure(val errorMessage: String) : FetchScopedDocumentsResult()
}
