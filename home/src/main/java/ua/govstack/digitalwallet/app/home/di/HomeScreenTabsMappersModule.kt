package ua.govstack.digitalwallet.app.home.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import ua.govstack.digitalwallet.app.home.ui.HomeScreenComposeMapper
import ua.govstack.digitalwallet.app.home.ui.HomeScreenComposeMapperImpl

@Module
@InstallIn(ViewModelComponent::class)
interface HomeScreenTabsMappersModule {

    @Binds
    fun bindHomeScreenComposeMapper(
        impl: HomeScreenComposeMapperImpl
    ): HomeScreenComposeMapper

}