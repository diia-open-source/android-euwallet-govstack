package ua.govstack.digitalwallet.app.wallet_transaction.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ua.govstack.digitalwallet.app.core.util.DispatcherProvider
import ua.govstack.digitalwallet.app.wallet_core.documents.store.DocumentStore
import ua.govstack.digitalwallet.app.wallet_core.database.dao.TransactionLogDao
import ua.govstack.digitalwallet.app.wallet_transaction.data.repository.TransactionsRepository
import ua.govstack.digitalwallet.app.wallet_transaction.data.repository.TransactionsRepositoryImpl
import ua.govstack.digitalwallet.app.wallet_transaction.domain.interactor.TransactionDetailsInteractor
import ua.govstack.digitalwallet.app.wallet_transaction.domain.interactor.TransactionDetailsInteractorImpl
import ua.govstack.digitalwallet.app.wallet_transaction.domain.interactor.TransactionsInteractor
import ua.govstack.digitalwallet.app.wallet_transaction.domain.interactor.TransactionsInteractorImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TransactionsModule {

    @Provides
    @Singleton
    fun provideTransactionsRepository(
        documentStore: DocumentStore,
        dispatcherProvider: DispatcherProvider,
        transactionDao: TransactionLogDao
    ): TransactionsRepository {
        return TransactionsRepositoryImpl(
            documentStore = documentStore,
            dispatcherProvider = dispatcherProvider,
            transactionsDao = transactionDao
        )
    }

    @Provides
    fun provideTransactionInteractor(
        transactionsRepository: TransactionsRepository,
    ): TransactionsInteractor = TransactionsInteractorImpl(transactionsRepository)

    @Provides
    fun provideTransactionDetailsInteractor(
        transactionsRepository: TransactionsRepository,
    ): TransactionDetailsInteractor = TransactionDetailsInteractorImpl(transactionsRepository)

}
