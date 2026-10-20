package ua.govstack.digitalwallet.app.wallet_gallery.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ua.govstack.digitalwallet.app.wallet_gallery.ui.details.mapper.DocFullInfoComposeMapper
import ua.govstack.digitalwallet.app.wallet_gallery.ui.details.mapper.DocFullInfoComposeMapperImpl

@Module
@InstallIn(SingletonComponent::class)
interface GalleryBindingsModule {

    @Binds
    fun bindDocFullComposeMapper(impl: DocFullInfoComposeMapperImpl): DocFullInfoComposeMapper
}
