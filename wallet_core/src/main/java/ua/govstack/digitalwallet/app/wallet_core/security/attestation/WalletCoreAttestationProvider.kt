package ua.govstack.digitalwallet.app.wallet_core.security.attestation

import eu.europa.ec.eudi.openid4vci.Nonce
import eu.europa.ec.eudi.wallet.provider.WalletAttestationsProvider
import org.multipaz.securearea.KeyInfo
import ua.govstack.digitalwallet.app.wallet_core.wallet.configs.WalletCoreConfig

interface WalletCoreAttestationProvider : WalletAttestationsProvider

class WalletCoreAttestationProviderImpl(
    private val walletCoreConfig: WalletCoreConfig,
    private val walletAttestationRepository: WalletAttestationRepository
) : WalletCoreAttestationProvider {

    override suspend fun getWalletAttestation(
        keyInfo: KeyInfo
    ): Result<String> = walletAttestationRepository.getWalletAttestation(
        baseUrl = walletCoreConfig.walletProviderHost,
        keyInfo = keyInfo.publicKey.toJwk()
    )

    override suspend fun getKeyAttestation(
        keys: List<KeyInfo>,
        nonce: Nonce?
    ): Result<String> = walletAttestationRepository.getKeyAttestation(
        baseUrl = walletCoreConfig.walletProviderHost,
        keys = keys.map { it.publicKey.toJwk() },
        nonce = nonce?.value
    )
}