package ua.govstack.digitalwallet.app.ui_base.components.molecule.message

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import ua.govstack.digitalwallet.app.core.models.common.message.AttentionIconMessageMlc
import ua.govstack.digitalwallet.app.ui_base.R
import ua.govstack.digitalwallet.app.ui_base.components.DiiaResourceIcon
import ua.govstack.digitalwallet.app.ui_base.components.atom.button.BtnStrokeAdditionalAtm
import ua.govstack.digitalwallet.app.ui_base.components.atom.button.ButtonStrokeAdditionalAtomData
import ua.govstack.digitalwallet.app.ui_base.components.atom.button.toUIModel
import ua.govstack.digitalwallet.app.ui_base.components.atom.icon.SmallIconAtm
import ua.govstack.digitalwallet.app.ui_base.components.atom.icon.SmallIconAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.icon.toUiModel
import ua.govstack.digitalwallet.app.ui_base.components.atom.text.textwithparameter.TextParameter
import ua.govstack.digitalwallet.app.ui_base.components.atom.text.textwithparameter.TextWithParametersAtom
import ua.govstack.digitalwallet.app.ui_base.components.atom.text.textwithparameter.TextWithParametersData
import ua.govstack.digitalwallet.app.ui_base.components.conditional
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.UIElementData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIAction
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIActionKeysCompose
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.state.UIState
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.SidePaddingMode
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.TopPaddingMode
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.UiText
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.toDynamicStringOrNull
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.toDp
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.toSidePaddingMode
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.toTopPaddingMode
import ua.govstack.digitalwallet.app.ui_base.components.molecule.list.table.items.tableblock.TableBlockItem
import ua.govstack.digitalwallet.app.ui_base.components.noRippleClickable
import ua.govstack.digitalwallet.app.ui_base.components.organism.list.pagination.SimplePagination
import ua.govstack.digitalwallet.app.ui_base.components.theme.AzureRadiance16
import ua.govstack.digitalwallet.app.ui_base.components.theme.Black
import ua.govstack.digitalwallet.app.ui_base.components.theme.DiiaTextStyle
import ua.govstack.digitalwallet.app.ui_base.components.theme.InfoYellow

private const val minLinesAllowed = 3
private const val maxLinesAllowed = 5

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun AttentionIconMessageMlc(
    modifier: Modifier = Modifier,
    data: AttentionIconMessageMlcData,
    onUIAction: (UIAction) -> Unit
) {
    val isExpanded = remember { mutableStateOf(data.isExpanded) }
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    BoxWithConstraints(
        modifier = modifier
            .padding(
                start = data.paddingHorizontal.toDp(defaultPadding = 24.dp),
                top = data.paddingTop.toDp(defaultPadding = 16.dp),
                end = data.paddingHorizontal.toDp(defaultPadding = 24.dp)
            )
            .fillMaxWidth()
            .background(
                color = when (data.backgroundMode) {
                    BackgroundMode.INFO -> InfoYellow
                    BackgroundMode.NOTE -> AzureRadiance16
                }, shape = RoundedCornerShape(16.dp)
            )
            .semantics(mergeDescendants = true) { }
            .testTag(data.componentId?.asString() ?: "")
    ) {
        val maxWidthPx = with(density) { maxWidth.roundToPx() }

        val textLayoutResult = textMeasurer.measure(
            text = data.text?.asString().orEmpty(),
            style = DiiaTextStyle.t2TextDescription,
            constraints = Constraints(maxWidth = maxWidthPx)
        )
        val fits = textLayoutResult.lineCount <= maxLinesAllowed

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.wrapContentWidth()) {
                data.icon?.let {
                    SmallIconAtm(
                        modifier = Modifier
                            .semantics {
                                if (it.accessibilityDescription == null) {
                                    hideFromAccessibility()
                                }
                            },
                        data = it,
                        onUIAction = onUIAction
                    )
                }
            }
            Column(modifier = Modifier.padding(start = 8.dp)) {
                data.title?.let {
                    Text(
                        text = data.title,
                        style = DiiaTextStyle.h4ExtraSmallHeading,
                        color = Black
                    )
                }

                data.text?.let {
                    TextWithParametersAtom(
                        modifier = Modifier
                            .conditional(data.title != null) {
                                padding(top = 8.dp)
                            },
                        data = TextWithParametersData(
                            actionKey = UIActionKeysCompose.TEXT_WITH_PARAMETERS,
                            text = data.text,
                            parameters = data.parameters
                        ),
                        style = DiiaTextStyle.t3TextBody,
                        onUIAction = onUIAction,
                        maxLines = if (isExpanded.value == false) minLinesAllowed else Int.MAX_VALUE
                    )
                }

                if (data.hasExpandedData == true && !fits) {
                    Row(modifier = Modifier.padding(top = 16.dp)) {
                        val text = if (isExpanded.value == true) {
                            data.collapsedText
                        } else {
                            data.expandedText
                        }
                        Text(
                            modifier = Modifier.noRippleClickable {
                                isExpanded.value = !(isExpanded.value ?: false)
                            },
                            text = text?.asString() ?: "",
                            style = DiiaTextStyle.t3TextBody,
                        )

                        Spacer(modifier = Modifier.size(4.dp))
                        Icon(
                            modifier = Modifier
                                .padding(start = 4.dp, top = 4.dp)
                                .size(8.dp, 8.dp),
                            painter = painterResource(
                                id = if (isExpanded.value == true) {
                                    R.drawable.ic_arrow_show_less
                                } else {
                                    R.drawable.ic_arrow_show_more
                                }
                            ),
                            contentDescription = stringResource(R.string.details),
                            tint = Black
                        )
                    }
                }

                data.btnStrokeAdditionalAtm?.let {
                    BtnStrokeAdditionalAtm(
                        modifier = Modifier.padding(top = 16.dp),
                        data = data.btnStrokeAdditionalAtm,
                        onUIAction = onUIAction,
                    )
                }
            }
        }
    }
}

