package ua.govstack.digitalwallet.app.opensource.di.documents

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ua.govstack.digitalwallet.app.opensource.util.documents.DocActionsNavigationHandlerImpl
import ua.govstack.digitalwallet.app.opensource.util.documents.DocGalleryNavigationHelperImpl
import ua.govstack.digitalwallet.app.wallet_gallery.ui.actions.DocActionsNavigationHandler
import ua.govstack.digitalwallet.app.wallet_gallery.ui.navigation.DocGalleryNavigationHelper

@Module
@InstallIn(SingletonComponent::class)
interface DocumentsBindings {

    @Binds
    fun bindDocActionsNavHandler(impl: DocActionsNavigationHandlerImpl): DocActionsNavigationHandler

    @Binds
    fun bindDocGalleryNavHelper(impl: DocGalleryNavigationHelperImpl): DocGalleryNavigationHelper

}