package ua.govstack.digitalwallet.app.wallet_sharing.domain.model

import eu.europa.ec.eudi.iso18013.transfer.response.DisclosedDocument
import eu.europa.ec.eudi.wallet.document.IssuedDocument
import ua.govstack.digitalwallet.app.wallet_core.documents.model.DocumentPayloadDomain
import java.net.URI

sealed class RemoteSharingRequestResult {
    data class UntrustedVerifier(
        val verifierName: String?,
    ) : RemoteSharingRequestResult()

    data class Success(
        val verifierName: String?,
        val verifierIsTrusted: Boolean,
        val documentPayloads: List<DocumentPayloadDomain>
    ) : RemoteSharingRequestResult()

    data class NoData(
        val verifierName: String?,
        val verifierIsTrusted: Boolean,
    ) : RemoteSharingRequestResult()

    data class Failure(val error: String) : RemoteSharingRequestResult()
    data object Disconnect : RemoteSharingRequestResult()
}

sealed class RemoteSharingLoadingResult {
    data class Failure(val error: String) : RemoteSharingLoadingResult()
    data object Success : RemoteSharingLoadingResult()
    data class Redirect(val uri: URI) : RemoteSharingLoadingResult()
    data object RequestReadyToBeSent : RemoteSharingLoadingResult()
}

sealed class RemoteSharingSendResult {
    data class Failure(val error: String) : RemoteSharingSendResult()
    data object Success : RemoteSharingSendResult()
}

sealed class RemoteSharingSuccessResult {
    data class Success(
        val disclosedDocuments: List<DisclosedDocument>,
        val documentMap: Map<String, IssuedDocument?>
    ) : RemoteSharingSuccessResult()

    data class Failed(val errorMessage: String) : RemoteSharingSuccessResult()
}
