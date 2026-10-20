package ua.govstack.digitalwallet.app.opensource.di.delegate

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import ua.govstack.digitalwallet.app.core.util.delegation.WithPermission
import ua.govstack.digitalwallet.app.opensource.util.delegation.DefaultSelfPermissionBehavior

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
@Module
@InstallIn(FragmentComponent::class)
interface FragmentDelegationModule {

    @Binds
    fun bindPermissionDelegate(
        impl: DefaultSelfPermissionBehavior
    ): WithPermission

}