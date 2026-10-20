package ua.govstack.digitalwallet.app.core.models

import androidx.navigation.NavDirections
import ua.govstack.digitalwallet.app.core.models.deeplink.DeepLinkAction

interface SingleDeeplinkProcessor {
    fun isHandled(action: DeepLinkAction): Boolean
    suspend fun handleDeepLinkAction(linkAction: DeepLinkAction): NavDirections?
}
