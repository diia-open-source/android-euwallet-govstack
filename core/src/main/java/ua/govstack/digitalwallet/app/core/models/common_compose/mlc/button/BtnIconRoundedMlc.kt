package ua.govstack.digitalwallet.app.core.models.common_compose.mlc.button


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ua.govstack.digitalwallet.app.core.models.common_compose.general.Action

@JsonClass(generateAdapter = true)
data class BtnIconRoundedMlc(
    @Json(name = "action")
    val action: Action?,
    @Json(name = "icon")
    val icon: String,
    @Json(name = "label")
    val label: String?
)