package ua.govstack.digitalwallet.app.ui_base.components.molecule.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.govstack.digitalwallet.app.core.models.common_compose.mlc.card.AttachmentCardMlc
import ua.govstack.digitalwallet.app.ui_base.components.DiiaResourceIcon
import ua.govstack.digitalwallet.app.ui_base.components.atom.button.generateMockBtnPlainIconAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.icon.SmallIconAtm
import ua.govstack.digitalwallet.app.ui_base.components.atom.icon.SmallIconAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.icon.generateMocKSmallIconAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.icon.toUiModel
import ua.govstack.digitalwallet.app.ui_base.components.atom.status.ChipStatusAtm
import ua.govstack.digitalwallet.app.ui_base.components.atom.status.ChipStatusAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.status.StatusChipType
import ua.govstack.digitalwallet.app.ui_base.components.atom.status.generateChipStatusAtmMockData
import ua.govstack.digitalwallet.app.ui_base.components.atom.status.toUiModel
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.UIElementData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIAction
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIActionKeysCompose.ATTACHMENT_CARD_MLC
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.pushBundle
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.state.UIState
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.SidePaddingMode
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.TopPaddingMode
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.UiText
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.toDp
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.toSidePaddingMode
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.toTopPaddingMode
import ua.govstack.digitalwallet.app.ui_base.components.molecule.button.BtnIconPlainGroupMlc
import ua.govstack.digitalwallet.app.ui_base.components.molecule.button.BtnIconPlainGroupMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.button.toUIModel
import ua.govstack.digitalwallet.app.ui_base.components.noRippleClickable
import ua.govstack.digitalwallet.app.ui_base.components.theme.Black
import ua.govstack.digitalwallet.app.ui_base.components.theme.BlackAlpha30
import ua.govstack.digitalwallet.app.ui_base.components.theme.BlackAlpha54
import ua.govstack.digitalwallet.app.ui_base.components.theme.BlackSqueeze
import ua.govstack.digitalwallet.app.ui_base.components.theme.DiiaTextStyle
import ua.govstack.digitalwallet.app.ui_base.components.theme.White

