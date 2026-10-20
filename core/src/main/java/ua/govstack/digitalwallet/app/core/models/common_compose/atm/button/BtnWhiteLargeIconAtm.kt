package ua.govstack.digitalwallet.app.core.models.common_compose.atm.button


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ua.govstack.digitalwallet.app.core.models.common_compose.general.Action
import ua.govstack.digitalwallet.app.core.models.common_compose.general.ButtonStates

@JsonClass(generateAdapter = true)
data class BtnWhiteLargeIconAtm(
    @Json(name = "action")
    val action: Action?,
    @Json(name = "componentId")
    val componentId: String?,
    @Json(name = "icon")
    val icon: String?,
    @Json(name = "label")
    val label: String?,
    @Json(name = "state")
    val state: ButtonStates?,
)