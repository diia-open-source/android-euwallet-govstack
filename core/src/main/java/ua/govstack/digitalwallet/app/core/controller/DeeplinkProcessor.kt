package ua.govstack.digitalwallet.app.core.controller

import androidx.navigation.NavDirections
import ua.govstack.digitalwallet.app.core.models.deeplink.DeepLinkAction

interface DeeplinkProcessor {
    /**
     * Handle deeplink action from list of available action list
     */
    suspend fun handleDeepLinkAction(action: DeepLinkAction): NavDirections?
}