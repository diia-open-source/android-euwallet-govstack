package ua.govstack.digitalwallet.app.wallet_core.security.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import eu.europa.ec.eudi.iso18013.transfer.readerauth.ReaderTrustStore
import io.ktor.client.HttpClient
import kotlinx.coroutines.runBlocking
import org.multipaz.securearea.SecureArea
import org.multipaz.securearea.software.SoftwareSecureArea
import org.multipaz.storage.Storage
import ua.govstack.digitalwallet.app.wallet_core.wallet.configs.WalletCoreConfig
import ua.govstack.digitalwallet.app.wallet_core.security.attestation.WalletAttestationRepository
import ua.govstack.digitalwallet.app.wallet_core.security.attestation.WalletAttestationRepositoryImpl
import ua.govstack.digitalwallet.app.wallet_core.security.attestation.WalletCoreAttestationProvider
import ua.govstack.digitalwallet.app.wallet_core.security.attestation.WalletCoreAttestationProviderImpl
import ua.govstack.digitalwallet.app.wallet_core.security.certificate.CertificateResourceHelper
import ua.govstack.digitalwallet.app.wallet_core.security.certificate.CertificateResourceHelperImpl
import ua.govstack.digitalwallet.app.wallet_core.security.certificate.CustomReaderTrustStore
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {

    @Provides
    @Singleton
    fun provideCertificateResourceHelper(
        @ApplicationContext context: Context,
    ): CertificateResourceHelper = CertificateResourceHelperImpl(context)

    @Provides
    @Singleton
    fun provideCustomReaderTrustStore(
        certificateResourceHelper: CertificateResourceHelper,
    ): ReaderTrustStore = CustomReaderTrustStore(
        trustedCertificates = certificateResourceHelper.getCertListFromAssetsDirectory()
    )

    @Provides
    @Singleton
    fun provideSecureArea(storageEngine: Storage): SecureArea =
        runBlocking { SoftwareSecureArea.create(storageEngine) }

    @Provides
    @Singleton
    fun provideWalletAttestationRepository(
        ktorHttpClient: HttpClient,
    ): WalletAttestationRepository = WalletAttestationRepositoryImpl(ktorHttpClient)

    @Provides
    @Singleton
    fun provideWalletCoreAttestationProvider(
        walletCoreConfig: WalletCoreConfig,
        walletAttestationRepository: WalletAttestationRepository,
    ): WalletCoreAttestationProvider = WalletCoreAttestationProviderImpl(
        walletCoreConfig,
        walletAttestationRepository,
    )
}
