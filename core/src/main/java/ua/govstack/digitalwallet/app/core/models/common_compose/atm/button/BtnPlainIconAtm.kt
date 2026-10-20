package ua.govstack.digitalwallet.app.core.models.common_compose.atm.button


import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import ua.govstack.digitalwallet.app.core.models.common_compose.general.Action

@Parcelize
@JsonClass(generateAdapter = true)
data class BtnPlainIconAtm(
    @Json(name = "icon")
    val icon: String?,
    @Json(name = "label")
    val label: String?,
    @Json(name = "state")
    val state: String?,
    @Json(name = "action")
    val action: Action?,
    @Json(name = "componentId")
    val componentId: String?,
) : Parcelable