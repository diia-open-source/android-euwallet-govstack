package ua.govstack.digitalwallet.app.menu.helper

import androidx.fragment.app.Fragment

interface MenuHelper {


    /**
     * Navigate to settings
     */
    fun navigateToSettings(fragment: Fragment)

    /**
     * Navigate to Logout dialog
     */
    fun navigateToLogout(fragment: Fragment)

    /**
     * Navigate to settings System dialog
     */
    fun navigateToSettingsSystemDialog(fragment: Fragment)

    /**
     * Navigate to transactions
     */
    fun navigateToTransaction(fragment: Fragment)

}
