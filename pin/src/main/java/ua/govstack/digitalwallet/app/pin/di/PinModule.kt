package ua.govstack.digitalwallet.app.pin.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ua.govstack.digitalwallet.app.pin.repository.LoginPinRepository
import ua.govstack.digitalwallet.app.pin.repository.LoginPinRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
interface PinModule {

    @Binds
    fun bindLoginPinRepository(impl: LoginPinRepositoryImpl): LoginPinRepository
}