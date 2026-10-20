package ua.govstack.digitalwallet.app.opensource.di.util

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ua.govstack.digitalwallet.app.core.util.UuidProvider
import ua.govstack.digitalwallet.app.core.util.UuidProviderImpl

@Module
@InstallIn(SingletonComponent::class)
object UUIDModule {

    @Provides
    fun provideUUIDProvider(): UuidProvider = UuidProviderImpl()
}