@Composable
fun AttachmentCardMlc(
    modifier: Modifier = Modifier,
    data: AttachmentCardMlcData,
    onUIAction: (UIAction) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = data.paddingHorizontal.toDp(defaultPadding = 16.dp),
                top = data.paddingTop.toDp(defaultPadding = 8.dp),
                end = data.paddingHorizontal.toDp(defaultPadding = 16.dp)
            )
            .background(
                color = White,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            data.chipStatusAtm?.let { lChipStatusAtm ->
                ChipStatusAtm(
                    modifier = Modifier
                        .align(Alignment.TopStart),
                    data = lChipStatusAtm
                )
            }
            data.iconRight?.let { lIconRight ->
                val uiAction = lIconRight
                    .action()
                    .copy(
                        actionKey = data.actionKey
                    ).pushBundle(
                        componentId = data.componentId
                    )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(44.dp)
                        .noRippleClickable {
                            onUIAction(uiAction)
                        }
                ) {
                    SmallIconAtm(
                        modifier = Modifier
                            .align(Alignment.TopEnd),
                        data = lIconRight,
                        onUIAction = {
                            onUIAction(uiAction)
                        }
                    )
                }
            }
        }
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = if (data.chipStatusAtm != null && data.iconRight == null) 22.dp else 0.dp),
            style = DiiaTextStyle.t1BigText,
            text = data.label,
            color = Black,
            maxLines = 1
        )
        data.description?.let { lDescription ->
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                style = DiiaTextStyle.t2TextDescription,
                text = lDescription,
                color = BlackAlpha54,
                maxLines = 1
            )
        }
        data.btnIconPlainGroupMlc?.let { lBtnIconPlainGroupMlc ->
            BtnIconPlainGroupMlc(
                modifier = Modifier
                    .padding(top = 16.dp),
                data = lBtnIconPlainGroupMlc,
                onUIAction = { uiAction ->
                    onUIAction(
                        uiAction.copy(
                            actionKey = data.actionKey
                        ).pushBundle(
                            componentId = data.componentId
                        )
                    )
                }
            )
        }
        data.key?.let { lKey ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    thickness = 1.dp,
                    color = BlackSqueeze
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1F)
                    ) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth(),
                            style = DiiaTextStyle.t1BigText,
                            text = lKey.label,
                            color = when (lKey.interactionState) {
                                is UIState.Interaction.Enabled -> Black
                                is UIState.Interaction.Disabled -> BlackAlpha30
                            },
                            maxLines = 1
                        )
                        lKey.description?.let { lDescription ->
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp),
                                style = DiiaTextStyle.t2TextDescription,
                                text = lDescription,
                                color = when (lKey.interactionState) {
                                    is UIState.Interaction.Enabled -> BlackAlpha54
                                    is UIState.Interaction.Disabled -> BlackAlpha30
                                },
                                maxLines = 1
                            )
                        }
                    }
                    lKey.iconRight?.let { lIconRight ->
                        val uiAction = lIconRight
                            .action()
                            .copy(
                                actionKey = data.actionKey
                            ).pushBundle(
                                componentId = data.componentId
                            )
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                        ) {
                            SmallIconAtm(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .noRippleClickable {
                                        onUIAction(uiAction)
                                    },
                                data = lIconRight,
                                onUIAction = {
                                    onUIAction(uiAction)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

data class AttachmentCardMlcData(
    val actionKey: String = ATTACHMENT_CARD_MLC,
    val componentId: String? = null,
    val paddingTop: TopPaddingMode? = null,
    val paddingHorizontal: SidePaddingMode? = null,
    val id: String? = null,
    val chipStatusAtm: ChipStatusAtmData? = null,
    val iconRight: SmallIconAtmData? = null,
    val label: String,
    val description: String? = null,
    val key: KeyItemData? = null,
    val btnIconPlainGroupMlc: BtnIconPlainGroupMlcData? = null
) : UIElementData {

    data class KeyItemData(
        val id: String? = null,
        val label: String,
        val description: String? = null,
        val iconRight: SmallIconAtmData? = null,
        val interactionState: UIState.Interaction = UIState.Interaction.Enabled
    )

}

fun AttachmentCardMlc.toUiModel() = AttachmentCardMlcData(
    componentId = componentId,
    paddingTop = paddingMode?.top.toTopPaddingMode(),
    paddingHorizontal = paddingMode?.side.toSidePaddingMode(),
    id = id,
    chipStatusAtm = chipStatusAtm?.toUiModel(),
    iconRight = iconRight?.toUiModel(),
    label = label,
    description = description,
    key = key?.let { lKey ->
        AttachmentCardMlcData.KeyItemData(
            id = lKey.id,
            label = lKey.label,
            description = lKey.description,
            iconRight = lKey.iconRight?.toUiModel()
        )
    },
    btnIconPlainGroupMlc = btnIconPlainGroupMlc.toUIModel()
)

enum class MockAttachmentCardMlcTypes {
    WithoutKey, WithKey
}

fun generateMockAttachmentCardMlcData(
    componentId: String = "attachment_card_mlc_0",
    mockAttachmentCardMlcTypes: MockAttachmentCardMlcTypes = MockAttachmentCardMlcTypes.WithoutKey
) = AttachmentCardMlcData(
    componentId = componentId,
    chipStatusAtm = generateChipStatusAtmMockData(StatusChipType.BLUE),
    iconRight = generateMocKSmallIconAtmData(
        componentId = "icon_right_$componentId",
        diiaResourceIcon = DiiaResourceIcon.DELETE
    ),
    label = "Label",
    description = "Description",
    key = when (mockAttachmentCardMlcTypes) {
        MockAttachmentCardMlcTypes.WithoutKey -> {
            null
        }

        MockAttachmentCardMlcTypes.WithKey -> {
            val keyItemComponentId = "key_item_$componentId"
            AttachmentCardMlcData.KeyItemData(
                id = keyItemComponentId,
                label = "Label",
                description = "Description",
                iconRight = generateMocKSmallIconAtmData(
                    componentId = "small_icon_atm_$keyItemComponentId",
                    diiaResourceIcon = DiiaResourceIcon.RESTORE
                )
            )
        }
    },
    btnIconPlainGroupMlc = when (mockAttachmentCardMlcTypes) {
        MockAttachmentCardMlcTypes.WithoutKey -> {
            val btnIconPlainGroupMlcComponentId = "btn_icon_plain_group_mlc_$componentId"
            BtnIconPlainGroupMlcData(
                componentId = UiText.DynamicString(btnIconPlainGroupMlcComponentId),
                listBtn = listOf(
                    generateMockBtnPlainIconAtmData(
                        componentId = btnIconPlainGroupMlcComponentId,
                        diiaResourceIcon = DiiaResourceIcon.KEY
                    )
                ),
                paddingTop = TopPaddingMode.MEDIUM,
                paddingHorizontal = SidePaddingMode.NONE
            )
        }

        MockAttachmentCardMlcTypes.WithKey -> {
            null
        }
    }
)

@Preview
@Composable
fun AttachmentCardMlcPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackSqueeze)
            .statusBarsPadding()
    ) {
        AttachmentCardMlc(
            data = generateMockAttachmentCardMlcData(
                mockAttachmentCardMlcTypes = MockAttachmentCardMlcTypes.WithoutKey
            ),
            onUIAction = { uiAction ->
                /* no-op */
            }
        )
        AttachmentCardMlc(
            data = generateMockAttachmentCardMlcData(
                mockAttachmentCardMlcTypes = MockAttachmentCardMlcTypes.WithKey
            ),
            onUIAction = {
                /* no-op */
            }
        )
    }
}