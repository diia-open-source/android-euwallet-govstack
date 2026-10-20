package ua.govstack.digitalwallet.app.opensource.di.feature

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ua.govstack.digitalwallet.app.context_menu.helper.ContextMenuNavigationHelper
import ua.govstack.digitalwallet.app.opensource.helper.ContextMenuNavigationHelperImpl

@Module
@InstallIn(SingletonComponent::class)
object ContextMenuModule {

    @Provides
    fun provideContextMenuNavigationHelper(): ContextMenuNavigationHelper {
        return ContextMenuNavigationHelperImpl()
    }

}