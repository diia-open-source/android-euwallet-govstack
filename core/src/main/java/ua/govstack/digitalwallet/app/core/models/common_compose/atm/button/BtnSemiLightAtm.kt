package ua.govstack.digitalwallet.app.core.models.common_compose.atm.button


import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import ua.govstack.digitalwallet.app.core.models.common_compose.general.Action
import ua.govstack.digitalwallet.app.core.models.common_compose.general.ButtonStates
@Parcelize
@JsonClass(generateAdapter = true)
data class BtnSemiLightAtm(
    @Json(name = "action")
    val action: Action?,
    @Json(name = "componentId")
    val componentId: String?,
    @Json(name = "label")
    val label: String?,
    @Json(name = "state")
    val state: ButtonStates?,
): Parcelable