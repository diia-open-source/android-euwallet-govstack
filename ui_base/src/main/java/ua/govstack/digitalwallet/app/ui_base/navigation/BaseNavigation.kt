package ua.govstack.digitalwallet.app.ui_base.navigation

import ua.govstack.digitalwallet.app.core.models.ContextMenuField
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.navigation.NavigationPath

sealed class BaseNavigation : NavigationPath {

    object Back : BaseNavigation()

    data class ContextMenu(val contextMenuArray: Array<ContextMenuField>?) : BaseNavigation()

}