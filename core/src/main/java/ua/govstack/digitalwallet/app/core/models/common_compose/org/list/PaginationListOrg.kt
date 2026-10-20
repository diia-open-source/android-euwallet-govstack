package ua.govstack.digitalwallet.app.core.models.common_compose.org.list

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ua.govstack.digitalwallet.app.core.models.common.message.AttentionIconMessageMlc
import ua.govstack.digitalwallet.app.core.models.common_compose.atm.media.ArticlePicAtm
import ua.govstack.digitalwallet.app.core.models.common_compose.atm.text.GreyTitleAtm
import ua.govstack.digitalwallet.app.core.models.common_compose.general.PaddingMode
import ua.govstack.digitalwallet.app.core.models.common_compose.mlc.card.CardMlc
import ua.govstack.digitalwallet.app.core.models.common_compose.mlc.card.CardMlcV2
import ua.govstack.digitalwallet.app.core.models.common_compose.mlc.card.HalvedCardMlc
import ua.govstack.digitalwallet.app.core.models.common_compose.mlc.card.ImageCardMlc
import ua.govstack.digitalwallet.app.core.models.common_compose.mlc.list.ListItemMlc
import ua.govstack.digitalwallet.app.core.models.common_compose.mlc.list.ListWidgetItemMlc
import ua.govstack.digitalwallet.app.core.models.common_compose.mlc.text.StubMessageMlc

@JsonClass(generateAdapter = true)
data class PaginationListOrg(
    @Json(name = "componentId")
    val componentId: String? = null,
    @Json(name = "items")
    val items: List<PaginationItem>? = null,
    @Json(name = "limit")
    val limit: Int?,
    @Json(name = "stubMessageMlc")
    val stubMessage: StubMessageMlc?,
    @Json(name = "paddingMode")
    val paddingMode: PaddingMode? = null,
)

@JsonClass(generateAdapter = true)
data class PaginationItem(
    @Json(name = "cardMlc")
    val cardMlc: CardMlc?,
    @Json(name = "listWidgetItemMlc")
    val listWidgetItemMlc: ListWidgetItemMlc?,
    @Json(name = "greyTitleAtm")
    val greyTitleAtm: GreyTitleAtm?,
    @Json(name = "listItemMlc")
    val listItemMlc: ListItemMlc?,
    @Json(name = "imageCardMlc")
    val imageCardMlc: ImageCardMlc?,
    @Json(name = "cardMlcV2")
    val cardMlcV2: CardMlcV2?,
    @Json(name = "articlePicAtm")
    val articlePicAtm: ArticlePicAtm?,
    @Json(name = "halvedCardMlc")
    val halvedCardMlc: HalvedCardMlc?,
    @Json(name = "stubMessageMlc")
    val stubMessageMlc: StubMessageMlc?,
    @Json(name = "attentionIconMessageMlc")
    val attentionIconMessageMlc: AttentionIconMessageMlc?
)