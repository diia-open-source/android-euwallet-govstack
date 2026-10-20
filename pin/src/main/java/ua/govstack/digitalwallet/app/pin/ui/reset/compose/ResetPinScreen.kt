package ua.govstack.digitalwallet.app.pin.ui.reset.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import ua.govstack.digitalwallet.app.ui_base.R
import ua.govstack.digitalwallet.app.ui_base.components.DiiaResourceIcon
import ua.govstack.digitalwallet.app.ui_base.components.atom.icon.MiddleIconAtmData
import ua.govstack.digitalwallet.app.ui_base.components.conditional
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.DataActionWrapper
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.UIElementData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIAction
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.TopPaddingMode
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.UiText
import ua.govstack.digitalwallet.app.ui_base.components.molecule.header.NavigationPanelMlcV2Data
import ua.govstack.digitalwallet.app.ui_base.components.molecule.text.SubTitleCentralizedMlc
import ua.govstack.digitalwallet.app.ui_base.components.molecule.text.SubTitleCentralizedMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.text.TitleCentralizedMlc
import ua.govstack.digitalwallet.app.ui_base.components.molecule.text.TitleCentralizedMlcData
import ua.govstack.digitalwallet.app.ui_base.components.organism.header.TopGroupOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.header.TopGroupOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.tile.NumButtonTileOrganism
import ua.govstack.digitalwallet.app.ui_base.components.organism.tile.NumButtonTileOrganismData
import ua.govstack.digitalwallet.app.ui_base.components.provideTestTagsAsResourceId
import ua.govstack.digitalwallet.app.ui_base.components.subatomic.loader.TridentLoaderWithUIBlocking
import ua.govstack.digitalwallet.app.ui_base.components.theme.BlackSqueeze
import ua.govstack.digitalwallet.app.ui_base.components.theme.Honeydew
import ua.govstack.digitalwallet.app.ui_base.mappers.loader.mapToLoader
import ua.govstack.digitalwallet.app.ui_base.models.orientation.Orientation

@Composable
fun ResetPinScreen(
    modifier: Modifier = Modifier,
    data: SnapshotStateList<UIElementData>,
    contentLoaded: Pair<String, Boolean>,
    useGradientBg: Boolean = true,
    onUIAction: (UIAction) -> Unit
) {
    ResetPinScreenPortrait(
        modifier,
        data,
        contentLoaded,
        useGradientBg,
        onUIAction
    )
}

@Composable
fun ResetPinScreenPortrait(
    modifier: Modifier,
    data: SnapshotStateList<UIElementData>,
    contentLoaded: Pair<String, Boolean>,
    useGradientBg: Boolean,
    onUIAction: (UIAction) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
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

            val topItem = data.filterIsInstance<TopGroupOrgData>().firstOrNull()
            topItem?.let { item ->
                TopGroupOrg(
                    modifier = modifier
                        .constrainAs(topGroup) {
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
                    modifier = Modifier
                        .constrainAs(numButton) {
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
        }
    }
    TridentLoaderWithUIBlocking(loader = mapToLoader(content = contentLoaded))
}

@Composable
@Preview
fun ResetPinScreenPreview() {
    val _uiData = remember { mutableStateListOf<UIElementData>() }
    val uiData: SnapshotStateList<UIElementData> = _uiData
    _uiData.add(
        TopGroupOrgData(
            navigationPanelMlcV2Data = NavigationPanelMlcV2Data(
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
        SubTitleCentralizedMlcData(label = UiText.DynamicString("To confirm that you are the one changing your sign-in code."), paddingTop = TopPaddingMode.LARGE)
    )
    _uiData.add(
        NumButtonTileOrganismData()
    )
    ResetPinScreen(data = uiData, contentLoaded = "" to true, onUIAction = { })
}

@Composable
@Preview(name = "phone", device = "spec:width=360dp,height=640dp,dpi=480")
fun ResetPinScreenPreview_small_screen() {
    val _uiData = remember { mutableStateListOf<UIElementData>() }
    val uiData: SnapshotStateList<UIElementData> = _uiData
    _uiData.add(
        TopGroupOrgData(
            navigationPanelMlcV2Data = NavigationPanelMlcV2Data(
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
        SubTitleCentralizedMlcData(label = UiText.DynamicString("To confirm that you are the one changing your sign-in code."), paddingTop = TopPaddingMode.LARGE)
    )
    _uiData.add(
        NumButtonTileOrganismData()
    )
    ResetPinScreen(data = uiData, contentLoaded = "" to true, onUIAction = { })
}
