package ua.govstack.digitalwallet.app.home.model

import ua.govstack.digitalwallet.app.ui_base.models.homescreen.HomeMenuItemConstructor
import ua.govstack.digitalwallet.app.home.R
import ua.govstack.digitalwallet.app.home.ui.HomeActions

object HomeMenuItem {
    val DOCUMENTS = HomeMenuItemConstructor(
        HomeActions.HOME_DOCUMENTS,
        R.color.colorPrimary,
        R.color.colorPrimary,
        R.string.app_bar_title_empty
    )

    val MENU = HomeMenuItemConstructor(
        HomeActions.HOME_MENU,
        R.color.colorPrimary,
        R.color.colorPrimary,
        R.string.app_bar_title_empty
    )
}