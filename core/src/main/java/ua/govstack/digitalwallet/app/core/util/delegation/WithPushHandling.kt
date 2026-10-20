package ua.govstack.digitalwallet.app.core.util.delegation

import kotlinx.coroutines.flow.Flow
import ua.govstack.digitalwallet.app.core.models.notification.pull.PullNotificationItemSelection


interface WithPushHandling {

    val notificationConsumedEvent: Flow<PullNotificationItemSelection>

    suspend fun consumePush(notification: PullNotificationItemSelection)
}
