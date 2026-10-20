package ua.govstack.digitalwallet.app.wallet_gallery.di

import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import ua.govstack.digitalwallet.app.core.di.actions.GlobalActionDeferredIssuingEvent
import ua.govstack.digitalwallet.app.core.models.common.DeferredIssuingEvent
import ua.govstack.digitalwallet.app.core.util.DispatcherProvider
import ua.govstack.digitalwallet.app.core.util.UuidProvider
import ua.govstack.digitalwallet.app.core.util.event.UiDataEvent
import ua.govstack.digitalwallet.app.diia_storage.store.datasource.preferences.PreferenceDataSource
import ua.govstack.digitalwallet.app.ui_base.provider.ResourceProvider
import ua.govstack.digitalwallet.app.wallet_core.documents.store.DocumentStore
import ua.govstack.digitalwallet.app.wallet_gallery.data.repository.DocumentsRepository
import ua.govstack.digitalwallet.app.wallet_gallery.data.repository.DocumentsRepositoryImpl
import ua.govstack.digitalwallet.app.wallet_gallery.domain.interactor.GetDocumentInteractorImpl
import ua.govstack.digitalwallet.app.wallet_gallery.domain.interactor.GetDocumentsInteractor
import ua.govstack.digitalwallet.app.wallet_gallery.domain.provider.EUDocOrderProvider
import ua.govstack.digitalwallet.app.wallet_gallery.domain.provider.EUDocOrderProviderImpl
import ua.govstack.digitalwallet.app.wallet_gallery.domain.transformer.DeferredDocumentTransformer
import ua.govstack.digitalwallet.app.wallet_gallery.domain.transformer.MdlTransformer
import ua.govstack.digitalwallet.app.wallet_gallery.domain.transformer.PidTransformer
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GalleryModule {

    @Provides
    @Singleton
    fun provideDocOrderProvider(
        preferenceDataSource: PreferenceDataSource
    ): EUDocOrderProvider {
        return EUDocOrderProviderImpl(preferenceDataSource = preferenceDataSource)
    }

    @Provides
    @Singleton
    fun providePidTransformer(
        resourceProvider: ResourceProvider,
        uuidProvider: UuidProvider
    ): PidTransformer {
        return PidTransformer(
            resourceProvider = resourceProvider,
            uuidProvider = uuidProvider
        )
    }

    @Provides
    @Singleton
    fun provideMdlTransformer(
        resourceProvider: ResourceProvider,
        uuidProvider: UuidProvider
    ): MdlTransformer {
        return MdlTransformer(
            resourceProvider = resourceProvider,
            uuidProvider = uuidProvider
        )
    }

    @Provides
    @Singleton
    fun provideDeferredDocumentTransformer(
        resourceProvider: ResourceProvider
    ): DeferredDocumentTransformer {
        return DeferredDocumentTransformer(resourceProvider = resourceProvider)
    }

    @Provides
    @Singleton
    fun provideDocumentsRepository(
        documentStore: DocumentStore,
        pidTransformer: PidTransformer,
        mdlTransformer: MdlTransformer,
        deferredDocumentTransformer: DeferredDocumentTransformer,
        docOrderProvider: EUDocOrderProvider,
    ): DocumentsRepository {
        val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
        val applicationScope = CoroutineScope(SupervisorJob() + dispatcher)
        return DocumentsRepositoryImpl(
            scope = applicationScope,
            documentStore = documentStore,
            pidTransformer = pidTransformer,
            mdlTransformer = mdlTransformer,
            deferredDocumentTransformer = deferredDocumentTransformer,
            orderProvider = docOrderProvider,
        )
    }

    @Provides
    @Singleton
    fun provideGetDocumentsInteractor(
        documentsRepository: DocumentsRepository,
        workManager: WorkManager,
        dispatcherProvider: DispatcherProvider,
        @GlobalActionDeferredIssuingEvent
        deferredIssuingEvent: MutableSharedFlow<UiDataEvent<DeferredIssuingEvent>?>,
    ): GetDocumentsInteractor {
        return GetDocumentInteractorImpl(
            documentsRepository = documentsRepository,
            workManager = workManager,
            dispatcherProvider = dispatcherProvider,
            deferredIssuingEvent = deferredIssuingEvent,
        )
    }

}
