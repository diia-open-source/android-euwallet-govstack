package ua.govstack.digitalwallet.app.pin.ui.create.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import ua.govstack.digitalwallet.app.ui_base.R
import ua.govstack.digitalwallet.app.ui_base.components.DiiaResourceIcon
import ua.govstack.digitalwallet.app.ui_base.components.atom.button.BtnPlainAtm
import ua.govstack.digitalwallet.app.ui_base.components.atom.button.BtnPlainAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.icon.MiddleIconAtmData
import ua.govstack.digitalwallet.app.ui_base.components.conditional
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.DataActionWrapper
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.UIElementData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIAction
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.state.UIState
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.TopPaddingMode
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.UiText
import ua.govstack.digitalwallet.app.ui_base.components.molecule.header.NavigationPanelMlcV2Data
import ua.govstack.digitalwallet.app.ui_base.components.molecule.text.SubTitleCentralizedMlc
import ua.govstack.digitalwallet.app.ui_base.components.molecule.text.SubTitleCentralizedMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.text.TitleCentralizedMlc
import ua.govstack.digitalwallet.app.ui_base.components.molecule.text.TitleCentralizedMlcData
import ua.govstack.digitalwallet.app.ui_base.components.organism.bottom.BottomGroupOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.header.TopGroupOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.header.TopGroupOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.tile.NumButtonTileOrganism
import ua.govstack.digitalwallet.app.ui_base.components.organism.tile.NumButtonTileOrganismData
import ua.govstack.digitalwallet.app.ui_base.components.provideTestTagsAsResourceId
import ua.govstack.digitalwallet.app.ui_base.components.theme.BlackSqueeze
import ua.govstack.digitalwallet.app.ui_base.components.theme.Honeydew
import ua.govstack.digitalwallet.app.ui_base.models.orientation.Orientation

@Composable
fun CreatePinScreen(
    modifier: Modifier = Modifier,
    data: SnapshotStateList<UIElementData>,
    shouldAnnounceAccessibility: Boolean = false,
    useGradientBg: Boolean = true,
    onUIAction: (UIAction) -> Unit
) {
    CreatePinScreenPortrait(
        modifier,
        data,
        shouldAnnounceAccessibility,
        useGradientBg,
        onUIAction
    )
}

@Composable
fun CreatePinScreenPortrait(
    modifier: Modifier,
    data: SnapshotStateList<UIElementData>,
    shouldAnnounceAccessibility: Boolean,
    useGradientBg: Boolean,
    onUIAction: (UIAction) -> Unit
) {
    val view = LocalView.current

    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
            .conditional(!useGradientBg) {
                background(BlackSqueeze)
            }
            .conditional(useGradientBg) {
                paint(
                    painterResource(id = R.drawable.bg_blue_yellow_gradient),
                    contentScale = ContentScale.FillBounds
                )
            }
            .safeDrawingPadding()
            .provideTestTagsAsResourceId()
    ) {
        val topGroup = createRef()
        val textGroup = createRef()
        val numButton = createRef()
        val bottomGroup = createRef()

        val topItem = data.filterIsInstance<TopGroupOrgData>().firstOrNull()
        topItem?.let { item ->
            val titleText = item.navigationPanelMlcV2Data?.title?.asString()
                ?: item.titleGroupMlcData?.heroText?.asString() ?: ""
            if (shouldAnnounceAccessibility) {
                LaunchedEffect(titleText) {
                    view.announceForAccessibility(titleText)
                }
            }
            TopGroupOrg(
                modifier = Modifier.constrainAs(topGroup) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                },
                data = item,
                onUIAction = onUIAction
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(textGroup) {
                    top.linkTo(topGroup.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.matchParent
                }
        ) {
            data.forEach { item ->
                when (item) {
                    is TitleCentralizedMlcData -> TitleCentralizedMlc(data = item)
                    is SubTitleCentralizedMlcData -> SubTitleCentralizedMlc(data = item)
                    else -> Unit
                }
            }
        }
        data.filterIsInstance<NumButtonTileOrganismData>().firstOrNull()?.let { item ->
            NumButtonTileOrganism(
                modifier = Modifier.constrainAs(numButton) {
                    linkTo(
                        top = textGroup.bottom,
                        bottom = parent.bottom,
                        bias = 0.3f
                    )
                },
                data = item,
                orientation = Orientation.Portrait,
                color = if (!useGradientBg) Color.White else Honeydew,
                onUIAction = onUIAction
            )
        }

        data.filterIsInstance<BtnPlainAtmData>().firstOrNull()?.let { item ->
            BtnPlainAtm(
                modifier = Modifier.constrainAs(bottomGroup) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom, margin = 30.dp)
                },
                data = item,
                onUIAction = onUIAction
            )
        }
    }
}

