package ua.govstack.digitalwallet.app.wallet_sharing.domain.model

sealed class SharingSessionConfig {
    data class OpenId4VP(val uri: String) :
        SharingSessionConfig()

    object Ble : SharingSessionConfig()
}