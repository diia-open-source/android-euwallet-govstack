package ua.govstack.digitalwallet.app.core.models.notification.pull.message


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ListItemGroupOrg(
    @Json(name = "items")
    val items: List<Item>?,
    @Json(name = "title")
    val title: String?
)