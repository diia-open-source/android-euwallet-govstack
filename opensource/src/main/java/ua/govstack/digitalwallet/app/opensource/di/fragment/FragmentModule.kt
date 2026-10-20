package ua.govstack.digitalwallet.app.opensource.di.fragment

import androidx.fragment.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.multibindings.IntoMap
import ua.govstack.digitalwallet.app.core.di.fragment.FragmentKey
import ua.govstack.digitalwallet.app.home.ui.HomeF

@Module
@InstallIn(FragmentComponent::class)
interface FragmentModule {

    @Binds
    @IntoMap
    @FragmentKey(HomeF::class)
    fun bindHomeF(impl: HomeF): Fragment
}