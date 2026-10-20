package ua.govstack.digitalwallet.app.core.models.common_compose.atm.text


import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import ua.govstack.digitalwallet.app.core.models.common_compose.general.PaddingMode

@Parcelize
@JsonClass(generateAdapter = true)
data class GreyTitleAtm(
    @Json(name = "componentId")
    val componentId: String?,
    @Json(name = "label")
    val label: String?,
    @Json(name = "paddingMode")
    val paddingMode: PaddingMode?
) : Parcelable