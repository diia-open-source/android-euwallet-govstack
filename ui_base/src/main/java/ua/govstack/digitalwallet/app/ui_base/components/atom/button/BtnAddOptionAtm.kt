package ua.govstack.digitalwallet.app.ui_base.components.atom.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.govstack.digitalwallet.app.core.models.common_compose.atm.button.BtnAddOptionAtm
import ua.govstack.digitalwallet.app.core.models.common_compose.general.ButtonStates
import ua.govstack.digitalwallet.app.ui_base.components.DiiaResourceIcon
import ua.govstack.digitalwallet.app.ui_base.components.atom.icon.SmallIconAtm
import ua.govstack.digitalwallet.app.ui_base.components.atom.icon.SmallIconAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.icon.generateMocKSmallIconAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.icon.toUiModel
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.DataActionWrapper
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.UIElementData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIAction
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIActionKeysCompose.BTN_ADD_OPTION_ATM
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.pushBundle
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.state.UIState
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.SidePaddingMode
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.TopPaddingMode
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.toDp
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.toSidePaddingMode
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.toTopPaddingMode
import ua.govstack.digitalwallet.app.ui_base.components.noRippleClickable
import ua.govstack.digitalwallet.app.ui_base.components.theme.Black
import ua.govstack.digitalwallet.app.ui_base.components.theme.BlackAlpha54
import ua.govstack.digitalwallet.app.ui_base.components.theme.BlackSqueeze
import ua.govstack.digitalwallet.app.ui_base.components.theme.DiiaTextStyle
import ua.govstack.digitalwallet.app.ui_base.components.theme.White
import ua.govstack.digitalwallet.app.ui_base.components.theme.WhiteAlpha50
import ua.govstack.digitalwallet.app.ui_base.util.toDataActionWrapper

@Composable
fun BtnAddOptionAtm(
    modifier: Modifier = Modifier,
    data: BtnAddOptionAtmData,
    onUIAction: (UIAction) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = data.paddingHorizontal.toDp(defaultPadding = 16.dp),
                top = data.paddingTop.toDp(defaultPadding = 8.dp),
                end = data.paddingHorizontal.toDp(defaultPadding = 16.dp)
            )
            .background(
                color = when (data.interactionState) {
                    is UIState.Interaction.Enabled -> White
                    is UIState.Interaction.Disabled -> WhiteAlpha50
                },
                shape = RoundedCornerShape(16.dp)
            )
            .noRippleClickable {
                if (data.interactionState == UIState.Interaction.Enabled) {
                    onUIAction(
                        UIAction(
                            actionKey = BTN_ADD_OPTION_ATM,
                            action = data.action
                        ).pushBundle(
                            componentId = data.componentId,
                            action = data.action
                        )
                    )
                }
            }
            .padding(16.dp)
            .alpha(
                alpha = when (data.interactionState) {
                    is UIState.Interaction.Enabled -> 1F
                    is UIState.Interaction.Disabled -> 0.5F
                }
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            data.iconLeft?.let { lIconLeft ->
                SmallIconAtm(
                    data = lIconLeft,
                    onUIAction = { uiAction ->
                        if (data.interactionState == UIState.Interaction.Enabled) {
                            onUIAction(
                                uiAction.copy(
                                    actionKey = BTN_ADD_OPTION_ATM
                                ).pushBundle(
                                    componentId = data.componentId
                                )
                            )
                        }
                    }
                )
            }
            Column(
                modifier = Modifier
                    .weight(1F),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    style = DiiaTextStyle.t1BigText,
                    text = data.label,
                    color = Black,
                    maxLines = 1
                )
                data.description?.let { lDescription ->
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        style = DiiaTextStyle.t2TextDescription,
                        text = lDescription,
                        color = BlackAlpha54,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

data class BtnAddOptionAtmData(
    val actionKey: String = BTN_ADD_OPTION_ATM,
    val componentId: String? = null,
    val paddingTop: TopPaddingMode? = null,
    val paddingHorizontal: SidePaddingMode? = null,
    val label: String,
    val description: String? = null,
    val iconLeft: SmallIconAtmData? = null,
    val interactionState: UIState.Interaction = UIState.Interaction.Enabled,
    val action: DataActionWrapper? = null
) : UIElementData

fun BtnAddOptionAtm.toUiModel() = BtnAddOptionAtmData(
    componentId = componentId,
    paddingTop = paddingMode?.top.toTopPaddingMode(),
    paddingHorizontal = paddingMode?.side.toSidePaddingMode(),
    label = label,
    description = description,
    iconLeft = iconLeft?.toUiModel(),
    interactionState = when (this.state) {
        ButtonStates.enabled -> UIState.Interaction.Enabled
        ButtonStates.disabled -> UIState.Interaction.Disabled
        ButtonStates.invisible -> UIState.Interaction.Disabled
        else -> UIState.Interaction.Enabled
    },
    action = action?.toDataActionWrapper()
)

fun generateMockBtnAddOptionAtmData(
    componentId: String = "btn_add_option_atm_0",
    interactionState: UIState.Interaction = UIState.Interaction.Enabled
) = BtnAddOptionAtmData(
    componentId = componentId,
    label = "Label",
    description = "Description",
    iconLeft = generateMocKSmallIconAtmData(
        componentId = "small_icon_atm_$componentId",
        diiaResourceIcon = DiiaResourceIcon.ICON_ADD
    ),
    interactionState = interactionState,
    action = DataActionWrapper(
        type = "btn_add_option_atm_action",
        resource = "btn_add_option_atm_resource"
    )
)

@Preview
@Composable
fun BtnAddOptionAtmPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackSqueeze)
            .statusBarsPadding()
    ) {
        BtnAddOptionAtm(
            data = generateMockBtnAddOptionAtmData(),
            onUIAction = {
                /* no-op */
            }
        )
        BtnAddOptionAtm(
            data = generateMockBtnAddOptionAtmData(
                interactionState = UIState.Interaction.Disabled
            ),
            onUIAction = {
                /* no-op */
            }
        )
    }
}