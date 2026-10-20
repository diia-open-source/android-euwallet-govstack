package ua.govstack.digitalwallet.app.ui_base.components.organism.qr

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.govstack.digitalwallet.app.core.models.common_compose.org.qr.QrCodeOrg
import ua.govstack.digitalwallet.app.ui_base.components.DiiaResourceIcon
import ua.govstack.digitalwallet.app.ui_base.components.atom.button.BtnPlainIconAtm
import ua.govstack.digitalwallet.app.ui_base.components.atom.button.BtnPlainIconAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.button.toUiModel
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.UIElementData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIAction
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIActionKeysCompose
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.state.UIState
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.UiIcon
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.UiText
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.toDynamicString
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.toDynamicStringOrNull
import ua.govstack.digitalwallet.app.ui_base.components.molecule.code.QrCodeMlc
import ua.govstack.digitalwallet.app.ui_base.components.molecule.code.QrCodeMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.code.toUIModel
import ua.govstack.digitalwallet.app.ui_base.components.molecule.message.PaginationMessageMlc
import ua.govstack.digitalwallet.app.ui_base.components.molecule.message.PaginationMessageMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.message.generatePaginationMessageMlcMockData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.message.toUIModel
import ua.govstack.digitalwallet.app.ui_base.components.subatomic.timer.ExpireLabel
import ua.govstack.digitalwallet.app.ui_base.components.theme.BlackAlpha54
import ua.govstack.digitalwallet.app.ui_base.components.theme.DiiaTextStyle

