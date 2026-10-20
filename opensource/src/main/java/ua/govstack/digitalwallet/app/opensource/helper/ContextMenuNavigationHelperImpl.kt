package ua.govstack.digitalwallet.app.opensource.helper

import androidx.fragment.app.Fragment
import ua.govstack.digitalwallet.app.context_menu.helper.ContextMenuNavigationHelper
import ua.govstack.digitalwallet.app.core.models.ContextMenuField
import ua.govstack.digitalwallet.app.core.util.extensions.fragment.navigate
import ua.govstack.digitalwallet.app.opensource.NavMainDirections

class ContextMenuNavigationHelperImpl : ContextMenuNavigationHelper {

    override fun navigateToContextMenu(
        fragment: Fragment,
        menu: Array<ContextMenuField>
    ) {
        fragment.navigate(NavMainDirections.actionGlobalDestinationContextMenu(menu))
    }
}