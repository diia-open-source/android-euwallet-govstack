package ua.govstack.digitalwallet.app.core.util.navigation

interface HomeNavigation {

    var isConsumed: Boolean

    fun consumeEvent(action: () -> Unit) {
        if (!isConsumed) {
            isConsumed = true
            action.invoke()
        }
    }
}