package ua.govstack.digitalwallet.app.wallet_core.documents.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import eu.europa.ec.eudi.wallet.EudiWallet
import ua.govstack.digitalwallet.app.core.util.UuidProvider
import ua.govstack.digitalwallet.app.wallet_core.database.dao.RevokedDocumentDao
import ua.govstack.digitalwallet.app.wallet_core.database.dao.TransactionLogDao
import ua.govstack.digitalwallet.app.wallet_core.documents.store.DocumentStatusStore
import ua.govstack.digitalwallet.app.wallet_core.documents.store.DocumentStatusStoreImpl
import ua.govstack.digitalwallet.app.wallet_core.documents.store.DocumentStore
import ua.govstack.digitalwallet.app.wallet_core.documents.store.DocumentStoreImpl
import ua.govstack.digitalwallet.app.wallet_core.documents.store.TransactionLogStore
import ua.govstack.digitalwallet.app.wallet_core.documents.store.TransactionLogStoreImpl
import ua.govstack.digitalwallet.app.wallet_core.wallet.configs.WalletCoreConfig
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DocumentsModule {
    @Provides
    @Singleton
    fun provideDocumentStore(
        eudiWallet: EudiWallet,
        walletCoreConfig: WalletCoreConfig,
        revokedDocumentDao: RevokedDocumentDao,
    ): DocumentStore {
        return DocumentStoreImpl(
            eudiWallet = eudiWallet,
            walletCoreConfig = walletCoreConfig,
            revokedDocumentDao = revokedDocumentDao,
        )
    }

    @Provides
    @Singleton
    fun provideDocumentStatusStore(
        eudiWallet: EudiWallet,
        revokedDocumentDao: RevokedDocumentDao,
    ): DocumentStatusStore {
        return DocumentStatusStoreImpl(
            eudiWallet = eudiWallet,
            revokedDocumentDao = revokedDocumentDao,
        )
    }

    @Provides
    @Singleton
    fun provideTransactionLogStore(
        transactionLogDao: TransactionLogDao,
        uuidProvider: UuidProvider
    ): TransactionLogStore {
        return TransactionLogStoreImpl(
            transactionLogDao = transactionLogDao,
            uuidProvider = uuidProvider
        )
    }
}