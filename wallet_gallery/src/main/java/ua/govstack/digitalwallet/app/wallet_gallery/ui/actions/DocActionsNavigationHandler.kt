package ua.govstack.digitalwallet.app.wallet_gallery.ui.actions

interface DocActionsNavigationHandler {
    /**
     * Handles navigation to other features
     */
    fun handleNavigation(
        fragment: DocActionsDFCompose,
        navigation: DocActionsVMCompose.DocActions,
        args: DocActionsDFComposeArgs
    )
}