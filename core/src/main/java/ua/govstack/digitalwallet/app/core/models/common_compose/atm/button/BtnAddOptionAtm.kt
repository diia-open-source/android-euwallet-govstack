package ua.govstack.digitalwallet.app.core.models.common_compose.atm.button

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ua.govstack.digitalwallet.app.core.models.common_compose.atm.icon.SmallIconAtm
import ua.govstack.digitalwallet.app.core.models.common_compose.general.Action
import ua.govstack.digitalwallet.app.core.models.common_compose.general.ButtonStates
import ua.govstack.digitalwallet.app.core.models.common_compose.general.PaddingMode

@JsonClass(generateAdapter = true)
class BtnAddOptionAtm(
    @Json(name = "componentId")
    val componentId: String?,
    @Json(name = "paddingMode")
    val paddingMode: PaddingMode?,
    @Json(name = "label")
    val label: String,
    @Json(name = "description")
    val description: String?,
    @Json(name = "iconLeft")
    val iconLeft: SmallIconAtm?,
    @Json(name = "state")
    val state: ButtonStates?,
    @Json(name = "action")
    val action: Action?
)