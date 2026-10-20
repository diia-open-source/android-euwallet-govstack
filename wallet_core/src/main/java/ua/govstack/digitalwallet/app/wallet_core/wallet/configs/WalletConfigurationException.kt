package ua.govstack.digitalwallet.app.wallet_core.wallet.configs

internal class WalletConfigurationException(
    message: String,
    cause: Throwable? = null,
) : IllegalStateException(message, cause)
