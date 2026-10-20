package ua.govstack.digitalwallet.app.wallet_issuing.di

import androidx.work.WorkManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import ua.govstack.digitalwallet.app.core.util.DispatcherProvider
import ua.govstack.digitalwallet.app.wallet_core.documents.store.DocumentStore
import ua.govstack.digitalwallet.app.wallet_core.issuance.DocumentIssuanceManager
import ua.govstack.digitalwallet.app.wallet_core.issuance.deferred.DeferredIssuingResultHandler
import ua.govstack.digitalwallet.app.wallet_issuing.data.repository.IssuingRepository
import ua.govstack.digitalwallet.app.wallet_issuing.data.repository.IssuingRepositoryImpl
import ua.govstack.digitalwallet.app.wallet_issuing.data.worker.WalletDeferredIssuingResultHandler
import ua.govstack.digitalwallet.app.wallet_issuing.domain.interactor.DocumentOfferInteractor
import ua.govstack.digitalwallet.app.wallet_issuing.domain.interactor.DocumentOfferInteractorImpl
import ua.govstack.digitalwallet.app.wallet_issuing.domain.interactor.IssueDocumentsInteractor
import ua.govstack.digitalwallet.app.wallet_issuing.domain.interactor.IssueDocumentsInteractorImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object IssuingModule {

    @Provides
    @Singleton
    fun provideIssuingRepository(
        documentStore: DocumentStore,
        documentIssuanceManager: DocumentIssuanceManager,
        dispatcherProvider: DispatcherProvider,
        workManager: WorkManager
    ): IssuingRepository =
        IssuingRepositoryImpl(
            documentStore = documentStore,
            documentIssuanceManager = documentIssuanceManager,
            dispatcherProvider = dispatcherProvider,
            workManager = workManager
        )

    @Provides
    fun provideDocumentOfferInteractor(
        issuingRepository: IssuingRepository,
    ): DocumentOfferInteractor {
        return DocumentOfferInteractorImpl(issuingRepository = issuingRepository)
    }

    @Provides
    fun provideIssueDocumentsInteractor(
        issuingRepository: IssuingRepository,
    ): IssueDocumentsInteractor {
        return IssueDocumentsInteractorImpl(
            issuingRepository = issuingRepository,
        )
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class IssuingDeferredDispatchModule {
    @Binds
    @IntoSet
    abstract fun bindDeferredIssuingResultHandler(
        handler: WalletDeferredIssuingResultHandler,
    ): DeferredIssuingResultHandler
}
