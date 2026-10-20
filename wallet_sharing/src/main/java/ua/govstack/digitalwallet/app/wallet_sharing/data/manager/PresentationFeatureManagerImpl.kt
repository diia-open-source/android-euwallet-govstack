package ua.govstack.digitalwallet.app.wallet_sharing.data.manager

import dagger.hilt.EntryPoints
import ua.govstack.digitalwallet.app.wallet_sharing.di.PresentationFeatureComponent
import ua.govstack.digitalwallet.app.wallet_sharing.di.PresentationFeatureEntryPoint
import ua.govstack.digitalwallet.app.wallet_sharing.domain.interactor.ProximityQRInteractor
import ua.govstack.digitalwallet.app.wallet_sharing.domain.interactor.ProximitySharingInteractor
import ua.govstack.digitalwallet.app.wallet_sharing.domain.interactor.RemoteSharingInteractor
import javax.inject.Inject
import javax.inject.Singleton

interface PresentationFeatureManager {
    fun initPresentationScope()
    fun stopPresentationScope()
    fun getProximityQRInteractor(): ProximityQRInteractor
    fun getProximitySharingInteractor(): ProximitySharingInteractor
    fun getRemoteSharingInteractor(): RemoteSharingInteractor
}

@Singleton
internal class PresentationFeatureManagerImpl @Inject constructor(
    private val presentationFeatureComponentBuilder: PresentationFeatureComponent.Builder
) : PresentationFeatureManager {
    var presentationFeatureComponent: PresentationFeatureComponent? = null
        private set

    override fun initPresentationScope() {
        if (presentationFeatureComponent == null) {
            presentationFeatureComponent = presentationFeatureComponentBuilder.build()
        }
    }

    override fun stopPresentationScope() {
        presentationFeatureComponent = null
    }

    override fun getProximitySharingInteractor(): ProximitySharingInteractor {
        val component = presentationFeatureComponent
            ?: throw IllegalStateException("Presentation scope not started.")

        return EntryPoints.get(
            component,
            PresentationFeatureEntryPoint::class.java
        ).proximitySharingInteractor()
    }

    override fun getRemoteSharingInteractor(): RemoteSharingInteractor {
        val component = presentationFeatureComponent
            ?: throw IllegalStateException("Presentation scope not started.")

        return EntryPoints.get(
            component,
            PresentationFeatureEntryPoint::class.java
        ).remoteSharingInteractor()
    }

    override fun getProximityQRInteractor(): ProximityQRInteractor {
        val component = presentationFeatureComponent
            ?: throw IllegalStateException("Presentation scope not started.")

        return EntryPoints.get(
            component,
            PresentationFeatureEntryPoint::class.java
        ).proximityQRInteractor()
    }


}