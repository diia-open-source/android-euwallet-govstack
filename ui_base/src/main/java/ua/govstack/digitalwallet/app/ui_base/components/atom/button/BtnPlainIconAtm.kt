package ua.govstack.digitalwallet.app.ui_base.components.atom.button

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.govstack.digitalwallet.app.core.models.common_compose.atm.button.BtnPlainIconAtm
import ua.govstack.digitalwallet.app.core.models.common_compose.general.ButtonStates
import ua.govstack.digitalwallet.app.ui_base.components.DiiaResourceIcon
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.DataActionWrapper
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIAction
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIActionKeysCompose
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.pushBundle
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.state.UIState
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.UiIcon
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.UiText
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.toDrawableResource
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.toDynamicString
import ua.govstack.digitalwallet.app.ui_base.components.noRippleClickable
import ua.govstack.digitalwallet.app.ui_base.components.subatomic.icon.UiIconWrapperSubatomic
import ua.govstack.digitalwallet.app.ui_base.components.subatomic.loader.LoaderCircularEclipse23Subatomic
import ua.govstack.digitalwallet.app.ui_base.components.theme.Black
import ua.govstack.digitalwallet.app.ui_base.components.theme.BlackAlpha30
import ua.govstack.digitalwallet.app.ui_base.components.theme.DiiaTextStyle
import ua.govstack.digitalwallet.app.ui_base.util.toDataActionWrapper

@Composable
fun BtnPlainIconAtm(
    modifier: Modifier = Modifier,
    data: BtnPlainIconAtmData,
    progressIndicator: Pair<String, Boolean> = Pair("", false),
    onUIAction: (UIAction) -> Unit
) {
    val componentId = data.componentId?.asString()

    val isLoading by remember {
        mutableStateOf(data.id == progressIndicator.first && progressIndicator.second)
    }

    Row(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .noRippleClickable(
                debounce = true,
                debounceTime = 1000L
            ) {
                if (data.interactionState == UIState.Interaction.Enabled) {
                    onUIAction(
                        UIAction(
                            actionKey = data.actionKey,
                            data = data.id,
                            action = data.action
                        ).pushBundle(
                            componentId = componentId,
                            action = data.action
                        )
                    )
                }
            }
            .testTag(data.componentId?.asString() ?: "")
            .semantics(mergeDescendants = true) {
                role = Role.Button
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedVisibility(visible = isLoading) {
            Row {
                LoaderCircularEclipse23Subatomic()
                Spacer(modifier = Modifier.width(8.dp))
            }
        }

        if (data.icon != null) {
            AnimatedVisibility(visible = !isLoading) {
                Row {
                    UiIconWrapperSubatomic(
                        modifier = Modifier
                            .size(24.dp)
                            .alpha(
                                if (data.interactionState == UIState.Interaction.Enabled) 1.0f else 0.3f
                            )
                            .semantics {
                                hideFromAccessibility()
                            },
                        icon = data.icon
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }

        Text(
            modifier = modifier,
            text = data.label.asString(),
            style = DiiaTextStyle.t2TextDescription,
            color = when (data.interactionState) {
                UIState.Interaction.Disabled -> BlackAlpha30
                UIState.Interaction.Enabled -> Black
            }
        )
    }
}

data class BtnPlainIconAtmData(
    val actionKey: String = UIActionKeysCompose.BTN_PLAIN_ICON_ATM,
    val id: String,
    val componentId: UiText? = null,
    val label: UiText,
    val icon: UiIcon?,
    val action: DataActionWrapper? = null,
    val interactionState: UIState.Interaction = UIState.Interaction.Enabled,
) {
    fun changeInteractionState(state: Boolean): BtnPlainIconAtmData {
        return this.copy(
            interactionState = if (state) UIState.Interaction.Enabled else UIState.Interaction.Disabled
        )
    }
}

fun BtnPlainIconAtm.toUiModel(
    id: String? = null,
    componentIdExternal: UiText? = null
): BtnPlainIconAtmData {
    return BtnPlainIconAtmData(
        id = id ?: UIActionKeysCompose.BTN_PLAIN_ICON_ATM,
        componentId = componentIdExternal ?: componentId.orEmpty().toDynamicString(),
        label = label.toDynamicString(),
        icon = icon?.toDrawableResource(),
        action = action?.toDataActionWrapper(),
        interactionState = when (state) {
            ButtonStates.enabled.name -> UIState.Interaction.Enabled
            ButtonStates.disabled.name -> UIState.Interaction.Disabled
            else -> UIState.Interaction.Enabled
        }
    )
}

fun generateMockBtnPlainIconAtmData(
    componentId: String = "btn_plain_icon_atm_0",
    diiaResourceIcon: DiiaResourceIcon = DiiaResourceIcon.PLACEHOLDER
) = BtnPlainIconAtmData(
    componentId = UiText.DynamicString(componentId),
    id = componentId,
    label = UiText.DynamicString("Label"),
    icon = UiIcon.DrawableResource(diiaResourceIcon.code),
    action = DataActionWrapper(
        type = "btn_plain_icon_atm_action"
    )
)

@Preview
@Composable
fun BtnPlainIconAtmPreview_Enabled() {
    val data = BtnPlainIconAtmData(
        id = "123",
        label = UiText.DynamicString("label"),
        icon = UiIcon.DrawableResource(DiiaResourceIcon.MENU.code),
        interactionState = UIState.Interaction.Enabled
    )
    BtnPlainIconAtm(
        modifier = Modifier,
        data = data
    ) {

    }
}

@Preview
@Composable
fun BtnPlainIconAtmPreview_Disabled() {
    val data = BtnPlainIconAtmData(
        id = "123",
        label = UiText.DynamicString("label"),
        icon = UiIcon.DrawableResource(DiiaResourceIcon.MENU.code),
        interactionState = UIState.Interaction.Disabled
    )
    BtnPlainIconAtm(
        modifier = Modifier,
        data = data
    ) {

    }
}

@Preview
@Composable
fun BtnPlainIconAtmPreview_Loading() {
    val data = BtnPlainIconAtmData(
        id = "123",
        label = UiText.DynamicString("label"),
        icon = UiIcon.DrawableResource(DiiaResourceIcon.MENU.code),
        interactionState = UIState.Interaction.Enabled
    )
    BtnPlainIconAtm(
        modifier = Modifier,
        data = data,
        progressIndicator = data.id to true
    ) {

    }
}