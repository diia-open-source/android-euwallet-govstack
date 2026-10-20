package ua.govstack.digitalwallet.app.core.models.common_compose.mlc.card


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ua.govstack.digitalwallet.app.core.models.common_compose.atm.icon.BadgeCounterAtm
import ua.govstack.digitalwallet.app.core.models.common_compose.general.Action

@JsonClass(generateAdapter = true)
data class VerticalCardMlc(
    @Json(name = "action")
    val action: Action?,
    @Json(name = "badgeCounterAtm")
    val badgeCounterAtm: BadgeCounterAtm,
    @Json(name = "id")
    val id: String?,
    @Json(name = "image")
    val image: String,
    @Json(name = "title")
    val title: String
)