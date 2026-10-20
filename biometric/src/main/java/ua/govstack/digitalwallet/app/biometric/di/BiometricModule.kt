package ua.govstack.digitalwallet.app.biometric.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ua.govstack.digitalwallet.app.biometric.AndroidBiometric
import ua.govstack.digitalwallet.app.biometric.Biometric
import ua.govstack.digitalwallet.app.biometric.store.BiometricRepository
import ua.govstack.digitalwallet.app.biometric.store.BiometricRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
interface BiometricModule {

    @Binds
    fun bindsBiometricRepository(
        impl: BiometricRepositoryImpl,
    ): BiometricRepository

    @Binds
    fun bindsBiometric(
        impl: AndroidBiometric,
    ): Biometric
}