@Composable
fun QrCodeOrg(
    modifier: Modifier = Modifier,
    data: QrCodeOrgData,
    onUIAction: (UIAction) -> Unit
) {
    var expired by remember { mutableStateOf(false) }

    var measuredHeightPx by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current
    val measuredHeightDp = with(density) { measuredHeightPx.toDp() }
    val heightModifier = if (measuredHeightPx > 0) {
        Modifier.height(measuredHeightDp)
    } else {
        Modifier.wrapContentHeight()
    }
    Box(
        modifier = modifier
            .testTag(tag = data.componentId)
            .height(390.dp)
    ) {
        if (data.paginationMessageMlcData != null) {
            Column {
                Spacer(
                    modifier = Modifier
                        .height(94.dp)
                )
                PaginationMessageMlc(
                    modifier = Modifier.fillMaxWidth(),
                    data = data.paginationMessageMlcData
                ) {
                    onUIAction(
                        UIAction(
                            actionKey = data.actionKey,
                            data = it.data,
                            action = it.action
                        )
                    )
                }
            }
        } else if (expired && data.expirationPaginationMessageMlc != null) {
            Column {
                Spacer(
                    modifier = Modifier
                        .height(72.dp)
                )
                PaginationMessageMlc(
                    modifier = heightModifier,
                    data = data.expirationPaginationMessageMlc,
                    onUIAction = onUIAction
                )
            }
        } else {
            Column(
                modifier = modifier
                    .wrapContentWidth()
                    .onGloballyPositioned { cords ->
                        measuredHeightPx = cords.size.height
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                data.text?.let {
                    Text(
                        modifier = Modifier
                            .height(48.dp)
                            .fillMaxWidth(),
                        text = data.text, style = DiiaTextStyle.t2TextDescription,
                        textAlign = TextAlign.Center
                    )
                } ?: Spacer(modifier = Modifier.height(16.dp))

                data.qrCode?.let {
                    Spacer(
                        modifier = Modifier
                            .height(16.dp)
                            .fillMaxWidth()
                    )
                    QrCodeMlc(
                        modifier = Modifier
                            .padding(start = 32.dp, end = 32.dp), data = it
                    )
                    Spacer(
                        modifier = Modifier
                            .height(16.dp)
                            .fillMaxWidth()
                    )
                }
                if (data.timer != null && data.expireLabelFirst != null) {
                    ExpireLabel(
                        expireLabelFirst = data.expireLabelFirst,
                        timer = data.timer,
                        expireLabelLast = data.expireLabelLast,
                        expired = expired
                    ) {
                        expired = true
                    }
                } else if (data.expireLabelWithoutTimer != null) {
                    Text(
                        text = data.expireLabelWithoutTimer,
                        style = DiiaTextStyle.t4TextSmallDescription,
                        color = BlackAlpha54
                    )
                } else {
                    Spacer(
                        modifier = Modifier
                            .height(16.dp)
                            .fillMaxWidth()
                    )
                }

                if (data.text == null) {
                    Spacer(
                        modifier = Modifier
                            .height(32.dp)
                            .fillMaxWidth()
                    )
                }
                data.btnPlainIconAtm?.let {
                    Spacer(
                        modifier = Modifier
                            .height(16.dp)
                            .fillMaxWidth()
                    )
                    BtnPlainIconAtm(
                        data = data.btnPlainIconAtm
                    ) {
                        onUIAction(
                            UIAction(
                                actionKey = data.actionKey,
                                data = it.data,
                                action = it.action
                            )
                        )
                    }
                    Spacer(
                        modifier = Modifier
                            .height(24.dp)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

data class QrCodeOrgData(
    val actionKey: String = UIActionKeysCompose.QR_CODE_ORG,
    val componentId: String = "qrCodeOrg",
    val text: String? = null,
    val qrCode: QrCodeMlcData? = null,
    val expireLabelFirst: UiText? = null,
    val expireLabelLast: UiText? = null,
    val timer: Int? = null,
    val expireLabelWithoutTimer: String? = null,
    val expirationPaginationMessageMlc: PaginationMessageMlcData? = null,
    val paginationMessageMlcData: PaginationMessageMlcData? = null,
    val btnPlainIconAtm: BtnPlainIconAtmData? = null,
) : UIElementData

fun QrCodeOrg.toUIModel(): QrCodeOrgData {
    return QrCodeOrgData(
        componentId = this.componentId,
        text = text,
        qrCode = qrCodeMlc?.toUIModel(),
        expireLabelFirst = expireLabel?.expireLabelFirst.toDynamicStringOrNull(),
        expireLabelLast = expireLabel?.expireLabelLast.toDynamicStringOrNull(),
        timer = expireLabel?.timer,
        expireLabelWithoutTimer = expireLabelWithoutTimer,
        btnPlainIconAtm = btnPlainIconAtm?.toUiModel(),
        expirationPaginationMessageMlc = stateAfterExpiration?.paginationMessageMlc?.toUIModel(),
        paginationMessageMlcData = paginationMessageMlc?.toUIModel()
    )
}

@Preview(showBackground = true)
@Composable
fun QrCodeOrg_Preview() {
    val btn = BtnPlainIconAtmData(
        id = "123",
        label = UiText.DynamicString("label"),
        icon = UiIcon.DrawableResource(DiiaResourceIcon.MENU.code),
        interactionState = UIState.Interaction.Enabled
    )
    val data = QrCodeOrgData(
        text = "Text",
        componentId = "qrCodeOrg",
        qrCode = QrCodeMlcData(
            qrLink = UiText.DynamicString("www.google.com")
        ),
        expireLabelFirst = "Код діятиме".toDynamicString(),
        expireLabelLast = "хв".toDynamicString(),
        timer = 10,
        expirationPaginationMessageMlc = generatePaginationMessageMlcMockData(),
        btnPlainIconAtm = btn
    )
    QrCodeOrg(data = data) {}
}

@Preview(showBackground = true)
@Composable
fun QrCodeOrg_NoText_noTimer_Preview() {
    val btn = BtnPlainIconAtmData(
        id = "123",
        label = UiText.DynamicString("label"),
        icon = UiIcon.DrawableResource(DiiaResourceIcon.MENU.code),
        interactionState = UIState.Interaction.Enabled
    )
    val data = QrCodeOrgData(
        text = null,
        componentId = "qrCodeOrg",
        qrCode = QrCodeMlcData(
            qrLink = UiText.DynamicString("www.google.com")
        ),

        expirationPaginationMessageMlc = generatePaginationMessageMlcMockData(),
        btnPlainIconAtm = btn
    )
    QrCodeOrg(data = data) {}
}

@Preview(showBackground = true)
@Composable
fun QrCodeOrg_NoText_Preview() {
    val btn = BtnPlainIconAtmData(
        id = "123",
        label = UiText.DynamicString("label"),
        icon = UiIcon.DrawableResource(DiiaResourceIcon.MENU.code),
        interactionState = UIState.Interaction.Enabled
    )
    val data = QrCodeOrgData(
        text = null,
        componentId = "qrCodeOrg",
        qrCode = QrCodeMlcData(
            qrLink = UiText.DynamicString("www.google.com")
        ),
        expireLabelWithoutTimer = "Код діятиме 12 годин ",
        expirationPaginationMessageMlc = generatePaginationMessageMlcMockData(),
        btnPlainIconAtm = btn
    )
    QrCodeOrg(data = data) {}
}


@Preview(showBackground = true)
@Composable
fun QrCodeOrg_NoTime_noText_Preview() {
    val data = QrCodeOrgData(
        text = null,
        componentId = "qrCodeOrg",
        qrCode = QrCodeMlcData(
            qrLink = UiText.DynamicString("www.google.com")
        )
    )
    QrCodeOrg(data = data) {}
}

@Preview(showBackground = true)
@Composable
fun QrCodeOrg_Error_Preview() {
    val data = QrCodeOrgData(
        text = null,
        componentId = "qrCodeOrg",
        paginationMessageMlcData = generatePaginationMessageMlcMockData(),
        qrCode = QrCodeMlcData(
            qrLink = UiText.DynamicString("www.google.com")
        )
    )
    QrCodeOrg(data = data) {}
}

@Preview(showBackground = true)
@Composable
fun QrCodeOrg_StringTimer() {
    val btn = BtnPlainIconAtmData(
        id = "123",
        label = UiText.DynamicString("label"),
        icon = UiIcon.DrawableResource(DiiaResourceIcon.MENU.code),
        interactionState = UIState.Interaction.Enabled
    )
    val data = QrCodeOrgData(
        text = null,
        componentId = "qrCodeOrg",
        expireLabelWithoutTimer = "Цей код діятиме завжди",
        qrCode = QrCodeMlcData(
            qrLink = UiText.DynamicString("www.google.com")
        ),
        btnPlainIconAtm = btn

    )
    QrCodeOrg(data = data) {}
}