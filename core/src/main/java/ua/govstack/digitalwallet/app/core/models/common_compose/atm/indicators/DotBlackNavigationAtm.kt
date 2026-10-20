package ua.govstack.digitalwallet.app.core.models.common_compose.atm.indicators

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ua.govstack.digitalwallet.app.core.models.common_compose.general.PaddingMode

@JsonClass(generateAdapter = true)
data class DotBlackNavigationAtm(
    @Json(name = "componentId")
    val componentId: String?,
    @Json(name = "count")
    val count: Int?,
    @Json(name = "paddingMode")
    val paddingMode: PaddingMode?
)