package ua.govstack.digitalwallet.app.core.models.common.message

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import ua.govstack.digitalwallet.app.core.models.common_compose.atm.button.BtnStrokeAdditionalAtm
import ua.govstack.digitalwallet.app.core.models.common_compose.general.PaddingMode
import ua.govstack.digitalwallet.app.core.models.common_compose.atm.icon.SmallIconAtm

@Parcelize
@JsonClass(generateAdapter = true)
data class AttentionIconMessageMlc(
    @Json(name = "componentId")
    val componentId: String? = null,
    @Json(name = "paddingMode")
    val paddingMode: PaddingMode?,
    @Json(name = "backgroundMode")
    val backgroundMode: BackgroundMode,
    @Json(name = "smallIconAtm")
    val smallIconAtm: SmallIconAtm?,
    @Json(name = "title")
    val title: String? = null,
    @Json(name = "text")
    val text: String?,
    @Json(name = "parameters")
    val parameters: List<TextParameter>?,
    @Json(name = "expanded")
    val expanded: ExpandedData?,
    @Json(name = "btnStrokeAdditionalAtm")
    val btnStrokeAdditionalAtm: BtnStrokeAdditionalAtm?
) : Parcelable {

    enum class BackgroundMode {
        info, note;
    }

    @Parcelize
    @JsonClass(generateAdapter = true)
    data class ExpandedData(
        @Json(name = "expandedText")
        val expandedText: String?,
        @Json(name = "collapsedText")
        val collapsedText: String?,
        @Json(name = "isExpanded")
        val isExpanded: Boolean?,
    ) : Parcelable
}