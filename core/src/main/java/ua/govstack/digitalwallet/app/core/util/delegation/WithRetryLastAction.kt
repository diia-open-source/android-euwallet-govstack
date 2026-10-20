package ua.govstack.digitalwallet.app.core.util.delegation


interface WithRetryLastAction {

    fun retryLastAction()

    fun setLastAction(action: () -> Unit)

}