package ua.govstack.digitalwallet.app.opensource.util.delegation

import ua.govstack.digitalwallet.app.core.util.delegation.WithRetryLastAction
import javax.inject.Inject

class DefaultRetryLastActionBehaviour @Inject constructor():
    WithRetryLastAction {
    private var lastAction: (() -> Unit)? = null

    override fun retryLastAction() {
        lastAction?.invoke()
        lastAction = null
    }

    override fun setLastAction(action: () -> Unit) {
        lastAction = action
    }
}