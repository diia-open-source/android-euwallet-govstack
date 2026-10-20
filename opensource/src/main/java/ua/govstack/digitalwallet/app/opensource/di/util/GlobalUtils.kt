package ua.govstack.digitalwallet.app.opensource.di.util

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ua.govstack.digitalwallet.app.core.util.DiiaDispatcherProvider
import ua.govstack.digitalwallet.app.core.util.DispatcherProvider
import ua.govstack.digitalwallet.app.core.util.alert.ClientAlertDialogsFactory
import ua.govstack.digitalwallet.app.core.util.deeplink.DeepLinkActionFactory
import ua.govstack.digitalwallet.app.opensource.util.alert.AndroidClientAlertDialogsFactory
import ua.govstack.digitalwallet.app.opensource.util.deeplink.AndroidDeepLinkActionFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
interface GlobalUtils {

    @Binds
    @Singleton
    fun bindTemplateFactory(impl: AndroidClientAlertDialogsFactory): ClientAlertDialogsFactory

    @Binds
    @Singleton
    fun bindCoroutineDispatcher(impl: DiiaDispatcherProvider): DispatcherProvider

    @Binds
    fun bindDeepLinkActionFactory(impl: AndroidDeepLinkActionFactory): DeepLinkActionFactory
}
