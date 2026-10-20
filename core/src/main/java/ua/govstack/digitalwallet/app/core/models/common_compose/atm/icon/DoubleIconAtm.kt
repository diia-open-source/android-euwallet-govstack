package ua.govstack.digitalwallet.app.core.models.common_compose.atm.icon


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ua.govstack.digitalwallet.app.core.models.common_compose.general.Action

@JsonClass(generateAdapter = true)
data class DoubleIconAtm(
    @Json(name = "accessibilityDescription")
    val accessibilityDescription: String?,
    @Json(name = "action")
    val action: Action?,
    @Json(name = "code")
    val code: String
)