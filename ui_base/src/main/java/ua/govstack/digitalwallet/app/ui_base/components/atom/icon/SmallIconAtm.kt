package ua.govstack.digitalwallet.app.ui_base.components.atom.icon

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.govstack.digitalwallet.app.core.models.common_compose.atm.icon.SmallIconAtm
import ua.govstack.digitalwallet.app.ui_base.components.DiiaResourceIcon
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.DataActionWrapper
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIAction
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIActionKeysCompose
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.pushBundle
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.UiText
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.toDynamicStringOrNull
import ua.govstack.digitalwallet.app.ui_base.components.noRippleClickable
import ua.govstack.digitalwallet.app.ui_base.util.toDataActionWrapper

@Composable
fun SmallIconAtm(
    modifier: Modifier = Modifier,
    data: SmallIconAtmData,
    onUIAction: (UIAction) -> Unit = {}
) {
    Image(
        modifier = modifier
            .size(24.dp)
            .noRippleClickable {
                onUIAction(data.action())
            }
            .testTag(data.componentId?.asString() ?: ""),
        painter = painterResource(
            id = DiiaResourceIcon.getResourceId(data.code)
        ),
        contentDescription = data.accessibilityDescription
            ?: stringResource(id = DiiaResourceIcon.getContentDescription(data.code))
    )
}

data class SmallIconAtmData(
    val actionKey: String = UIActionKeysCompose.SMALL_ICON_ATM_DATA,
    val componentId: UiText? = null,
    val id: String? = null,
    val code: String,
    val accessibilityDescription: String? = null,
    val action: DataActionWrapper? = null
) {

    fun action(): UIAction {
        return UIAction(
            actionKey = actionKey,
            data = id,
            action = action
        ).pushBundle(
            componentId = (componentId as? UiText.DynamicString)?.value,
            action = action
        )
    }

}

fun SmallIconAtm.toUiModel(id: String? = null) = SmallIconAtmData(
    componentId = componentId.toDynamicStringOrNull(),
    id = id,
    code = code,
    accessibilityDescription = accessibilityDescription,
    action = action?.toDataActionWrapper()
)

fun generateMocKSmallIconAtmData(
    componentId: String = "small_icon_atm_0",
    diiaResourceIcon: DiiaResourceIcon = DiiaResourceIcon.PLACEHOLDER
) = SmallIconAtmData(
    componentId = UiText.DynamicString(componentId),
    code = diiaResourceIcon.code,
    action = DataActionWrapper(
        type = "small_icon_atm_action",
        resource = "small_icon_atm_resource"
    )
)

@Preview
@Composable
fun SmallIconAtmPreview() {
    SmallIconAtm(
        data = generateMocKSmallIconAtmData()
    )
}