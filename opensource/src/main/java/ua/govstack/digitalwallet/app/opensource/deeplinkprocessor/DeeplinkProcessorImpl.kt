package ua.govstack.digitalwallet.app.opensource.deeplinkprocessor

import androidx.navigation.NavDirections
import ua.govstack.digitalwallet.app.core.controller.DeeplinkProcessor
import ua.govstack.digitalwallet.app.core.models.SingleDeeplinkProcessor
import ua.govstack.digitalwallet.app.core.models.deeplink.DeepLinkAction

class DeeplinkProcessorImpl(
    private val linkActionProcessors: List<SingleDeeplinkProcessor>
): DeeplinkProcessor {

    override suspend fun handleDeepLinkAction(action: DeepLinkAction): NavDirections? {
        linkActionProcessors.forEach {
            if (it.isHandled(action)) {
                return it.handleDeepLinkAction(action)
            }
        }
        return null
    }
}