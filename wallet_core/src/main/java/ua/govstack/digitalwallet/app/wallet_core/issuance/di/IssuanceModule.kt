package ua.govstack.digitalwallet.app.wallet_core.issuance.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import eu.europa.ec.eudi.wallet.EudiWallet
import kotlinx.coroutines.asCoroutineDispatcher
import ua.govstack.digitalwallet.app.wallet_core.documents.store.DocumentStore
import ua.govstack.digitalwallet.app.wallet_core.wallet.configs.WalletCoreConfig
import ua.govstack.digitalwallet.app.wallet_core.issuance.DocumentIssuanceManager
import ua.govstack.digitalwallet.app.wallet_core.issuance.DocumentIssuanceManagerImpl
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object IssuanceModule {
    @Provides
    @Singleton
    fun provideWalletIssuingService(
        eudiWallet: EudiWallet,
        walletCoreConfig: WalletCoreConfig,
        documentStore: DocumentStore,
    ): DocumentIssuanceManager {
        return DocumentIssuanceManagerImpl(
            eudiWallet = eudiWallet,
            walletCoreConfig = walletCoreConfig,
            documentStore = documentStore,
            dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher(),
        )
    }
}