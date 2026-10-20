package ua.govstack.digitalwallet.app.ui_base.components.atom.text

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import ua.govstack.digitalwallet.app.core.models.common_compose.atm.text.AmountAtm
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.UIElementData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.UiText
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.toDynamicString
import ua.govstack.digitalwallet.app.ui_base.components.theme.Black
import ua.govstack.digitalwallet.app.ui_base.components.theme.DarkGreen
import ua.govstack.digitalwallet.app.ui_base.components.theme.DiiaTextStyle
import ua.govstack.digitalwallet.app.ui_base.components.theme.Red

@Composable
fun AmountAtm(
    modifier: Modifier = Modifier,
    data: AmountAtmData
) {
    Text(
        modifier = modifier
            .testTag(data.componentId?.asString() ?: ""),
        text = data.value.asString(),
        style = DiiaTextStyle.t1BigText,
        color = when (data.color) {
            Color.RED -> Red
            Color.GREEN -> DarkGreen
            Color.BLACK -> Black
        }
    )
}

data class AmountAtmData(
    val value: UiText,
    val componentId: UiText? = null,
    val color: Color
) : UIElementData

enum class Color {
    RED,
    GREEN,
    BLACK
}

fun AmountAtm.toUIModel(): AmountAtmData {
    return AmountAtmData(
        componentId = this.componentId.orEmpty().toDynamicString(),
        value = this.value.toDynamicString(),
        color = when (this.colour) {
            AmountAtm.Color.red -> Color.RED
            AmountAtm.Color.black -> Color.BLACK
            AmountAtm.Color.green -> Color.GREEN
        }
    )
}

@Preview
@Composable
fun AmountAtmPreview() {
    val data = AmountAtmData(
        value = UiText.DynamicString("+3 000.00 ₴"),
        color = Color.GREEN
    )
    AmountAtm(data = data)
}