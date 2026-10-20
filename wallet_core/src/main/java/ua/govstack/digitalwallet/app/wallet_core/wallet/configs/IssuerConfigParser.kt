package ua.govstack.digitalwallet.app.wallet_core.wallet.configs

import android.content.Context
import eu.europa.ec.eudi.openid4vci.IssuerMetadataPolicy
import eu.europa.ec.eudi.wallet.issue.openid4vci.OpenId4VciManager
import eu.europa.ec.eudi.wallet.issue.openid4vci.dpop.DPopConfig
import kotlinx.serialization.SerializationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import ua.govstack.digitalwallet.app.wallet_core.issuance.VciConfig
import java.io.FileNotFoundException

@Serializable
internal data class IssuerConfigDto(
    val order: Int,
    val issuerUrl: String,
    val clientAuthenticationType: String,
    val clientId: String? = null,
    val authFlowRedirectionURI: String,
    val usePar: String,
    val logIncompleteDeferredIssuance: Boolean,
)

internal class IssuerConfigParser(
    private val json: Json = Json {
        // Allow forward-compatible config files with extra fields added over time.
        ignoreUnknownKeys = true
        // Coerce unexpected nullable/defaultable values when it is safe to do so.
        coerceInputValues = true
    }
) {

    fun loadFromAssets(
        context: Context,
        fileName: String,
        issuerMetadataPolicy: IssuerMetadataPolicy,
    ): List<VciConfig> {
        try {
            // Read the packaged issuer catalog from assets; this file is part of the app bundle.
            val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
            val parsedDtos = json.decodeFromString<List<IssuerConfigDto>>(jsonString)

            return parsedDtos.map { dto ->
                VciConfig(
                    config = OpenId4VciManager.Config.Builder()
                        .withIssuerUrl(dto.issuerUrl)
                        // Client authentication strategy must match the issuer integration contract.
                        .withClientAuthenticationType(dto.toClientAuthenticationType())
                        .withAuthFlowRedirectionURI(dto.authFlowRedirectionURI)
                        .withParUsage(dto.usePar.toParUsage())
                        .withDPopConfig(DPopConfig.Default)
                        .withIssuerMetadataPolicy(issuerMetadataPolicy)
                        .withLogIncompleteDeferredIssuance(dto.logIncompleteDeferredIssuance)
                        .build(),
                    order = dto.order
                )
            }
        } catch (exception: FileNotFoundException) {
            throw WalletConfigurationException(
                message = "Wallet issuer config asset '$fileName' was not found in the application package",
                cause = exception
            )
        } catch (exception: SerializationException) {
            throw WalletConfigurationException(
                message = "Wallet issuer config asset '$fileName' contains invalid JSON or incompatible fields",
                cause = exception
            )
        } catch (exception: IllegalArgumentException) {
            throw WalletConfigurationException(
                message = "Wallet issuer config asset '$fileName' contains unsupported or incomplete issuer entries",
                cause = exception
            )
        }
    }

    private fun IssuerConfigDto.toClientAuthenticationType(): OpenId4VciManager.ClientAuthenticationType =
        when (clientAuthenticationType) {
            "AttestationBased" -> OpenId4VciManager.ClientAuthenticationType.AttestationBased
            "None" -> OpenId4VciManager.ClientAuthenticationType.None(
                // Non-attested integrations must explicitly declare the clientId in JSON.
                clientId = requireNotNull(clientId) {
                    "clientId is required when clientAuthenticationType is None"
                }
            )
            else -> error("Unsupported clientAuthenticationType value: $clientAuthenticationType")
        }

    @OpenId4VciManager.Config.ParUsage
    private fun String.toParUsage(): Int =
        when (this) {
            "IF_SUPPORTED" -> OpenId4VciManager.Config.ParUsage.IF_SUPPORTED
            "NEVER" -> OpenId4VciManager.Config.ParUsage.NEVER
            "REQUIRED" -> OpenId4VciManager.Config.ParUsage.REQUIRED
            else -> error("Unsupported usePar value: $this")
        }
}
