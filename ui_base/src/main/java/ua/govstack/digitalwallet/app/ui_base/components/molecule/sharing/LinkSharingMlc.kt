package ua.govstack.digitalwallet.app.ui_base.components.molecule.sharing

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.govstack.digitalwallet.app.core.models.common_compose.mlc.sharing.LinkSharingMlc
import ua.govstack.digitalwallet.app.ui_base.components.DiiaResourceIcon
import ua.govstack.digitalwallet.app.ui_base.components.atom.icon.SmallIconAtm
import ua.govstack.digitalwallet.app.ui_base.components.atom.icon.SmallIconAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.icon.toUiModel
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.UIElementData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIAction
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.SidePaddingMode
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.TopPaddingMode
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.toDp
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.toSidePaddingMode
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.toTopPaddingMode
import ua.govstack.digitalwallet.app.ui_base.components.theme.Black
import ua.govstack.digitalwallet.app.ui_base.components.theme.DiiaTextStyle

@Composable
fun LinkSharingMlc(
    modifier: Modifier = Modifier,
    data: LinkSharingMlcData,
    onUIAction: (UIAction) -> Unit
) {
    val hasIcon = data.iconRight != null

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = data.paddingHorizontal.toDp(defaultPadding = 0.dp),
                top = data.paddingTop.toDp(defaultPadding = 0.dp),
                end = data.paddingHorizontal.toDp(defaultPadding = 0.dp)
            )
            .height(24.dp)
            .testTag(data.componentId),
        verticalAlignment = Alignment.CenterVertically

    ) {
        Spacer(modifier = Modifier.width(32.dp))
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(end = if (hasIcon) 8.dp else 0.dp),
            text = data.label,
            color = Black,
            style = DiiaTextStyle.h3SmallHeading,
            maxLines = 1,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis
        )
        data.iconRight?.let {
            SmallIconAtm(data = it, onUIAction = onUIAction)
        }
        if (!hasIcon) {
            Spacer(
                modifier = Modifier.width(32.dp)
            )
        }
    }
}

data class LinkSharingMlcData(
    val componentId: String = "linkSharingMlc",
    val paddingTop: TopPaddingMode? = null,
    val paddingHorizontal: SidePaddingMode? = null,
    val iconRight: SmallIconAtmData? = null,
    val label: String
) : UIElementData


fun LinkSharingMlc.toUIModel(): LinkSharingMlcData {
    return LinkSharingMlcData(
        componentId = componentId,
        label = label,
        paddingTop = paddingMode?.top.toTopPaddingMode(),
        paddingHorizontal = paddingMode?.side.toSidePaddingMode(),
        iconRight = iconRight?.toUiModel(),
    )
}

enum class LinkSharingMlcMockTypes {
    NO_PADDINGS_LONG_TEX, PADDINGS, NO_PADDING, PADDINGS_LONG_TEXT, ICON_RIGHT
}

fun generateLinkSharingMlcMockData(type: LinkSharingMlcMockTypes): LinkSharingMlcData {
    return when (type) {
        LinkSharingMlcMockTypes.PADDINGS -> LinkSharingMlcData(
            label = "diia.gov.ua/diplink",
            paddingHorizontal = SidePaddingMode.MEDIUM,
            paddingTop = TopPaddingMode.LARGE
        )

        LinkSharingMlcMockTypes.ICON_RIGHT -> LinkSharingMlcData(
            label = "diia.gov.ua/diplink", iconRight = SmallIconAtmData(
                id = "1",
                code = DiiaResourceIcon.PLACEHOLDER.code,
                accessibilityDescription = "Button"
            )
        )

        LinkSharingMlcMockTypes.NO_PADDING -> LinkSharingMlcData(label = "diia.gov.ua/diplink")
        LinkSharingMlcMockTypes.PADDINGS_LONG_TEXT -> LinkSharingMlcData(
            label = "diia.com/label/label/label.labellabel/label",
            paddingHorizontal = SidePaddingMode.MEDIUM,
            paddingTop = TopPaddingMode.LARGE
        )

        LinkSharingMlcMockTypes.NO_PADDINGS_LONG_TEX -> LinkSharingMlcData(label = "diia.com/label/label/label.labellabel/label")
    }
}

@Composable
@Preview(showBackground = true)
fun LinkSharingMlc_Preview_no_paddings() {
    LinkSharingMlc(data = generateLinkSharingMlcMockData(LinkSharingMlcMockTypes.NO_PADDING)) {}
}

@Composable
@Preview(showBackground = true)
fun LinkSharingMlc_Preview_paddings() {
    LinkSharingMlc(
        data = generateLinkSharingMlcMockData(LinkSharingMlcMockTypes.PADDINGS)
    ) {}
}

@Composable
@Preview(showBackground = true)
fun LinkSharingMlc_Preview_no_paddings_long_text() {
    LinkSharingMlc(data = generateLinkSharingMlcMockData(LinkSharingMlcMockTypes.NO_PADDINGS_LONG_TEX)) {}
}


@Composable
@Preview(showBackground = true)
fun LinkSharingMlc_Preview_paddings_long_text() {
    LinkSharingMlc(data = generateLinkSharingMlcMockData(LinkSharingMlcMockTypes.PADDINGS_LONG_TEXT)) {}
}

@Composable
@Preview(showBackground = true)
fun LinkSharingMlcPreview_IconRight() {
    LinkSharingMlc(
        data = generateLinkSharingMlcMockData(LinkSharingMlcMockTypes.ICON_RIGHT)
    ) {}
}

@Composable
@Preview(showBackground = true)
fun LinkSharingMlc_Preview_paddings_con() {
    LinkSharingMlc(
        data = generateLinkSharingMlcMockData(LinkSharingMlcMockTypes.PADDINGS).copy(
            label = "Very very very very long label that woon't fit",
            iconRight = SmallIconAtmData(
                id = "1",
                code = DiiaResourceIcon.PLACEHOLDER.code,
                accessibilityDescription = "Button"
            )
        )
    ) {}
}