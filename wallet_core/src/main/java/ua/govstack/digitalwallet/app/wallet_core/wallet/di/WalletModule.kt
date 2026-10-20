package ua.govstack.digitalwallet.app.wallet_core.wallet.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import eu.europa.ec.eudi.iso18013.transfer.readerauth.ReaderTrustStore
import eu.europa.ec.eudi.wallet.EudiWallet
import eu.europa.ec.eudi.wallet.document.DocumentManager
import io.ktor.client.HttpClient
import kotlinx.io.files.Path
import org.multipaz.storage.GenericStorageEngine
import org.multipaz.storage.Storage
import org.multipaz.storage.StorageEngine
import org.multipaz.storage.android.AndroidStorage
import ua.govstack.digitalwallet.app.wallet_core.documents.store.TransactionLogStore
import ua.govstack.digitalwallet.app.wallet_core.security.attestation.WalletCoreAttestationProvider
import ua.govstack.digitalwallet.app.wallet_core.security.certificate.CertificateResourceHelper
import ua.govstack.digitalwallet.app.wallet_core.wallet.configs.IssuerConfigParser
import ua.govstack.digitalwallet.app.wallet_core.wallet.configs.WalletConfigValidator
import ua.govstack.digitalwallet.app.wallet_core.wallet.configs.WalletCoreConfig
import ua.govstack.digitalwallet.app.wallet_core.wallet.configs.WalletCoreConfigImpl
import java.io.File
import javax.inject.Singleton
import kotlin.time.ExperimentalTime

@Module
@InstallIn(SingletonComponent::class)
internal object WalletModule {
    @Provides
    @Singleton
    internal fun provideEudiWalletCore(
        @ApplicationContext context: Context,
        ktorHttpClient: HttpClient,
        storageEngine: Storage,
        customReaderTrustStore: ReaderTrustStore,
        transactionLogStore: TransactionLogStore,
        walletCoreAttestationProvider: WalletCoreAttestationProvider,
        config: WalletCoreConfig
    ): EudiWallet {
        return EudiWallet.Companion(
            context, config = config.config,
            walletProvider = walletCoreAttestationProvider
        )
        {
            withKtorHttpClientFactory { ktorHttpClient }
            withStorage(storageEngine)
            withTransactionLogger(transactionLogStore)
            withReaderTrustStore(customReaderTrustStore)
        }
    }

    @Provides
    @Singleton
    internal fun provideSecureEngine(
        @ApplicationContext context: Context,
    ): StorageEngine {
        val storageDir = File(context.noBackupFilesDir, "identity")
        if (!storageDir.exists()) {
            storageDir.mkdir()
        }
        return GenericStorageEngine(Path(storageDir.path, "testStorage.bin"))
    }

    @OptIn(ExperimentalTime::class)
    @Provides
    @Singleton
    internal fun provideStorageEngine(
        @ApplicationContext context: Context,
    ): Storage {
        return AndroidStorage(
            File(
                context.noBackupFilesDir.path,
                "storage.db"
            ).absolutePath
        ) as Storage
    }

    @Provides
    @Singleton
    internal fun provideWalletCoreConfig(
        @ApplicationContext context: Context,
        certificateResourceHelper: CertificateResourceHelper,
        issuerConfigParser: IssuerConfigParser,
        walletConfigValidator: WalletConfigValidator,
    ): WalletCoreConfig =
        WalletCoreConfigImpl(
            context = context,
            certificateResourceHelper = certificateResourceHelper,
            issuerConfigParser = issuerConfigParser,
            walletConfigValidator = walletConfigValidator,
        )

    @Provides
    @Singleton
    internal fun provideIssuerConfigParser(): IssuerConfigParser = IssuerConfigParser()

    @Provides
    @Singleton
    internal fun provideWalletConfigValidator(): WalletConfigValidator = WalletConfigValidator()

    @Provides
    @Singleton
    internal fun provideEudiDocumentManager(
        eudiWallet: EudiWallet,
    ): DocumentManager {
        return eudiWallet
    }
}
