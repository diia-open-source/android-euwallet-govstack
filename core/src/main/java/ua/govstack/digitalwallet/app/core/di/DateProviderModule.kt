package ua.govstack.digitalwallet.app.core.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ua.govstack.digitalwallet.app.core.util.date.CurrentDateProvider
import ua.govstack.digitalwallet.app.core.util.date.CurrentDateProviderImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DateProviderModule {

    @Provides
    @Singleton
    fun provideDateProvider(): CurrentDateProvider = CurrentDateProviderImpl()
}