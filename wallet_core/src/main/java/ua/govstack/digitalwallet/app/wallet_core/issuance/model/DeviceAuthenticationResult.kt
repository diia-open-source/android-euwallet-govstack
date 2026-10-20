package ua.govstack.digitalwallet.app.wallet_core.issuance.model

data class DeviceAuthenticationResult(
    val onAuthenticationSuccess: suspend () -> Unit = {},
    val onAuthenticationError: () -> Unit = {},
    val onAuthenticationFailure: () -> Unit = {},
)