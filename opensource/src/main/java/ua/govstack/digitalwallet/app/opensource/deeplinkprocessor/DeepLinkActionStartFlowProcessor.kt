package ua.govstack.digitalwallet.app.opensource.deeplinkprocessor

import androidx.navigation.NavDirections
import ua.govstack.digitalwallet.app.core.models.SingleDeeplinkProcessor
import ua.govstack.digitalwallet.app.core.models.deeplink.DeepLinkAction
import ua.govstack.digitalwallet.app.core.models.deeplink.DeepLinkActionStartFlow
import ua.govstack.digitalwallet.app.core.util.deeplink.DeepLinkRoutes
import ua.govstack.digitalwallet.app.opensource.NavMainDirections

class DeepLinkActionStartFlowProcessor: SingleDeeplinkProcessor {
    override suspend fun handleDeepLinkAction(linkAction: DeepLinkAction): NavDirections? {
        val action = linkAction as DeepLinkActionStartFlow

        return when (action.flowId) {
            DeepLinkRoutes.HOME -> null
            in DeepLinkRoutes.presentationFlowIds -> {
                NavMainDirections.actionHomeFToSharingF(presentationLink = linkAction.resId)
            }
            in DeepLinkRoutes.credentialOfferSchemes -> {
                NavMainDirections.actionHomeFToDocumentOfferF(linkToProcess = linkAction.resId)
            }
            else -> null
        }
    }
    override fun isHandled(action: DeepLinkAction): Boolean = action is DeepLinkActionStartFlow
}