@Composable
@Preview
fun CreatePinScreenPreviewSign() {
    val _uiData = remember { mutableStateListOf<UIElementData>() }
    val uiData: SnapshotStateList<UIElementData> = _uiData
    _uiData.add(
        TopGroupOrgData(
            navigationPanelMlcV2Data = NavigationPanelMlcV2Data(
                title = UiText.DynamicString("Diia.Signature Activation"),
                iconLeft = MiddleIconAtmData(
                    code = DiiaResourceIcon.BACK.code,
                    action = DataActionWrapper(
                        type = "back",
                        subtype = null,
                        resource = null
                    )
                ),
                isClosed = true
            )
        )
    )
    _uiData.add(
        TitleCentralizedMlcData(label = UiText.DynamicString("Create a 6-digit code"))
    )
    _uiData.add(
        SubTitleCentralizedMlcData(
            label = UiText.DynamicString("You will use this code to sign documents and access services"),
            paddingTop = TopPaddingMode.LARGE
        )
    )
    _uiData.add(
        NumButtonTileOrganismData()
    )
    _uiData.add(
        BtnPlainAtmData(
            title = UiText.DynamicString("Cancel"),
            id = "1",
            interactionState = UIState.Interaction.Enabled
        )
    )
    CreatePinScreen(data = uiData, onUIAction = { })
}

@Composable
@Preview
fun CreatePinScreenPreviewLogin() {
    val _uiData = remember { mutableStateListOf<UIElementData>() }
    val uiData: SnapshotStateList<UIElementData> = _uiData
    _uiData.add(
        TopGroupOrgData(
            navigationPanelMlcV2Data = NavigationPanelMlcV2Data(
                isClosed = true
            )
        )
    )
    _uiData.add(
        TitleCentralizedMlcData(label = UiText.DynamicString("Create a 4-digit code"))
    )
    _uiData.add(
        SubTitleCentralizedMlcData(
            label = UiText.DynamicString("You will use this code to sign in to the Diia app."),
            paddingTop = TopPaddingMode.LARGE
        )
    )
    _uiData.add(
        NumButtonTileOrganismData()
    )
    _uiData.add(
        BottomGroupOrgData(
            secondaryButton = BtnPlainAtmData(
                title = UiText.DynamicString("Cancel"),
                id = "1",
                interactionState = UIState.Interaction.Enabled
            )
        )
    )
    CreatePinScreen(data = uiData, onUIAction = { })
}

@Composable
@Preview(name = "phone", device = "spec:width=360dp,height=640dp,dpi=480")
fun CreatePinScreenPreview_small_screen() {
    val _uiData = remember { mutableStateListOf<UIElementData>() }
    val uiData: SnapshotStateList<UIElementData> = _uiData
    _uiData.add(
        TopGroupOrgData(
            navigationPanelMlcV2Data = NavigationPanelMlcV2Data(
                title = UiText.DynamicString("Diia.Signature Activation"),
                iconLeft = MiddleIconAtmData(
                    code = DiiaResourceIcon.BACK.code,
                    action = DataActionWrapper(
                        type = "back",
                        subtype = null,
                        resource = null
                    )
                ),
                isClosed = true
            )
        )
    )
    _uiData.add(
        TitleCentralizedMlcData(label = UiText.DynamicString("Create a 6-digit code"))
    )
    _uiData.add(
        SubTitleCentralizedMlcData(
            label = UiText.DynamicString("You will use this code to sign documents and access services"),
            paddingTop = TopPaddingMode.LARGE
        )
    )
    _uiData.add(
        NumButtonTileOrganismData()
    )
    _uiData.add(
        BottomGroupOrgData(
            secondaryButton = BtnPlainAtmData(
                title = UiText.DynamicString("Cancel"),
                id = "1",
                interactionState = UIState.Interaction.Enabled
            )
        )
    )
    CreatePinScreen(data = uiData, onUIAction = { })
}
