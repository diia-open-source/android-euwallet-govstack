package ua.govstack.digitalwallet.app.diia_storage.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ua.govstack.digitalwallet.app.core.util.decoder.AndroidBase64Wrapper
import ua.govstack.digitalwallet.app.core.util.decoder.Base64Wrapper

@Module
@InstallIn(SingletonComponent::class)
object Base64WrapperModule {

    @Provides
    fun provideBase64Wrapper(): Base64Wrapper = AndroidBase64Wrapper()

}