package ua.govstack.digitalwallet.app.wallet_core.wallet.configs

import eu.europa.ec.eudi.wallet.EudiWalletConfig
import eu.europa.ec.eudi.wallet.document.CreateDocumentSettings.CredentialPolicy
import eu.europa.ec.eudi.wallet.issue.openid4vci.OpenId4VciManager
import ua.govstack.digitalwallet.app.wallet_core.documents.model.DocumentCategories
import ua.govstack.digitalwallet.app.wallet_core.documents.model.DocumentIdentifier
import ua.govstack.digitalwallet.app.wallet_core.issuance.VciConfig

interface WalletCoreConfig {


    /**
     * Holds the configuration settings for the EudiWallet.
     * This configuration includes settings such as API endpoints, cryptographic parameters,
     * and storage locations.
     *
     * Integrators normally do not construct this manually. Instead, they provide the
     * environment-specific inputs and let [WalletCoreConfigImpl] assemble the final SDK config.
     */
    val config: EudiWalletConfig

    /**
     * A list of configurations for Verifiable Credentials Issuance (VCI) using OpenID4VCI.
     *
     * Each element in the list is an [VciConfig] object, which contains:
     * - The [OpenId4VciManager.Config] required to communicate with a specific issuer.
     * - An `order` property to determine the display order of the issuer.
     *
     * In this project the list is sourced from a packaged JSON asset so integrators can
     * update issuer endpoints without editing Kotlin code.
     */
    val issuersConfig: List<VciConfig>

    /**
     * Returns a predefined set of document categories and their associated identifiers.
     *
     * This property provides a structured mapping of common document categories (e.g., Government, Travel, Finance)
     * to specific document identifiers.  These identifiers are used to uniquely identify different types of
     * documents within a given category and are based on standard formats and specifications.
     *
     * The structure is a  lists of [DocumentIdentifier] objects, each representing a specific document type within that category.
     *
     */
    val documentCategories: DocumentCategories
        get() = DocumentCategories(
            value = listOf(
                    DocumentIdentifier.MdocPid,
                    DocumentIdentifier.SdJwtPid,
                    DocumentIdentifier.MdocMdl
                )
        )

    /**
     * Configuration for document issuance, including default rules and specific overrides.
     *
     * This property defines the behavior for how many credentials of each document type are issued
     * and their usage policy (e.g., rotate use, one-time use).
     *
     * It consists of:
     * - `defaultRule`: A [DocumentIssuanceRule] that applies to all document types unless overridden.
     *   By default, it uses [CredentialPolicy.RotateUse] and issues 1 credential.
     * - `documentSpecificRules`: A map allowing overrides for specific document types.
     *   - Keys are [DocumentIdentifier] objects representing specific document types.
     *   - Values are [DocumentIssuanceRule] objects defining the policy and number of credentials for that document.
     *
     * For example:
     * - [DocumentIdentifier.MdocPid] is configured for [CredentialPolicy.OneTimeUse] with 10 credentials.
     * - [DocumentIdentifier.SdJwtPid] is configured for [CredentialPolicy.OneTimeUse] with 10 credentials.
     *
     * Any document type not listed in `documentSpecificRules` will use the `defaultRule`.
     */
    val documentIssuanceConfig: DocumentIssuanceConfig

    /**
     * High-level policy for handling untrusted ecosystem parties.
     *
     * This is one of the most security-sensitive parts of the wallet configuration.
     */
    val trustHandlingConfig: TrustHandlingConfig
        get() = TrustHandlingConfig()

    /**
     * Host for the Wallet Provider.
     *
     * This base URL is used by attestation-related flows and should point to the
     * deployment environment that matches the packaged issuer and trust configuration.
     */
    val walletProviderHost: String
}
