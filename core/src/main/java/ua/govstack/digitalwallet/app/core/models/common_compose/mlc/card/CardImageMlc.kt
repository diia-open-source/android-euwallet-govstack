package ua.govstack.digitalwallet.app.core.models.common_compose.mlc.card

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ua.govstack.digitalwallet.app.core.models.common.message.AttentionIconMessageMlc
import ua.govstack.digitalwallet.app.core.models.common_compose.general.Action
import ua.govstack.digitalwallet.app.core.models.common_compose.general.PaddingMode

@JsonClass(generateAdapter = true)
data class CardImageMlc(
    @Json(name = "accessibilityDescription")
    val accessibilityDescription: String?,
    @Json(name = "action")
    val action: Action?,
    @Json(name = "attentionIconMessageMlc")
    val attentionIconMessageMlc: AttentionIconMessageMlc?,
    @Json(name = "componentId")
    val componentId: String?,
    @Json(name = "description")
    val description: String?,
    @Json(name = "gif")
    val gif: String?,
    @Json(name = "image")
    val image: String?,
    @Json(name = "label")
    val label: String?,
    @Json(name = "paddingMode")
    val paddingMode: PaddingMode?
)