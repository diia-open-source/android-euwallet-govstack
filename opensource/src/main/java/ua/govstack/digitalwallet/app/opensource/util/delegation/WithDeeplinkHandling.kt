package ua.govstack.digitalwallet.app.opensource.util.delegation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ua.govstack.digitalwallet.app.core.di.actions.GlobalActionDeeplink
import ua.govstack.digitalwallet.app.core.models.deeplink.DeepLinkAction
import ua.govstack.digitalwallet.app.core.util.deeplink.DeepLinkActionFactory
import ua.govstack.digitalwallet.app.core.util.delegation.WithDeeplinkHandling
import ua.govstack.digitalwallet.app.core.util.event.UiDataEvent
import javax.inject.Inject

class DefaultDeeplinkHandleBehaviour @Inject constructor(
    @param:GlobalActionDeeplink private val globalActionDeeplink: MutableStateFlow<UiDataEvent<DeepLinkAction>?>,
    private val deepLinkActionFactory: DeepLinkActionFactory,
) : WithDeeplinkHandling {

    override val deeplinkFlow: StateFlow<UiDataEvent<DeepLinkAction>?>
        get() = globalActionDeeplink

    override suspend fun emitDeeplink(event: UiDataEvent<DeepLinkAction>) {
        globalActionDeeplink.emit(event)
    }

    override fun buildDeepLinkAction(path: String): DeepLinkAction {
        return deepLinkActionFactory.buildDeepLinkAction(path)
    }
}
