package ua.govstack.digitalwallet.app.wallet_sharing.domain.model

import eu.europa.ec.eudi.iso18013.transfer.response.DisclosedDocument
import eu.europa.ec.eudi.wallet.document.IssuedDocument
import ua.govstack.digitalwallet.app.wallet_core.documents.model.DocumentPayloadDomain

sealed class ProximitySharingRequestResult {
    data class UntrustedVerifier(
        val verifierName: String? = null,
    ) : ProximitySharingRequestResult()

    data class Success(
        val verifierName: String? = null,
        val verifierIsTrusted: Boolean,
        val documentPayloads: List<DocumentPayloadDomain>
    ) : ProximitySharingRequestResult()

    data class NoData(
        val verifierName: String? = null,
        val verifierIsTrusted: Boolean,
    ) : ProximitySharingRequestResult()

    data class Failure(val error: String) : ProximitySharingRequestResult()
    data object Disconnect : ProximitySharingRequestResult()
}

sealed class ProximitySharingLoadingResult {
    data class Failure(val error: String) : ProximitySharingLoadingResult()
    data object Success : ProximitySharingLoadingResult()
    data object RequestReadyToBeSent : ProximitySharingLoadingResult()
}

sealed class ProximitySharingSendResult {
    data class Failure(val error: String) : ProximitySharingSendResult()
    data object Success : ProximitySharingSendResult()
}

sealed class ProximitySharingSuccessResult {
    data class Success(
        val disclosedDocuments: List<DisclosedDocument>,
        val documentMap: Map<String, IssuedDocument?>
    ) : ProximitySharingSuccessResult()

    data class Failed(
        val errorMessage: String,
    ) : ProximitySharingSuccessResult()
}
