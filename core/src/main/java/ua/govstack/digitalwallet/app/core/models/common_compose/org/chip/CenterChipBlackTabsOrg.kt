package ua.govstack.digitalwallet.app.core.models.common_compose.org.chip

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import ua.govstack.digitalwallet.app.core.models.common_compose.general.PaddingMode
import ua.govstack.digitalwallet.app.core.models.common_compose.mlc.chip.ChipBlackMlc
import ua.govstack.digitalwallet.app.core.models.common_compose.mlc.chip.ChipBlackMlcItem

@Parcelize
@JsonClass(generateAdapter = true)
data class CenterChipBlackTabsOrg(
    @Json(name = "componentId")
    val componentId: String,
    @Json(name = "paddingMode")
    val paddingMode: PaddingMode?,
    @Json(name = "items")
    val items: List<ChipBlackMlcItem>,
    @Json(name = "preselectedCode")
    val preselectedCode: String
) : Parcelable
