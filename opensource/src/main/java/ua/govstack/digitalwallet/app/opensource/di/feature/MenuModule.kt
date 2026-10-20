package ua.govstack.digitalwallet.app.opensource.di.feature

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ua.govstack.digitalwallet.app.menu.helper.MenuHelper
import ua.govstack.digitalwallet.app.opensource.helper.MenuHelperImpl

@Module
@InstallIn(SingletonComponent::class)
object MenuModule {

    @Provides
    fun provideMenuHelper(): MenuHelper {
        return MenuHelperImpl()
    }
}