package ua.govstack.digitalwallet.app.ui_base.components.organism.qr

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastCbrt
import ua.govstack.digitalwallet.app.core.models.common_compose.org.qr.LinkQrShareOrg
import ua.govstack.digitalwallet.app.core.util.state.Loader
import ua.govstack.digitalwallet.app.ui_base.components.DiiaResourceIcon
import ua.govstack.digitalwallet.app.ui_base.components.atom.button.BtnPlainIconAtmData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.UIElementData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIAction
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIActionKeysCompose
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.state.UIState
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.SidePaddingMode
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.TopPaddingMode
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.UiIcon
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.UiText
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.toDynamicString
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.toDp
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.toSidePaddingMode
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.toTopPaddingMode
import ua.govstack.digitalwallet.app.ui_base.components.molecule.code.QrCodeMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.message.generatePaginationMessageMlcMockData
import ua.govstack.digitalwallet.app.ui_base.components.organism.chip.CenterChipBlackTabsOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.chip.CenterChipBlackTabsOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.chip.generateCenterChipBlackTab_Two_OrgMockData
import ua.govstack.digitalwallet.app.ui_base.components.organism.chip.toUiModel
import ua.govstack.digitalwallet.app.ui_base.components.organism.sharing.LinkSharingOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.sharing.LinkSharingOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.sharing.LinkSharingOrgMocTypes
import ua.govstack.digitalwallet.app.ui_base.components.organism.sharing.generateLinkSharingOrgMockData
import ua.govstack.digitalwallet.app.ui_base.components.organism.sharing.toUIModel
import ua.govstack.digitalwallet.app.ui_base.components.subatomic.loader.TridentLoaderAtm
import ua.govstack.digitalwallet.app.ui_base.components.theme.White

@Composable
fun LinkQrShareOrg(
    modifier: Modifier = Modifier,
    data: LinkQrShareOrgData,
    loader: Loader = Loader.create(),
    onUIAction: (UIAction) -> Unit
) {
    val isLoading = loader.isLoadingByComponent(UIActionKeysCompose.LINK_QR_SHARE_ORG)
    val orgHeight = 476.dp
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = data.paddingHorizontal.toDp(defaultPadding = 16.dp),
                top = data.paddingTop.toDp(defaultPadding = 8.dp),
                end = data.paddingHorizontal.toDp(defaultPadding = 16.dp)
            )
            .testTag(data.componentId.asString())
            .height(orgHeight)
    ) {
        Column(
            modifier = Modifier
                .background(
                    color = White,
                    shape = RoundedCornerShape(24.dp)
                )
                .fillMaxHeight()
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 24.dp)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(orgHeight)
                ) {
                    TridentLoaderAtm(
                        modifier = Modifier
                            .align(Alignment.Center)
                    )
                }
            } else {
                data.centerChipBlackTabsOrg?.let {
                    // CenterChipBlack   TabsOrgData.items are required to have size = 2 here
                    CenterChipBlackTabsOrg(data = it, onUIAction = onUIAction)
                }
                //If preselected code is equal to the code of the first item then show LinkSharingOrg
                if (data.selectedCode == data.centerChipBlackTabsOrg?.items?.firstOrNull()?.code && data.linkSharingOrg != null) {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                    )
                    LinkSharingOrg(
                        data = data.linkSharingOrg,
                        onUIAction = onUIAction
                    )
                } else if (data.selectedCode == data.centerChipBlackTabsOrg?.items?.getOrNull(1)?.code && data.qrCodeOrg != null) {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                    )
                    QrCodeOrg(data = data.qrCodeOrg, onUIAction = onUIAction)
                }
            }
        }
    }
}


data class LinkQrShareOrgData(
    val actionKey: String = UIActionKeysCompose.LINK_QR_SHARE_ORG,
    val componentId: UiText = UiText.DynamicString("linkQrShareOrg"),
    val paddingTop: TopPaddingMode? = null,
    val paddingHorizontal: SidePaddingMode? = null,
    val selectedCode: String? = null,
    val centerChipBlackTabsOrg: CenterChipBlackTabsOrgData? = null,
    val linkSharingOrg: LinkSharingOrgData? = null,
    val qrCodeOrg: QrCodeOrgData? = null
) : UIElementData

