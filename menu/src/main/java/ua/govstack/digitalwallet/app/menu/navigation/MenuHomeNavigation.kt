package ua.govstack.digitalwallet.app.menu.navigation

import ua.govstack.digitalwallet.app.core.models.dialogs.TemplateDialogModel
import ua.govstack.digitalwallet.app.core.util.navigation.HomeNavigation

sealed class MenuHomeNavigation : HomeNavigation {

    data class ToTemplateDialog(
        val template: TemplateDialogModel,
        override var isConsumed: Boolean = false
    ) : MenuHomeNavigation()

    data class ToSettings(override var isConsumed: Boolean = false) : MenuHomeNavigation()
    data class ToLogout(override var isConsumed: Boolean = false) : MenuHomeNavigation()
    data class ToLinkDialog(override var isConsumed: Boolean = false) : MenuHomeNavigation()
    data class ToCameraRequest(override var isConsumed: Boolean = false) : MenuHomeNavigation()
    data class ToTransactions(override var isConsumed: Boolean = false) : MenuHomeNavigation()
}
