package ua.govstack.digitalwallet.app.opensource.di.feature

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import ua.govstack.digitalwallet.app.core.controller.DeeplinkProcessor
import ua.govstack.digitalwallet.app.core.di.actions.GlobalActionFocusOnDocument
import ua.govstack.digitalwallet.app.core.di.actions.GlobalActionSelectedMenuItem
import ua.govstack.digitalwallet.app.core.util.event.UiDataEvent
import ua.govstack.digitalwallet.app.opensource.deeplinkprocessor.DeepLinkActionStartFlowProcessor
import ua.govstack.digitalwallet.app.opensource.deeplinkprocessor.DeepLinkActionViewDocumentProcessor
import ua.govstack.digitalwallet.app.opensource.deeplinkprocessor.DeeplinkProcessorImpl
import ua.govstack.digitalwallet.app.ui_base.models.homescreen.HomeMenuItemConstructor

@Module
@InstallIn(SingletonComponent::class)
class HomeModule {

    @Provides
    fun provideDeepLinkProcessor(
        @GlobalActionFocusOnDocument globalActionFocusOnDocument: MutableStateFlow<UiDataEvent<String>?>,
        @GlobalActionSelectedMenuItem globalActionSelectedMenuItem: MutableStateFlow<UiDataEvent<HomeMenuItemConstructor>?>
    ): DeeplinkProcessor {
        val linkActionProcessors = listOf(
            DeepLinkActionViewDocumentProcessor(
                globalActionFocusOnDocument,
                globalActionSelectedMenuItem
            ),
            DeepLinkActionStartFlowProcessor()
        )

        return DeeplinkProcessorImpl(linkActionProcessors)
    }
}