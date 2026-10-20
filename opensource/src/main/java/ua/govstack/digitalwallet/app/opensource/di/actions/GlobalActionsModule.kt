package ua.govstack.digitalwallet.app.opensource.di.actions

import android.content.Context
import androidx.lifecycle.MutableLiveData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import ua.govstack.digitalwallet.app.core.di.actions.GlobalActionAllowAuthorizedLinks
import ua.govstack.digitalwallet.app.core.di.actions.GlobalActionAppStartEvent
import ua.govstack.digitalwallet.app.core.di.actions.GlobalActionConfirmDocumentRemoval
import ua.govstack.digitalwallet.app.core.di.actions.GlobalActionDeeplink
import ua.govstack.digitalwallet.app.core.di.actions.GlobalActionDeferredIssuingEvent
import ua.govstack.digitalwallet.app.core.di.actions.GlobalActionDeleteDocument
import ua.govstack.digitalwallet.app.core.di.actions.GlobalActionDocumentBackground
import ua.govstack.digitalwallet.app.core.di.actions.GlobalActionFocusOnDocument
import ua.govstack.digitalwallet.app.core.di.actions.GlobalActionLogout
import ua.govstack.digitalwallet.app.core.di.actions.GlobalActionNetworkState
import ua.govstack.digitalwallet.app.core.di.actions.GlobalActionRunDeepLinkAfterPin
import ua.govstack.digitalwallet.app.core.di.actions.GlobalActionSelectedMenuItem
import ua.govstack.digitalwallet.app.core.models.common.DeferredIssuingEvent
import ua.govstack.digitalwallet.app.core.models.deeplink.DeepLinkAction
import ua.govstack.digitalwallet.app.core.network.connectivity.ConnectivityObserver
import ua.govstack.digitalwallet.app.core.util.event.UiDataEvent
import ua.govstack.digitalwallet.app.core.util.event.UiEvent
import ua.govstack.digitalwallet.app.home.model.background.DocumentBackground
import ua.govstack.digitalwallet.app.opensource.data.data_source.network.connectivity.NetworkConnectivityObserver
import ua.govstack.digitalwallet.app.ui_base.models.homescreen.HomeMenuItemConstructor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GlobalActionsModule {

    @Provides
    @GlobalActionLogout
    @Singleton
    fun provideActionLogout() = MutableLiveData<UiEvent>()

    @Provides
    @Singleton
    @GlobalActionNetworkState
    fun provideNetworkStateFlow(
        @ApplicationContext context: Context
    ): ConnectivityObserver {
        return NetworkConnectivityObserver(context)
    }

    @Provides
    @GlobalActionDeeplink
    @Singleton
    fun provideActionDeeplink() = MutableStateFlow<UiDataEvent<DeepLinkAction>?>(null)

    @Provides
    @Singleton
    @GlobalActionAllowAuthorizedLinks
    fun provideActionAllowAuthorizedLinks() = MutableSharedFlow<UiDataEvent<Boolean>>()

    @Provides
    @GlobalActionDocumentBackground
    @Singleton
    fun provideActionDocumentBackground() = MutableStateFlow<UiDataEvent<DocumentBackground>?>(null)


    @Provides
    @GlobalActionConfirmDocumentRemoval
    @Singleton
    fun provideActionConfirmDocumentRemoval() = MutableStateFlow<UiDataEvent<String>?>(null)

    @Provides
    @GlobalActionFocusOnDocument
    @Singleton
    fun provideActionFocusOnDoc() = MutableStateFlow<UiDataEvent<String>?>(null)

    @Provides
    @GlobalActionDeleteDocument
    @Singleton
    fun provideActionDeleteDoc() = MutableStateFlow<UiDataEvent<String>?>(null)

    @Provides
    @GlobalActionSelectedMenuItem
    @Singleton
    fun provideActionSelectedMenuItem() =
        MutableStateFlow<UiDataEvent<HomeMenuItemConstructor>?>(null)

    @Provides
    @Singleton
    @GlobalActionAppStartEvent
    fun provideActionAppStartEvent() = MutableSharedFlow<UiEvent>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_LATEST
    )

    @Provides
    @GlobalActionRunDeepLinkAfterPin
    @Singleton
    fun provideActionRunDeepLinkAfterPin() = MutableSharedFlow<UiEvent>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_LATEST
    )

    @Provides
    @GlobalActionDeferredIssuingEvent
    @Singleton
    fun provideDeferredIssuingEventFlow() = MutableSharedFlow<UiDataEvent<DeferredIssuingEvent>?>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

}
