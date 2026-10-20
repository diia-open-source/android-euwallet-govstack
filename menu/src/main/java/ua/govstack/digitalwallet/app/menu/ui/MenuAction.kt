package ua.govstack.digitalwallet.app.menu.ui

sealed class MenuAction {
    object OpenSettings : MenuAction()
    object Logout : MenuAction()
    object OpenQrCodeScan : MenuAction()
}
