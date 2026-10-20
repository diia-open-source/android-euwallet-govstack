package ua.govstack.digitalwallet.app.core.util.deeplink

import ua.govstack.digitalwallet.app.core.models.deeplink.*

interface DeepLinkActionFactory {

    fun buildDeepLinkAction(path: String): DeepLinkAction
}
