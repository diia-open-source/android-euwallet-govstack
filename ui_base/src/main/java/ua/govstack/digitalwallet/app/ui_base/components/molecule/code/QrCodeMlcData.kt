package ua.govstack.digitalwallet.app.ui_base.components.molecule.code

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.govstack.digitalwallet.app.core.models.common_compose.mlc.scancode.QrCodeMlc
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIActionKeysCompose
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.UiText
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.toDynamicString
import ua.govstack.digitalwallet.app.ui_base.components.subatomic.barcode.BarcodeAdapterView
import ua.govstack.digitalwallet.app.ui_base.components.subatomic.barcode.BarcodeType

@Composable
fun QrCodeMlc(
    modifier: Modifier = Modifier,
    data: QrCodeMlcData
) {
    BarcodeAdapterView(
        modifier = modifier,
        value = data.qrLink.asString(),
        type = BarcodeType.QR_CODE,
        width = 230.dp,
        height = 230.dp,
        blur = data.blured
    )
}

data class QrCodeMlcData(
    val actionKey: String = UIActionKeysCompose.QR_CODE_MLC,
    val componentId: UiText? = null,
    val qrLink: UiText,
    val blured: Boolean = false
)

fun QrCodeMlc.toUIModel(): QrCodeMlcData {
    return QrCodeMlcData(
        componentId = UiText.DynamicString(this.componentId.orEmpty()),
        qrLink = this.qrLink.toDynamicString()
    )
}

@Preview(showBackground = true)
@Composable
fun QrCodeMlc_Preview_Valid() {
    val data = QrCodeMlcData(
        qrLink = UiText.DynamicString("www.google.com")
    )
    QrCodeMlc(data = data)
}

@Preview(showBackground = true)
@Composable
fun QrCodeMlc_Preview_Blured() {
    val data = QrCodeMlcData(
        qrLink = UiText.DynamicString("www.google.com"),
        blured = true
    )
    QrCodeMlc(data = data)
}

