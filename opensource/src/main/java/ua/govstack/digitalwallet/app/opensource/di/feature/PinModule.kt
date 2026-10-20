package ua.govstack.digitalwallet.app.opensource.di.feature

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ua.govstack.digitalwallet.app.opensource.helper.PinHelperImpl
import ua.govstack.digitalwallet.app.pin.helper.PinHelper

@Module
@InstallIn(SingletonComponent::class)
interface PinModule {

    @Binds
    fun bindPinHelper(impl: PinHelperImpl): PinHelper
}