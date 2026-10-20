package ua.govstack.digitalwallet.app.wallet_core.wallet.configs

import android.content.Context
import eu.europa.ec.eudi.wallet.EudiWalletConfig
import eu.europa.ec.eudi.wallet.document.CreateDocumentSettings.CredentialPolicy
import eu.europa.ec.eudi.wallet.logging.Logger
import eu.europa.ec.eudi.wallet.transfer.openId4vp.ClientIdScheme
import eu.europa.ec.eudi.wallet.transfer.openId4vp.Format
import ua.govstack.digitalwallet.app.wallet_core.BuildConfig
import ua.govstack.digitalwallet.app.core.util.deeplink.DeepLinkRoutes
import ua.govstack.digitalwallet.app.wallet_core.security.certificate.CertificateResourceHelper
import ua.govstack.digitalwallet.app.wallet_core.security.certificate.IssuerMetadataPolicyProvider
import ua.govstack.digitalwallet.app.wallet_core.issuance.VciConfig
import kotlin.time.Duration.Companion.seconds

internal class WalletCoreConfigImpl(
    context: Context,
    private val certificateResourceHelper: CertificateResourceHelper,
    private val issuerConfigParser: IssuerConfigParser,
    private val walletConfigValidator: WalletConfigValidator,
) : WalletCoreConfig {

    // Hold the application context only, so this singleton never retains an Activity.
    private val applicationContext = context.applicationContext

    // Separate locks keep lazy initialization simple and safe for UI + background callers.
    private val configLock = Any()
    private val issuersConfigLock = Any()

    @Volatile
    private var _config: EudiWalletConfig? = null

    @Volatile
    private var _issuersConfig: List<VciConfig>? = null

    override val config: EudiWalletConfig
        get() {
            _config?.let { return it }

            return synchronized(configLock) {
                _config ?: run {
                    val localCertificateList = certificateResourceHelper.getCertListFromAssetsDirectory()

                    EudiWalletConfig {
                        // Document keys stay hardware-backed when the device supports StrongBox.
                        configureDocumentKeyCreation(
                            userAuthenticationRequired = false,
                            userAuthenticationTimeout = 30.seconds,
                            useStrongBoxForKeys = true
                        )
                        // Debug logging is configurable per build so integrators can reduce noise in release builds.
                        configureLogging(
                            level = if (BuildConfig.WALLET_DEBUG_LOGGING) {
                                Logger.LEVEL_DEBUG
                            } else {
                                Logger.LEVEL_ERROR
                            }
                        )
                        // Reader trust is derived from certificates bundled in assets/certificates.
                        configureReaderTrustStore(
                            localCertificateList
                        )
                        // OpenID4VP formats and client ID schemes are fixed in code because
                        // they define the wallet's supported interoperability surface.
                        configureOpenId4Vp {
                            withClientIdSchemes(
                                listOf(
                                    ClientIdScheme.X509SanDns,
                                    ClientIdScheme.X509Hash
                                )
                            )
                            withSchemes(DeepLinkRoutes.openId4VpSchemes)
                            withFormats(
                                Format.MsoMdoc.ES256, Format.SdJwtVc.ES256
                            )
                        }
                    }
                }.also { _config = it }
            }
        }

    override val issuersConfig: List<VciConfig>
        get() {
            _issuersConfig?.let { return it }

            return synchronized(issuersConfigLock) {
                _issuersConfig ?: run {
                    val localCertificateList = certificateResourceHelper.getCertListFromAssetsDirectory()
                    val issuerMetadataPolicy =
                        IssuerMetadataPolicyProvider(localCertificateList).create(
                            allowUntrustedIssuers = trustHandlingConfig.allowUntrustedIssuers
                        )
                    val assetPath = BuildConfig.WALLET_ISSUERS_CONFIG_ASSET

                    walletConfigValidator.requireConfigured(assetPath) {
                        // The issuer catalog is externalized into assets so deployers can
                        // change endpoints/order without changing the wallet source code.
                        val parsedConfig = issuerConfigParser.loadFromAssets(
                            context = applicationContext,
                            fileName = assetPath,
                            issuerMetadataPolicy = issuerMetadataPolicy
                        )

                        // Fail fast with a clear configuration error before any issuance flow starts.
                        walletConfigValidator.validateIssuersConfig(
                            issuersConfig = parsedConfig,
                            assetPath = assetPath
                        )

                        parsedConfig.sortedBy { it.order }
                    }
                }.also { _issuersConfig = it }
            }
        }

    override val documentIssuanceConfig: DocumentIssuanceConfig
        get() = DocumentIssuanceConfig(
            defaultRule = DocumentIssuanceRule(
                // RotateUse is the default issuance behavior expected by the current wallet flows.
                policy = CredentialPolicy.RotateUse,
                numberOfCredentials = 1
            ),
            documentSpecificRules = mapOf()
        )

    override val trustHandlingConfig: TrustHandlingConfig
        get() = TrustHandlingConfig(
            allowUntrustedIssuers = BuildConfig.ALLOW_UNTRUSTED_ISSUERS,
            allowUntrustedVerifiers = BuildConfig.ALLOW_UNTRUSTED_VERIFIERS,
            enforceIssuerAllowList = BuildConfig.ENFORCE_ISSUER_ALLOW_LIST,
        )

    override val walletProviderHost: String
        // Used by wallet attestation flows. Must match the intended deployment environment.
        get() = BuildConfig.WALLET_PROVIDER_HOST
}
