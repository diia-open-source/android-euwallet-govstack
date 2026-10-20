package ua.govstack.digitalwallet.app.wallet_core.wallet.configs

import ua.govstack.digitalwallet.app.wallet_core.issuance.VciConfig

internal class WalletConfigValidator {

    fun validateIssuersConfig(
        issuersConfig: List<VciConfig>,
        assetPath: String,
    ) {
        require(issuersConfig.isNotEmpty()) {
            "Issuer config asset '$assetPath' must define at least one issuer"
        }

        val duplicateOrders = issuersConfig.groupBy { it.order }.filterValues { it.size > 1 }.keys
        require(duplicateOrders.isEmpty()) {
            "Issuer config asset '$assetPath' contains duplicate order values: $duplicateOrders"
        }

        val blankUrls = issuersConfig
            .filter { it.config.issuerUrl.isBlank() }
            .map { it.order }
        require(blankUrls.isEmpty()) {
            "Issuer config asset '$assetPath' contains blank issuer URLs for orders: $blankUrls"
        }
    }

    fun <T> requireConfigured(
        assetPath: String,
        block: () -> T,
    ): T = try {
        block()
    } catch (exception: WalletConfigurationException) {
        throw exception
    } catch (exception: IllegalArgumentException) {
        throw WalletConfigurationException(
            message = "Wallet configuration is invalid for asset '$assetPath'",
            cause = exception
        )
    }
}
