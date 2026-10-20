package ua.govstack.digitalwallet.app.wallet_sharing.domain.model

import eu.europa.ec.eudi.iso18013.transfer.response.RequestedDocument
import java.net.URI

sealed class TransferEventResult {
    data object Connected : TransferEventResult()
    data object Connecting : TransferEventResult()
    data object Disconnected : TransferEventResult()
    data class Error(val error: String) : TransferEventResult()
    data class QrEngagementReady(val qrCode: String) : TransferEventResult()
    data class RequestReceived(
        val requestData: List<RequestedDocument>,
        val verifierName: String?,
        val verifierIsTrusted: Boolean,
    ) : TransferEventResult()

    data object ResponseSent : TransferEventResult()
    data class Redirect(val uri: URI) : TransferEventResult()
    data object IntentToSend : TransferEventResult()
}

sealed class CheckKeyUnlockResult {
    data class Failure(val error: String) : CheckKeyUnlockResult()
    data object RequestIsReadyToBeSent : CheckKeyUnlockResult()
}

sealed class SendRequestedDocumentsResult {
    data class Failure(val error: String) : SendRequestedDocumentsResult()
    data object RequestSent : SendRequestedDocumentsResult()
}

sealed class ResponseReceivedResult {
    data object Success : ResponseReceivedResult()
    data class Redirect(val uri: URI) : ResponseReceivedResult()
    data class Failure(val error: String) : ResponseReceivedResult()
}

sealed class WalletCoreResult {
    data class Failure(val error: String) : WalletCoreResult()
    data object Success : WalletCoreResult()
    data class Redirect(val uri: URI) : WalletCoreResult()
    data object RequestIsReadyToBeSent : WalletCoreResult()
}
