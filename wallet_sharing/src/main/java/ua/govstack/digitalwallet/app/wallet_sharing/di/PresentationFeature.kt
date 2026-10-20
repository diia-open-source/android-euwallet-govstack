package ua.govstack.digitalwallet.app.wallet_sharing.di


import dagger.hilt.DefineComponent
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ua.govstack.digitalwallet.app.wallet_sharing.domain.interactor.ProximityQRInteractor
import ua.govstack.digitalwallet.app.wallet_sharing.domain.interactor.ProximitySharingInteractor
import ua.govstack.digitalwallet.app.wallet_sharing.domain.interactor.RemoteSharingInteractor
import javax.inject.Scope

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class PresentationFeatureScope

@PresentationFeatureScope
@DefineComponent(parent = SingletonComponent::class)
interface PresentationFeatureComponent {
    @DefineComponent.Builder
    interface Builder {
        fun build(): PresentationFeatureComponent
    }
}

@EntryPoint
@InstallIn(PresentationFeatureComponent::class)
interface PresentationFeatureEntryPoint {

    fun proximityQRInteractor(): ProximityQRInteractor
    fun proximitySharingInteractor(): ProximitySharingInteractor
    fun remoteSharingInteractor(): RemoteSharingInteractor


}