package ua.govstack.digitalwallet.app.core.models.notification.pull.message


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LeftNavIcon(
    @Json(name = "action")
    val action: Action?,
    @Json(name = "code")
    val code: String?,
    @Json(name = "accessibilityDescription")
    val accessibilityDescription: String?
)