package ua.govstack.digitalwallet.app.wallet_core.wallet.configs

/**
 * High-level trust toggles for wallet integrations.
 *
 * These flags are intentionally grouped in one place because they directly affect
 * how strictly the wallet validates issuers and verifiers. Integrators should treat
 * them as security-sensitive configuration and review every change carefully.
 */
data class TrustHandlingConfig(
    /**
     * When true, the wallet may continue with issuer metadata that is not anchored
     * in the configured trust material.
     */
    val allowUntrustedIssuers: Boolean = false,

    /**
     * When true, the wallet may accept verifiers that are not fully trusted by the
     * configured verifier trust rules.
     */
    val allowUntrustedVerifiers: Boolean = false,

    /**
     * When true, issuance is restricted to issuers that are explicitly allowed by the
     * wallet's issuer selection and trust policy.
     */
    val enforceIssuerAllowList: Boolean = false,
)
