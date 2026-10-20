package ua.govstack.digitalwallet.app.context_menu.helper

import androidx.fragment.app.Fragment
import ua.govstack.digitalwallet.app.core.models.ContextMenuField

interface ContextMenuNavigationHelper {

    /**
     * Navigate to ContextMenu
     */
    fun navigateToContextMenu(fragment: Fragment, menu: Array<ContextMenuField>)
}