data class AttentionIconMessageMlcData(
    override val id: String = "",
    val componentId: UiText? = null,
    val paddingTop: TopPaddingMode? = null,
    val paddingHorizontal: SidePaddingMode? = null,
    val icon: SmallIconAtmData?,
    val title: String? = null,
    val text: UiText?,
    val parameters: List<TextParameter>? = null,
    val backgroundMode: BackgroundMode,
    val hasExpandedData: Boolean? = null,
    val isExpanded: Boolean? = true,
    val expandedText: UiText? = null,
    val collapsedText: UiText? = null,
    val btnStrokeAdditionalAtm: ButtonStrokeAdditionalAtomData? = null
) : UIElementData, TableBlockItem, SimplePagination

enum class BackgroundMode {
    INFO, NOTE;
}

fun AttentionIconMessageMlc.toUiModel(): AttentionIconMessageMlcData {
    return AttentionIconMessageMlcData(
        componentId = this.componentId.toDynamicStringOrNull(),
        id = this.componentId.orEmpty(),
        icon = this.smallIconAtm?.toUiModel(),
        title = this.title,
        text = this.text?.let {
            UiText.DynamicString(it)
        },
        backgroundMode = when (this.backgroundMode) {
            AttentionIconMessageMlc.BackgroundMode.info -> BackgroundMode.INFO
            AttentionIconMessageMlc.BackgroundMode.note -> BackgroundMode.NOTE
        },
        paddingTop = paddingMode?.top.toTopPaddingMode(),
        paddingHorizontal = paddingMode?.side.toSidePaddingMode(),
        parameters = if (this.parameters != null) {
            mutableListOf<TextParameter>().apply {
                parameters?.forEach {
                    add(
                        TextParameter(
                            data = TextParameter.Data(
                                name = it.data?.name.toDynamicStringOrNull(),
                                resource = it.data?.resource.toDynamicStringOrNull(),
                                alt = it.data?.alt.toDynamicStringOrNull()
                            ),
                            type = it.type
                        )
                    )
                }
            }
        } else emptyList(),
        hasExpandedData = this.expanded != null,
        isExpanded = this.expanded?.isExpanded ?: true,
        expandedText = this.expanded?.expandedText?.let { UiText.DynamicString(it) },
        collapsedText = this.expanded?.collapsedText?.let { UiText.DynamicString(it) },
        btnStrokeAdditionalAtm = this.btnStrokeAdditionalAtm?.toUIModel()
    )
}

