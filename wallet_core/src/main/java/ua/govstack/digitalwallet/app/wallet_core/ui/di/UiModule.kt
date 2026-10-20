package ua.govstack.digitalwallet.app.wallet_core.ui.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ua.govstack.digitalwallet.app.ui_base.provider.ResourceProvider
import ua.govstack.digitalwallet.app.ui_base.provider.ResourceProviderImpl
import ua.govstack.digitalwallet.app.wallet_core.ui.alertDialogsFactory.WalletAlertDialogFactory
import ua.govstack.digitalwallet.app.wallet_core.ui.alertDialogsFactory.WalletAlertDialogFactoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UiModule {
    @Provides
    @Singleton
    fun provideAlertDialogTemplateFactory(
    ): WalletAlertDialogFactory {
        return WalletAlertDialogFactoryImpl()
    }

    @Provides
    @Singleton
    fun provideResourceProvider(
        @ApplicationContext context: Context
    ): ResourceProvider {
        return ResourceProviderImpl(context)
    }
}