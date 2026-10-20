package ua.govstack.digitalwallet.app.opensource.helper

import androidx.fragment.app.Fragment
import ua.govstack.digitalwallet.app.core.models.SystemDialog
import ua.govstack.digitalwallet.app.core.util.extensions.fragment.findNavControllerById
import ua.govstack.digitalwallet.app.core.util.extensions.fragment.navigate
import ua.govstack.digitalwallet.app.menu.NavMenuActionsDirections
import ua.govstack.digitalwallet.app.menu.helper.MenuHelper
import ua.govstack.digitalwallet.app.opensource.NavMainDirections
import ua.govstack.digitalwallet.app.opensource.R
import ua.govstack.digitalwallet.app.ui_base.NavSystemDialogDirections

class MenuHelperImpl : MenuHelper {

    override fun navigateToSettings(fragment: Fragment) {
        fragment.apply {
            navigate(
                NavMenuActionsDirections.actionHomeFToSettingsF(),
                findNavControllerById(R.id.nav_host)
            )
        }
    }

    override fun navigateToLogout(fragment: Fragment) {
        fragment.navigate(
            NavSystemDialogDirections.actionGlobalToSystemDialog(
                SystemDialog(
                    fragment.getString(ua.govstack.digitalwallet.app.menu.R.string.settings_dialog_title_logout),
                    fragment.getString(ua.govstack.digitalwallet.app.menu.R.string.settings_dialog_logout_text),
                    fragment.getString(ua.govstack.digitalwallet.app.menu.R.string.settings_dialog_logout_stay),
                    fragment.getString(ua.govstack.digitalwallet.app.menu.R.string.settings_dialog_logout_leave),
                )
            ),
            fragment.findNavControllerById(ua.govstack.digitalwallet.app.menu.R.id.nav_host)
        )
    }

    override fun navigateToSettingsSystemDialog(fragment: Fragment) {
        fragment.navigate(
            NavSystemDialogDirections.actionGlobalToSystemDialog(
                SystemDialog(
                    fragment.getString(
                        ua.govstack.digitalwallet.app.menu.R.string.settings_dialog_title_faq,
                        fragment.getString(ua.govstack.digitalwallet.app.menu.R.string.browser)
                    ),
                    null,
                    fragment.getString(ua.govstack.digitalwallet.app.menu.R.string.settings_dialog_support_open),
                    fragment.getString(ua.govstack.digitalwallet.app.menu.R.string.settings_dialog_support_cancel),
                )
            ),
            fragment.findNavControllerById(ua.govstack.digitalwallet.app.menu.R.id.nav_host)
        )
    }

    override fun navigateToTransaction(fragment: Fragment) {
        fragment.apply {
            navigate(
                NavMainDirections.actionHomeFToTransactionF(),
                findNavControllerById(R.id.nav_host)
            )
        }
    }

}
