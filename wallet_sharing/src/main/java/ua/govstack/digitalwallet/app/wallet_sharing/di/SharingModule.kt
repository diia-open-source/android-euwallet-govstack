package ua.govstack.digitalwallet.app.wallet_sharing.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import eu.europa.ec.eudi.wallet.EudiWallet
import ua.govstack.digitalwallet.app.core.util.UuidProvider
import ua.govstack.digitalwallet.app.ui_base.provider.ResourceProvider
import ua.govstack.digitalwallet.app.wallet_core.documents.store.DocumentStatusStore
import ua.govstack.digitalwallet.app.wallet_core.documents.store.DocumentStore
import ua.govstack.digitalwallet.app.wallet_core.wallet.configs.WalletCoreConfig
import ua.govstack.digitalwallet.app.wallet_sharing.data.manager.SharingSessionManager
import ua.govstack.digitalwallet.app.wallet_sharing.data.manager.SharingSessionManagerImpl
import ua.govstack.digitalwallet.app.wallet_sharing.data.repository.PresentationRepository
import ua.govstack.digitalwallet.app.wallet_sharing.data.repository.PresentationRepositoryImpl
import ua.govstack.digitalwallet.app.wallet_sharing.data.repository.ProximityRepository
import ua.govstack.digitalwallet.app.wallet_sharing.data.repository.ProximityRepositoryImpl
import ua.govstack.digitalwallet.app.wallet_sharing.domain.interactor.ProximityQRInteractor
import ua.govstack.digitalwallet.app.wallet_sharing.domain.interactor.ProximityQRInteractorImpl
import ua.govstack.digitalwallet.app.wallet_sharing.domain.interactor.ProximitySharingInteractor
import ua.govstack.digitalwallet.app.wallet_sharing.domain.interactor.ProximitySharingInteractorImpl
import ua.govstack.digitalwallet.app.wallet_sharing.domain.interactor.RemoteSharingInteractor
import ua.govstack.digitalwallet.app.wallet_sharing.domain.interactor.RemoteSharingInteractorImpl

@Module
@InstallIn(PresentationFeatureComponent::class)
object SharingModule {

    @Provides
    @PresentationFeatureScope
    fun provideSharingSessionManager(
        resourceProvides: ResourceProvider,
        eudiWallet: EudiWallet,
    ): SharingSessionManager {
        return SharingSessionManagerImpl(
            eudiWallet = eudiWallet,
            resourceProvider = resourceProvides,
        )
    }

    @PresentationFeatureScope
    @Provides
    fun provideProximityRepository(
        sharingSessionManager: SharingSessionManager,
        documentStore: DocumentStore,
        documentStatusStore: DocumentStatusStore,
    ): ProximityRepository =
        ProximityRepositoryImpl(
            sharingSessionManager = sharingSessionManager,
            documentStore = documentStore,
            documentStatusStore = documentStatusStore,
        )

    @PresentationFeatureScope
    @Provides
    fun provideProximityQRInteractor(
        proximityRepository: ProximityRepository,
    ): ProximityQRInteractor =
        ProximityQRInteractorImpl(
            repository = proximityRepository
        )

    @PresentationFeatureScope
    @Provides
    fun provideProximityInteractor(
        resourceProvider: ResourceProvider,
        uuidProvider: UuidProvider,
        proximityRepository: ProximityRepository,
        walletCoreConfig: WalletCoreConfig,
    ): ProximitySharingInteractor =
        ProximitySharingInteractorImpl(
            resourceProvider = resourceProvider,
            uuidProvider = uuidProvider,
            repository = proximityRepository,
            walletCoreConfig = walletCoreConfig,
        )

    @PresentationFeatureScope
    @Provides
    fun providePresentationRepository(
        sharingSessionManager: SharingSessionManager,
        documentStore: DocumentStore,
        documentStatusStore: DocumentStatusStore,
    ): PresentationRepository =
        PresentationRepositoryImpl(
            sharingSessionManager = sharingSessionManager,
            documentStore = documentStore,
            documentStatusStore = documentStatusStore,
        )

    @PresentationFeatureScope
    @Provides
    fun providePresentationInteractor(
        resourceProvider: ResourceProvider,
        uuidProvider: UuidProvider,
        repository: PresentationRepository,
        walletCoreConfig: WalletCoreConfig,
    ): RemoteSharingInteractor =
        RemoteSharingInteractorImpl(
            resourceProvider = resourceProvider,
            uuidProvider = uuidProvider,
            repository = repository,
            walletCoreConfig = walletCoreConfig,
        )

}
