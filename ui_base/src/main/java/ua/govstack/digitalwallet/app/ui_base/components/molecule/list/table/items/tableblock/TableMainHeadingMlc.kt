package ua.govstack.digitalwallet.app.ui_base.components.molecule.list.table.items.tableblock

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.govstack.digitalwallet.app.core.models.common_compose.table.TableMainHeadingMlc
import ua.govstack.digitalwallet.app.ui_base.components.atom.icon.IconAtm
import ua.govstack.digitalwallet.app.ui_base.components.atom.icon.IconAtmData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.UIElementData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIAction
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIActionKeysCompose
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.SidePaddingMode
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.TopPaddingMode
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.UiText
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.toDynamicString
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.toDp
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.toSidePaddingMode
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.toTopPaddingMode
import ua.govstack.digitalwallet.app.ui_base.components.noRippleClickable
import ua.govstack.digitalwallet.app.ui_base.components.theme.Black
import ua.govstack.digitalwallet.app.ui_base.components.theme.BlackAlpha50
import ua.govstack.digitalwallet.app.ui_base.components.theme.DiiaTextStyle
import ua.govstack.digitalwallet.app.ui_base.util.toUiModel

@Composable
fun TableMainHeadingMlc(
    modifier: Modifier = Modifier,
    data: TableMainHeadingMlcData,
    onUIAction: (UIAction) -> Unit
) {
    val componentId = data.componentId?.asString().orEmpty()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = data.paddingHorizontal.toDp(defaultPadding = 0.dp),
                top = data.paddingTop.toDp(defaultPadding = 0.dp),
                end = data.paddingHorizontal.toDp(defaultPadding = 0.dp)
            )
            .testTag(componentId)
            .semantics(mergeDescendants = true) { }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(end = 16.dp)
        ) {
            data.title?.let { lTitle ->
                Text(
                    modifier = Modifier
                        .focusable(),
                    text = lTitle.asString(),
                    color = Black,
                    style = DiiaTextStyle.h4ExtraSmallHeading
                )
            }
            val description = data.description?.asString()
            if (!description.isNullOrBlank()) {
                Text(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .focusable(),
                    text = description,
                    color = BlackAlpha50,
                    style = DiiaTextStyle.t2TextDescription
                )
            }
        }
        data.iconAtmData?.let { lIconAtmData ->
            IconAtm(
                modifier = Modifier
                    .size(24.dp)
                    .focusable(),
                data = lIconAtmData,
                onUIAction = {
                    onUIAction(
                        UIAction(
                            actionKey = UIActionKeysCompose.TABLE_MAIN_HEADING_MLC,
                            data = it.data,
                            action = it.action
                        )
                    )
                }
            )
        }
    }
}

data class TableMainHeadingMlcData(
    val componentId: UiText? = null,
    val title: UiText? = null,
    val description: UiText? = null,
    val iconAtmData: IconAtmData? = null,
    val paddingTop: TopPaddingMode? = null,
    val paddingHorizontal: SidePaddingMode? = null,
) : UIElementData

fun TableMainHeadingMlc.toUIModel() = TableMainHeadingMlcData(
    componentId = componentId?.let { UiText.DynamicString(it) },
    title = UiText.DynamicString(label),
    description = description?.let { UiText.DynamicString(it) },
    iconAtmData = icon?.toUiModel(),
    paddingTop = paddingMode?.top.toTopPaddingMode(),
    paddingHorizontal = paddingMode?.side.toSidePaddingMode(),
)

fun UiText?.toTableMainHeadingMlcData() = TableMainHeadingMlcData(
    componentId = "table_main_heading_mlc".toDynamicString(),
    title = this,
    description = null,
    iconAtmData = null
)

@Composable
@Preview
fun TableMainHeadingMlcPreview() {
    val state = TableMainHeadingMlcData(
        title = "Heading".toDynamicString(),
        iconAtmData = IconAtmData(
            code = "copy"
        )
    )
    TableMainHeadingMlc(
        data = state
    ) {
        /* no-op */
    }
}

@Composable
@Preview
fun TableMainHeadingMlcWithDescriptionPreview() {
    val state = TableMainHeadingMlcData(
        title = "Heading".toDynamicString(),
        description = "Description".toDynamicString(),
        iconAtmData = IconAtmData(
            code = "copy"
        )
    )
    TableMainHeadingMlc(
        data = state
    ) {
        /* no-op */
    }
}