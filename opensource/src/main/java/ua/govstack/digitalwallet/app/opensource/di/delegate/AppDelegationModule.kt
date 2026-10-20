package ua.govstack.digitalwallet.app.opensource.di.delegate

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ua.govstack.digitalwallet.app.core.util.delegation.WithBuildConfig
import ua.govstack.digitalwallet.app.opensource.util.delegation.WithBuildConfigImpl

@Module
@InstallIn(SingletonComponent::class)
interface AppDelegationModule {

    @Binds
    fun bindWithBuildConfig(
        impl: WithBuildConfigImpl
    ): WithBuildConfig
}