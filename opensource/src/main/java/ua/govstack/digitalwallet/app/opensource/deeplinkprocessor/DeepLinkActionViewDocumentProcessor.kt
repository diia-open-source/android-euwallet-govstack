package ua.govstack.digitalwallet.app.opensource.deeplinkprocessor

import androidx.navigation.NavDirections
import kotlinx.coroutines.flow.MutableStateFlow
import ua.govstack.digitalwallet.app.core.di.actions.GlobalActionFocusOnDocument
import ua.govstack.digitalwallet.app.core.di.actions.GlobalActionSelectedMenuItem
import ua.govstack.digitalwallet.app.core.models.SingleDeeplinkProcessor
import ua.govstack.digitalwallet.app.core.models.deeplink.DeepLinkAction
import ua.govstack.digitalwallet.app.core.models.deeplink.DeepLinkActionViewDocument
import ua.govstack.digitalwallet.app.core.util.event.UiDataEvent
import ua.govstack.digitalwallet.app.home.model.HomeMenuItem
import ua.govstack.digitalwallet.app.ui_base.models.homescreen.HomeMenuItemConstructor

class DeepLinkActionViewDocumentProcessor(
    @param:GlobalActionFocusOnDocument val globalActionFocusOnDocument: MutableStateFlow<UiDataEvent<String>?>,
    @param:GlobalActionSelectedMenuItem val globalActionSelectedMenuItem: MutableStateFlow<UiDataEvent<HomeMenuItemConstructor>?>,
): SingleDeeplinkProcessor {
    override suspend fun handleDeepLinkAction(linkAction: DeepLinkAction): NavDirections? {
        val action = linkAction as DeepLinkActionViewDocument
        globalActionFocusOnDocument.emit(UiDataEvent(action.documentType))
        globalActionSelectedMenuItem.emit(UiDataEvent(HomeMenuItem.DOCUMENTS))
        return null
    }

    override fun isHandled(action: DeepLinkAction): Boolean = action is DeepLinkActionViewDocument
}