fun generateAttentionIconMessageMlcMockData(): AttentionIconMessageMlcData {
    return AttentionIconMessageMlcData(
        icon = SmallIconAtmData(code = DiiaResourceIcon.ELLIPSE_INFO.code),
        text = UiText.DynamicString("Щоб надіслати заяву, потрібно вказати всі дані."),
        backgroundMode = BackgroundMode.INFO
    )
}

fun generateAttentionIconMessageMlcExpandedMockData(): AttentionIconMessageMlcData {
    return AttentionIconMessageMlcData(
        icon = SmallIconAtmData(code = DiiaResourceIcon.ELLIPSE_INFO.code),
        text = UiText.DynamicString("Try not to write more than 10 lines of message in this component. Even if we have enough space, users have difficulty perceiving large amounts of information."),
        backgroundMode = BackgroundMode.NOTE,
        isExpanded = false,
        hasExpandedData = true,
        expandedText = UiText.DynamicString("Show more"),
        collapsedText = UiText.DynamicString("Show less"),
    )
}

fun generateAttentionIconMessageMlcMockDataCollapsedMockData(): AttentionIconMessageMlcData {
    return AttentionIconMessageMlcData(
        icon = SmallIconAtmData(code = DiiaResourceIcon.ELLIPSE_INFO.code),
        text = UiText.DynamicString("Try not to write more than 10 lines of message in this component. Even if we have enough space, users have difficulty perceiving large amounts of information. Try not to write more than 10 lines of message in this component. Even if we have enough space, users have difficulty perceiving large amounts of information."),
        backgroundMode = BackgroundMode.NOTE,
        isExpanded = true,
        hasExpandedData = true,
        expandedText = UiText.DynamicString("Show more"),
        collapsedText = UiText.DynamicString("Show less"),
    )
}

fun generateAttentionIconMessageMlcMockDataCollapsedBtnMockData(): AttentionIconMessageMlcData {
    return AttentionIconMessageMlcData(
        icon = SmallIconAtmData(code = DiiaResourceIcon.ELLIPSE_INFO.code),
        text = UiText.DynamicString("Try not to write more than 10 lines of message in this component. Even if we have enough space, users have difficulty perceiving large amounts of information. Try not to write more than 10 lines of message in this component. Even if we have enough space, users have difficulty perceiving large amounts of information."),
        backgroundMode = BackgroundMode.NOTE,
        isExpanded = true,
        hasExpandedData = true,
        expandedText = UiText.DynamicString("Show more"),
        collapsedText = UiText.DynamicString("Show less"),
        btnStrokeAdditionalAtm = ButtonStrokeAdditionalAtomData(
            id = "btnStrokeID",
            title = UiText.DynamicString("Button's text"),
            interactionState = UIState.Interaction.Enabled
        )
    )
}

fun generateAttentionIconMessageMlcMockDataBgNote(): AttentionIconMessageMlcData {
    return AttentionIconMessageMlcData(
        icon = SmallIconAtmData(code = DiiaResourceIcon.ELLIPSE_INFO.code),
        title = "Title",
        text = UiText.DynamicString("Щоб надіслати заяву, потрібно вказати всі дані."),
        backgroundMode = BackgroundMode.NOTE
    )
}

@Composable
@Preview
fun AttentionIconMessageMlcPreview() {
    AttentionIconMessageMlc(
        data = generateAttentionIconMessageMlcMockData()
    ) {}
}

@Composable
@Preview
fun AttentionIconMessageMlcPreview_NOTE() {
    AttentionIconMessageMlc(data = generateAttentionIconMessageMlcMockDataBgNote()) {}
}

@Composable
@Preview
fun AttentionIconMessageMlcPreview_expanded() {
    AttentionIconMessageMlc(data = generateAttentionIconMessageMlcExpandedMockData()) {}
}

@Composable
@Preview
fun AttentionIconMessageMlcPreview_collapsed() {
    AttentionIconMessageMlc(data = generateAttentionIconMessageMlcMockDataCollapsedMockData()) {}
}

@Composable
@Preview
fun AttentionIconMessageMlcPreview_collapsed_btn() {
    AttentionIconMessageMlc(data = generateAttentionIconMessageMlcMockDataCollapsedBtnMockData()) {}
}