package ua.govstack.digitalwallet.app.opensource.di.documents

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ua.govstack.digitalwallet.app.opensource.ui.compose.mappers.DocumentComposeMapperImpl
import ua.govstack.digitalwallet.app.ui_base.mappers.document.DocumentComposeMapper

@Module
@InstallIn(SingletonComponent::class)
interface DocTabMappersModule {

    @Binds
    fun bindPreviewComposeMapper(
        impl: DocumentComposeMapperImpl
    ): DocumentComposeMapper

}