package ua.govstack.digitalwallet.app.core.models.common_compose.org.carousel


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ua.govstack.digitalwallet.app.core.models.common_compose.mlc.list.ListItemMlc

@JsonClass(generateAdapter = true)
data class Item(
    @Json(name = "listItemMlc")
    val listItemMlc: ListItemMlc?
)