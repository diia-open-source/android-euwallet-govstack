package ua.govstack.digitalwallet.app.core.models.common_compose.atm.button


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlayerBtnAtm(
    @Json(name = "icon")
    val icon: String,
    @Json(name = "type")
    val type: String
)