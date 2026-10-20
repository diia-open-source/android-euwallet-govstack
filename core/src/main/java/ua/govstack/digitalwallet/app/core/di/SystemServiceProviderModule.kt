package ua.govstack.digitalwallet.app.core.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import ua.govstack.digitalwallet.app.core.util.system.service.SystemServiceProvider
import ua.govstack.digitalwallet.app.core.util.system.service.SystemServiceProviderImpl

@Module
@InstallIn(ViewModelComponent::class)
interface SystemServiceProviderModule {

    @Binds
    fun bindServiceProvider(
        impl: SystemServiceProviderImpl
    ): SystemServiceProvider
}