fun LinkQrShareOrg.toUiModel(): LinkQrShareOrgData {
    return LinkQrShareOrgData(
        componentId = UiText.DynamicString(componentId),
        paddingTop = paddingMode?.top.toTopPaddingMode(),
        paddingHorizontal = paddingMode?.side.toSidePaddingMode(),
        selectedCode = centerChipBlackTabsOrg?.preselectedCode,
        centerChipBlackTabsOrg = centerChipBlackTabsOrg?.toUiModel(),
        linkSharingOrg = linkSharingOrg?.toUIModel(),
        qrCodeOrg = qrCodeOrg?.toUIModel()
    )
}


@Composable
@Preview
fun LinkQrShareOrg_LinkSharingOrg_Preview() {
    LinkQrShareOrg(
        data = LinkQrShareOrgData(
            centerChipBlackTabsOrg = generateCenterChipBlackTab_Two_OrgMockData(preselectedCode = "1"),
            linkSharingOrg = generateLinkSharingOrgMockData(LinkSharingOrgMocTypes.WITH_TEXT),
            selectedCode = "1",
            qrCodeOrg = QrCodeOrgData(
                text = "Text",
                componentId = "qrCodeOrg",
                qrCode = QrCodeMlcData(
                    qrLink = UiText.DynamicString("www.google.com")
                ),
                expireLabelFirst = "Код діятиме".toDynamicString(),
                expireLabelLast = "хв".toDynamicString(),
                timer = 10,
                expirationPaginationMessageMlc = generatePaginationMessageMlcMockData()
            )
        )
    ) {}
}

@Composable
@Preview
fun LinkQrShareOrg_QrCodeOrg_Preview() {
    LinkQrShareOrg(
        data = LinkQrShareOrgData(
            paddingTop = TopPaddingMode.MEDIUM,
            paddingHorizontal = SidePaddingMode.MEDIUM,
            centerChipBlackTabsOrg = generateCenterChipBlackTab_Two_OrgMockData(preselectedCode = "2"),
            linkSharingOrg = generateLinkSharingOrgMockData(LinkSharingOrgMocTypes.WITH_TEXT),
            selectedCode = "2",
            qrCodeOrg = QrCodeOrgData(
                text = "Text",
                componentId = "qrCodeOrg",
                qrCode = QrCodeMlcData(
                    qrLink = UiText.DynamicString("www.google.com")
                ),
                expireLabelFirst = "Код діятиме".toDynamicString(),
                expireLabelLast = "хв".toDynamicString(),
                timer = 10,
                expirationPaginationMessageMlc = generatePaginationMessageMlcMockData()
            )
        )
    ) {}
}

@Composable
@Preview
fun LinkQrShareOrg_PaginationMessage_Preview() {
    LinkQrShareOrg(
        data = LinkQrShareOrgData(
            paddingTop = TopPaddingMode.MEDIUM,
            paddingHorizontal = SidePaddingMode.MEDIUM,
            centerChipBlackTabsOrg = generateCenterChipBlackTab_Two_OrgMockData(preselectedCode = "7"),
            linkSharingOrg = generateLinkSharingOrgMockData(LinkSharingOrgMocTypes.WITH_TEXT),
            selectedCode = "2",
            qrCodeOrg = QrCodeOrgData(
                text = "Text \n heeee",
                componentId = "qrCodeOrg",
                qrCode = QrCodeMlcData(
                    qrLink = UiText.DynamicString("www.google.com")
                ),
                expireLabelFirst = "Код діятиме".toDynamicString(),
                expireLabelLast = "хв".toDynamicString(),
                timer = 10,
                expirationPaginationMessageMlc = generatePaginationMessageMlcMockData()
            )
        )
    ) {}
}

