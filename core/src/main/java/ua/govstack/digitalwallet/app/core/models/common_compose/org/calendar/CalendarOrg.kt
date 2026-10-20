package ua.govstack.digitalwallet.app.core.models.common_compose.org.calendar

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ua.govstack.digitalwallet.app.core.models.common_compose.atm.icon.IconAtm
import ua.govstack.digitalwallet.app.core.models.common_compose.mlc.text.CurrentTimeMlc
import ua.govstack.digitalwallet.app.core.models.common_compose.mlc.text.StubMessageMlc

@JsonClass(generateAdapter = true)
data class CalendarOrg(
    @Json(name = "componentId")
    val componentId: String?,
    @Json(name = "inputCode")
    val inputCode: String?,
    @Json(name = "mandatory")
    val mandatory: Boolean?,
    @Json(name = "currentTimeMlc")
    val currentTimeMlc: CurrentTimeMlc?,
    @Json(name = "iconForMovingForward")
    val iconForMovingForward: IconForMoving?,
    @Json(name = "iconForMovingBackwards")
    val iconForMovingBackwards: IconForMoving?,
    @Json(name = "columnsAmount")
    val columnsAmount: Int?,
    @Json(name = "items")
    val items: List<CalendarItemOrg>?,
    @Json(name = "stubMessageMlc")
    val stubMessageMlc: StubMessageMlc?
) {
    @JsonClass(generateAdapter = true)
    data class IconForMoving(
        @Json(name = "iconAtm")
        val iconAtm: IconAtm,
    )

}