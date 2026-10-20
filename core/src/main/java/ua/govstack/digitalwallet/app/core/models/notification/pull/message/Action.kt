package ua.govstack.digitalwallet.app.core.models.notification.pull.message


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Action(
    @Json(name = "resource")
    val resource: String?,
    @Json(name = "subtype")
    val subtype: String?,
    @Json(name = "type")
    val type: MessageActions?
)