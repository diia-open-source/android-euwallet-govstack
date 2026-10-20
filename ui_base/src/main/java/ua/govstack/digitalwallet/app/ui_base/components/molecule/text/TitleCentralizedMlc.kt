package ua.govstack.digitalwallet.app.ui_base.components.molecule.text

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.govstack.digitalwallet.app.core.models.common_compose.mlc.text.TitleCentralizedMlc
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.UIElementData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIActionKeysCompose
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.SidePaddingMode
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.TopPaddingMode
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.UiText
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.toDp
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.toSidePaddingMode
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.toTopPaddingMode
import ua.govstack.digitalwallet.app.ui_base.components.theme.DiiaTextStyle

@Composable
fun TitleCentralizedMlc(
    modifier: Modifier = Modifier,
    data: TitleCentralizedMlcData
) {
    Text(
        modifier = modifier
            .padding(
                start = data.paddingHorizontal.toDp(defaultPadding = 24.dp),
                top = data.paddingTop.toDp(defaultPadding = 8.dp),
                end = data.paddingHorizontal.toDp(defaultPadding = 24.dp)
            )
            .fillMaxWidth()
            .semantics {
                heading()
            }
            .testTag(data.componentId?.asString() ?: ""),
        textAlign = TextAlign.Center,
        text = data.label.asString(),
        style = DiiaTextStyle.h2MediumHeading
    )
}

data class TitleCentralizedMlcData(
    val actionKey: String = UIActionKeysCompose.TEXT_LABEL_MLC,
    val label: UiText,
    val componentId: UiText? = null,
    val paddingTop: TopPaddingMode? = null,
    val paddingHorizontal: SidePaddingMode? = null,
) : UIElementData

fun TitleCentralizedMlc.toUIModel(): TitleCentralizedMlcData {
    return TitleCentralizedMlcData(
        label = UiText.DynamicString(this.label),
        componentId = UiText.DynamicString(this.componentId.orEmpty()),
        paddingTop = paddingMode?.top.toTopPaddingMode(),
        paddingHorizontal = paddingMode?.side.toSidePaddingMode()
    )
}

fun generateTitleCentralizedMlcMockData(): TitleCentralizedMlcData {
    return TitleCentralizedMlcData(label = UiText.DynamicString("Продаж транспортного засобу"))
}

@Preview
@Composable
fun TitleCentralizedMlcPreview() {
    TitleCentralizedMlc(data = generateTitleCentralizedMlcMockData())
}