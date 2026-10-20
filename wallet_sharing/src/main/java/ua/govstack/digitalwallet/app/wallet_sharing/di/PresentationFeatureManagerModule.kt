package ua.govstack.digitalwallet.app.wallet_sharing.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ua.govstack.digitalwallet.app.wallet_sharing.data.manager.PresentationFeatureManager
import ua.govstack.digitalwallet.app.wallet_sharing.data.manager.PresentationFeatureManagerImpl
import ua.govstack.digitalwallet.app.wallet_sharing.domain.observer.BluetoothConnectionObserver
import ua.govstack.digitalwallet.app.wallet_sharing.domain.observer.BluetoothConnectionObserverImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class PresentationFeatureManagerModule {
    @Binds
    @Singleton
    abstract fun bindManager(impl: PresentationFeatureManagerImpl): PresentationFeatureManager
}

@Module
@InstallIn(SingletonComponent::class)
object BluetoothModule {
    @Provides
    @Singleton
    fun provideBluetoothConnectionObserver(
        @ApplicationContext context: Context
    ): BluetoothConnectionObserver{
        return BluetoothConnectionObserverImpl(context)
    }
}
