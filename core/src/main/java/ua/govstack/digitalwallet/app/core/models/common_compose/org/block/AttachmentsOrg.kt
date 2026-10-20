package ua.govstack.digitalwallet.app.core.models.common_compose.org.block

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ua.govstack.digitalwallet.app.core.models.common_compose.atm.button.BtnAddOptionAtm
import ua.govstack.digitalwallet.app.core.models.common_compose.mlc.card.AttachmentCardMlc

@JsonClass(generateAdapter = true)
data class AttachmentsOrg(
    @Json(name = "componentId")
    val componentId: String?,
    @Json(name = "inputCode")
    val inputCode: String?,
    @Json(name = "minNumber")
    val minNumber: Int?,
    @Json(name = "maxNumber")
    val maxNumber: Int?,
    @Json(name = "mandatory")
    val mandatory: Boolean?,
    @Json(name = "items")
    val items: List<Item>?,
    @Json(name = "btnAddOptionAtm")
    val btnAddOptionAtm: BtnAddOptionAtm?
) {

    @JsonClass(generateAdapter = true)
    data class Item(
        @Json(name = "attachmentCardMlc")
        val attachmentCardMlc: AttachmentCardMlc?
    )

}