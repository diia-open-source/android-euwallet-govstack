package ua.govstack.digitalwallet.app.core.push

import androidx.navigation.NavDirections
import ua.govstack.digitalwallet.app.core.models.notification.pull.PullNotificationItemSelection

abstract class BasePushNotificationAction(val id: String) {
    abstract fun getNavigationDirection(item: PullNotificationItemSelection): NavDirections?
}