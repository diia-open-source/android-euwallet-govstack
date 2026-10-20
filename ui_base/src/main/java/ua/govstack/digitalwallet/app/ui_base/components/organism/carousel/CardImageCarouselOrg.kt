package ua.govstack.digitalwallet.app.ui_base.components.organism.carousel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.govstack.digitalwallet.app.core.models.common_compose.org.carousel.CardImageCarouselOrg
import ua.govstack.digitalwallet.app.ui_base.components.DiiaResourceIcon
import ua.govstack.digitalwallet.app.ui_base.components.DiiaResourceRaw
import ua.govstack.digitalwallet.app.ui_base.components.atom.icon.SmallIconAtmData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIAction
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIActionKeysCompose
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.SidePaddingMode
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.TopPaddingMode
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.UiText
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.toDp
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.toSidePaddingMode
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.toTopPaddingMode
import ua.govstack.digitalwallet.app.ui_base.components.molecule.card.CardImageMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.card.toUiModel
import ua.govstack.digitalwallet.app.ui_base.components.molecule.message.AttentionIconMessageMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.message.BackgroundMode

@Composable
fun CardImageCarouselOrg(
    modifier: Modifier = Modifier,
    data: CardImageCarouselOrgData,
    onUIAction: (UIAction) -> Unit
) {
    BaseSimpleCarouselInternal(
        modifier = modifier
            .padding(
                start = data.paddingHorizontal.toDp(defaultPadding = 0.dp),
                top = data.paddingTop.toDp(defaultPadding = 8.dp),
                end = data.paddingHorizontal.toDp(defaultPadding = 0.dp)
            )
            .testTag(data.componentId?.asString() ?: ""),
        data = data,
        onUIAction = {
            onUIAction(
                UIAction(
                    actionKey = data.actionKey,
                    data = it.data,
                    action = it.action
                )
            )
        }
    )
}

data class CardImageCarouselOrgData(
    val actionKey: String = UIActionKeysCompose.CARD_IMAGE_CAROUSEL_ORG,
    val id: String? = null,
    val componentId: UiText? = null,
    val paddingTop: TopPaddingMode? = null,
    val paddingHorizontal: SidePaddingMode? = null,
    override val items: List<SimpleCarouselCard>,
) : BaseSimpleCarouselInternalData

fun CardImageCarouselOrg.toUiModel(id: String? = null): CardImageCarouselOrgData {
    return CardImageCarouselOrgData(
        id = id,
        componentId = componentId?.let { UiText.DynamicString(it) },
        paddingTop = this.paddingMode?.top.toTopPaddingMode(),
        paddingHorizontal = this.paddingMode?.side.toSidePaddingMode(),
        items = mutableListOf<SimpleCarouselCard>().apply {
            items?.forEach {
                it.cardImageMlc?.let { cardImage ->
                    add(cardImage.toUiModel())
                }
            }
        } as List<SimpleCarouselCard>
    )
}

fun generateCardImageCarouselMockData(): CardImageCarouselOrgData {
    val card = CardImageMlcData(
        id = "",
        label = UiText.DynamicString("label"),
        description = UiText.DynamicString("description"),
        image = "https://design.google/library/evolving-google-identity",
        attentionIconMessageMlc = AttentionIconMessageMlcData(
            icon = SmallIconAtmData(code = DiiaResourceIcon.ELLIPSE_INFO.code),
            text = UiText.DynamicString("Щоб надіслати заяву, потрібно вказати всі дані."),
            backgroundMode = BackgroundMode.NOTE
        )
    )
    val cardDefault = CardImageMlcData(
        id = "",
        label = UiText.DynamicString("label"),
        description = UiText.DynamicString("description"),
        gif = DiiaResourceRaw.DEFAULT_GLOBAL.code,
        attentionIconMessageMlc = AttentionIconMessageMlcData(
            icon = SmallIconAtmData(code = DiiaResourceIcon.ELLIPSE_INFO.code),
            text = UiText.DynamicString("Щоб надіслати заяву, потрібно вказати всі дані."),
            backgroundMode = BackgroundMode.NOTE
        )
    )
    val cards = listOf(cardDefault, card, card, card)
    return CardImageCarouselOrgData(
        items = cards
    )
}

@Preview
@Composable
fun CardImageCarouselOrgPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray),
        contentAlignment = Alignment.Center
    ) {
        val data = generateCardImageCarouselMockData()
        CardImageCarouselOrg(
            data = data,
            onUIAction = {
                /* no-op */
            }
        )
    }
}
