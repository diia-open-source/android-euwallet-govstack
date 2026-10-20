package ua.govstack.digitalwallet.app.ui_base.components.molecule.card

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ua.govstack.digitalwallet.app.core.models.common_compose.mlc.card.ServiceCardMlc
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.UIElementData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIAction
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIActionKeysCompose
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.UiIcon
import ua.govstack.digitalwallet.app.ui_base.components.theme.Black
import ua.govstack.digitalwallet.app.ui_base.components.theme.DiiaTextStyle
import ua.govstack.digitalwallet.app.ui_base.components.theme.WhiteAlpha50

@Composable
fun ServiceCardMlc(
    modifier: Modifier = Modifier,
    data: ServiceCardMlcData,
    onUIAction: (UIAction) -> Unit,
) {
    Box(
        modifier = modifier
            .height(112.dp)
            .background(
                color = WhiteAlpha50,
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                onUIAction(UIAction(actionKey = data.actionKey, data = data.id))
            }
    ) {
        Box(
            modifier = Modifier
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp
                )
                .size(32.dp)
                .align(Alignment.TopStart)
        )
        Text(
            modifier = Modifier
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
                .align(Alignment.BottomStart),
            text = data.label,
            style = DiiaTextStyle.t3TextBody,
            color = Black,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

data class ServiceCardMlcData(
    val actionKey: String = UIActionKeysCompose.PS_ITEM_CLICK,
    val id: String? = "",
    val label: String,
    val icon: UiIcon.DrawableResource
) : UIElementData

fun ServiceCardMlc.toUIModel(): ServiceCardMlcData {
    return ServiceCardMlcData(
        actionKey = this.action?.type ?: UIActionKeysCompose.PS_ITEM_CLICK,
        label = this.label,
        icon = UiIcon.DrawableResource(code = this.icon)
    )
}