@Composable
@Preview
fun LinkQrShareOrg_Loading_Preview() {
    LinkQrShareOrg(
        loader = Loader.createComponent(UIActionKeysCompose.LINK_QR_SHARE_ORG, true),
        data = LinkQrShareOrgData(
            paddingTop = TopPaddingMode.MEDIUM,
            paddingHorizontal = SidePaddingMode.MEDIUM,
            selectedCode = "2",
            centerChipBlackTabsOrg = generateCenterChipBlackTab_Two_OrgMockData(preselectedCode = "7"),
            linkSharingOrg = generateLinkSharingOrgMockData(LinkSharingOrgMocTypes.WITH_TEXT),
            qrCodeOrg = QrCodeOrgData(
                text = "Text",
                componentId = "qrCodeOrg",
                qrCode = QrCodeMlcData(
                    qrLink = UiText.DynamicString("www.google.com")
                ),
                expireLabelFirst = "Код діятиме".toDynamicString(),
                expireLabelLast = "хв".toDynamicString(),
                timer = 10,
                expirationPaginationMessageMlc = generatePaginationMessageMlcMockData()
            )
        )
    ) {}
}


@Composable
@Preview
fun LinkQrShareOrg_Loading_Preview_2() {
    LinkQrShareOrg(
        loader = Loader.createComponent(UIActionKeysCompose.LINK_QR_SHARE_ORG, false),
        data = LinkQrShareOrgData(
            paddingTop = TopPaddingMode.MEDIUM,
            paddingHorizontal = SidePaddingMode.MEDIUM,
            selectedCode = "2",
            centerChipBlackTabsOrg = generateCenterChipBlackTab_Two_OrgMockData(),
            linkSharingOrg = generateLinkSharingOrgMockData(LinkSharingOrgMocTypes.WITH_TEXT),
            qrCodeOrg = QrCodeOrgData(
                text = "Text",
                componentId = "qrCodeOrg",
                qrCode = QrCodeMlcData(
                    qrLink = UiText.DynamicString("www.google.com")
                ),
                expireLabelWithoutTimer = "10 days",
                expirationPaginationMessageMlc = generatePaginationMessageMlcMockData(),
                btnPlainIconAtm = BtnPlainIconAtmData(
                    id = "123",
                    label = UiText.DynamicString("label"),
                    icon = UiIcon.DrawableResource(DiiaResourceIcon.MENU.code),
                    interactionState = UIState.Interaction.Enabled
                )
            )
        )
    ) {}
}

@Composable
@Preview(heightDp = 500)
fun LinkQrShareOrg_LinkOrgMessage() {
    LinkQrShareOrg(
        loader = Loader.createComponent(UIActionKeysCompose.LINK_QR_SHARE_ORG, false),
        data = LinkQrShareOrgData(
            paddingTop = TopPaddingMode.MEDIUM,
            paddingHorizontal = SidePaddingMode.MEDIUM,
            centerChipBlackTabsOrg = generateCenterChipBlackTab_Two_OrgMockData("1"),
            linkSharingOrg = generateLinkSharingOrgMockData(LinkSharingOrgMocTypes.ERROR),
        )
    ) {}
}

@Composable
@Preview(heightDp = 500)
fun LinkQrShareOrg_QROrgMessage() {
    LinkQrShareOrg(
        loader = Loader.createComponent(UIActionKeysCompose.LINK_QR_SHARE_ORG, false),
        data = LinkQrShareOrgData(
            paddingTop = TopPaddingMode.MEDIUM,
            paddingHorizontal = SidePaddingMode.MEDIUM,
            centerChipBlackTabsOrg = generateCenterChipBlackTab_Two_OrgMockData(preselectedCode = "2"),
            qrCodeOrg = QrCodeOrgData(
                text = "Text",
                componentId = "qrCodeOrg",
                paginationMessageMlcData = generatePaginationMessageMlcMockData(),
                qrCode = QrCodeMlcData(
                    qrLink = UiText.DynamicString("www.google.com")
                )
            ),
        )
    ) {}
}