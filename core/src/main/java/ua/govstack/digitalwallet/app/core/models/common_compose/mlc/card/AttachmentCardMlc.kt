package ua.govstack.digitalwallet.app.core.models.common_compose.mlc.card

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ua.govstack.digitalwallet.app.core.models.common_compose.atm.chip.ChipStatusAtm
import ua.govstack.digitalwallet.app.core.models.common_compose.atm.icon.SmallIconAtm
import ua.govstack.digitalwallet.app.core.models.common_compose.general.PaddingMode
import ua.govstack.digitalwallet.app.core.models.common_compose.mlc.button.BtnIconPlainGroupMlc

@JsonClass(generateAdapter = true)
data class AttachmentCardMlc(
    @Json(name = "componentId")
    val componentId: String?,
    @Json(name = "paddingMode")
    val paddingMode: PaddingMode?,
    @Json(name = "id")
    val id: String?,
    @Json(name = "chipStatusAtm")
    val chipStatusAtm: ChipStatusAtm?,
    @Json(name = "iconRight")
    val iconRight: SmallIconAtm?,
    @Json(name = "label")
    val label: String,
    @Json(name = "description")
    val description: String?,
    @Json(name = "key")
    val key: KeyItem?,
    @Json(name = "btnIconPlainGroupMlc")
    val btnIconPlainGroupMlc: BtnIconPlainGroupMlc?
) {

    @JsonClass(generateAdapter = true)
    data class KeyItem(
        @Json(name = "id")
        val id: String?,
        @Json(name = "label")
        val label: String,
        @Json(name = "description")
        val description: String?,
        @Json(name = "iconRight")
        val iconRight: SmallIconAtm?
    )

}