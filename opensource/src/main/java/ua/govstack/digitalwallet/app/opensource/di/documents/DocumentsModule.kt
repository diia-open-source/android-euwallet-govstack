package ua.govstack.digitalwallet.app.opensource.di.documents

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ua.govstack.digitalwallet.app.core.models.document.BaseLocalizationChecker
import ua.govstack.digitalwallet.app.core.models.document.DocJsonAdapterDelegate
import ua.govstack.digitalwallet.app.core.models.document.Document
import ua.govstack.digitalwallet.app.core.models.document.barcode.DocumentBarcodeFactory
import ua.govstack.digitalwallet.app.core.util.extensions.context.dpToPx
import ua.govstack.digitalwallet.app.opensource.helper.DocumentsHelperImpl
import ua.govstack.digitalwallet.app.ui_base.mappers.document.BaseDocumentActionProvider
import ua.govstack.digitalwallet.app.ui_base.mappers.document.BaseFullInfoComposeMapper
import ua.govstack.digitalwallet.app.ui_base.mappers.document.DocumentComposeMapper
import ua.govstack.digitalwallet.app.wallet_core.documents.helper.DocumentsHelper
import ua.govstack.digitalwallet.app.wallet_doc_mdl.util.MdlContextMenuActionProvider
import ua.govstack.digitalwallet.app.wallet_doc_mdl.util.MdlFullInfoComposeMapper
import ua.govstack.digitalwallet.app.wallet_doc_pid.util.PidContextMenuActionProvider
import ua.govstack.digitalwallet.app.wallet_doc_pid.util.PidFullInfoComposeMapper
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DocTypesAvailableToUsers

@Module
@InstallIn(SingletonComponent::class)
object DocumentsModule {

    @Provides
    @Singleton
    fun provideLocalizationCheckers(): List<@JvmSuppressWildcards BaseLocalizationChecker> {
        return listOf()
    }

    @Provides
    fun provideDocumentsHelper(
    ): DocumentsHelper {
        return DocumentsHelperImpl()
    }

    @Provides
    @Singleton
    fun provideFullIntoComposeMapper(docComposeMapper: DocumentComposeMapper): List<@JvmSuppressWildcards BaseFullInfoComposeMapper> {
        return listOf(
            PidFullInfoComposeMapper(docComposeMapper),
            MdlFullInfoComposeMapper(docComposeMapper)
        )
    }

    @Provides
    @Singleton
    fun provideDocMenuActions(): List<@JvmSuppressWildcards BaseDocumentActionProvider> {
        return listOf(
            PidContextMenuActionProvider(),
            MdlContextMenuActionProvider()
        )
    }

    @Provides
    @Singleton
    fun provideDocDelegates(): List<DocJsonAdapterDelegate<out Document>> {
        return listOf(
        )
    }

    @Provides
    @Singleton
    @DocTypesAvailableToUsers
    fun provideDocTypesForUser(): Set<@JvmSuppressWildcards String> {
        return setOf()
    }

    private const val QR_SIZE = 218F
    private const val EAN_13_H = 36F
    private const val EAN_13_W = 218F

    @Provides
    fun provideBarcodeFactory(
        @ApplicationContext context: Context
    ): DocumentBarcodeFactory = DocumentBarcodeFactory(
        qrSizePx = context.dpToPx(QR_SIZE),
        ean13CodeHeight = context.dpToPx(EAN_13_H),
        ean13CodeWidth = context.dpToPx(EAN_13_W)
    )

}
