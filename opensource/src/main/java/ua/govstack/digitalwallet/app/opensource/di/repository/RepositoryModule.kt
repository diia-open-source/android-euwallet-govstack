package ua.govstack.digitalwallet.app.opensource.di.repository

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ua.govstack.digitalwallet.app.core.data.repository.SystemRepository
import ua.govstack.digitalwallet.app.diia_storage.store.repository.authorization.AuthorizationRepository
import ua.govstack.digitalwallet.app.diia_storage.store.repository.authorization.AuthorizationRepositoryImpl
import ua.govstack.digitalwallet.app.diia_storage.store.repository.system.SystemRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    fun bindSystemRepository(impl: SystemRepositoryImpl): SystemRepository

    @Binds
    fun bindSettingsRepository(impl: AuthorizationRepositoryImpl): AuthorizationRepository
}