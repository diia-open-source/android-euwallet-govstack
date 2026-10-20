package ua.govstack.digitalwallet.app.core.util.delegation

import kotlinx.coroutines.flow.StateFlow
import ua.govstack.digitalwallet.app.core.models.deeplink.DeepLinkAction
import ua.govstack.digitalwallet.app.core.util.event.UiDataEvent


interface WithDeeplinkHandling {

    val deeplinkFlow: StateFlow<UiDataEvent<DeepLinkAction>?>

    suspend fun emitDeeplink(event: UiDataEvent<DeepLinkAction>)

    fun buildDeepLinkAction(path: String): DeepLinkAction
}
