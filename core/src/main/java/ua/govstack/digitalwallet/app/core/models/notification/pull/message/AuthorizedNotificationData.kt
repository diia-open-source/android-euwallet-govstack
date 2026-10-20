package ua.govstack.digitalwallet.app.core.models.notification.pull.message

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AuthorizedNotificationData(
    @Json(name = "notification")
    val notification: Notification
)
