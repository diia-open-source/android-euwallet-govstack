package ua.govstack.digitalwallet.app.pin.ui.input.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import ua.govstack.digitalwallet.app.ui_base.R
import ua.govstack.digitalwallet.app.ui_base.components.conditional
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.UIElementData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIAction
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.UiText
import ua.govstack.digitalwallet.app.ui_base.components.molecule.header.NavigationPanelMlcV2Data
import ua.govstack.digitalwallet.app.ui_base.components.molecule.text.TextLabelMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.text.TitleCentralizedMlc
import ua.govstack.digitalwallet.app.ui_base.components.molecule.text.TitleCentralizedMlcData
import ua.govstack.digitalwallet.app.ui_base.components.organism.header.TopGroupOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.header.TopGroupOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.tile.NumButtonTileOrganism
import ua.govstack.digitalwallet.app.ui_base.components.organism.tile.NumButtonTileOrganismData
import ua.govstack.digitalwallet.app.ui_base.components.provideTestTagsAsResourceId
import ua.govstack.digitalwallet.app.ui_base.components.subatomic.loader.TridentLoaderWithUIBlocking
import ua.govstack.digitalwallet.app.ui_base.components.theme.BlackSqueeze
import ua.govstack.digitalwallet.app.ui_base.components.theme.DiiaTextStyle
import ua.govstack.digitalwallet.app.ui_base.mappers.loader.mapToLoader
import ua.govstack.digitalwallet.app.ui_base.models.orientation.Orientation

@Composable
fun PinInputScreen(
    modifier: Modifier = Modifier,
    data: SnapshotStateList<UIElementData>,
    contentLoaded: Pair<String, Boolean>,
    useGradientBg: Boolean = true,
    onUIAction: (UIAction) -> Unit
) {
    PinInputScreenPortrait(modifier, data, contentLoaded, useGradientBg, onUIAction)
}

@Composable
fun PinInputScreenPortrait(
    modifier: Modifier,
    data: SnapshotStateList<UIElementData>,
    contentLoaded: Pair<String, Boolean>,
    useGradientBg: Boolean,
    onUIAction: (UIAction) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        ConstraintLayout(
            modifier = modifier
                .conditional(!useGradientBg) {
                    background(BlackSqueeze)
                }
                .conditional(useGradientBg) {
                    paint(
                        painterResource(id = R.drawable.bg_blue_yellow_gradient),
                        contentScale = ContentScale.FillBounds
                    )
                }
                .fillMaxSize()
                .safeDrawingPadding()
                .provideTestTagsAsResourceId()
        ) {
            val topGroup = createRef()
            val title = createRef()
            val numButton = createRef()
            val bottomText = createRef()
            data.forEach { item ->
                if (item is TopGroupOrgData) {
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

                if (item is TitleCentralizedMlcData) {
                    TitleCentralizedMlc(
                        modifier = Modifier.constrainAs(title) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            top.linkTo(topGroup.bottom)
                        },
                        data = item
                    )
                }

                if (item is NumButtonTileOrganismData) {
                    NumButtonTileOrganism(
                        modifier = Modifier
                            .constrainAs(numButton) {
                                top.linkTo(parent.top, margin = 16.dp)
                                bottom.linkTo(parent.bottom)
                            },
                        data = item,
                        orientation = Orientation.Portrait,
                        onUIAction = onUIAction
                    )
                }

                if (item is TextLabelMlcData) {
                    ClickableText(
                        modifier = modifier
                            .wrapContentSize()
                            .constrainAs(bottomText) {
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                                bottom.linkTo(parent.bottom, margin = 30.dp)
                            }
                            .semantics {
                                role = Role.Button
                            },
                        text = AnnotatedString(item.text.asString()),
                        style = DiiaTextStyle.t1BigText,
                    ) {
                        onUIAction(UIAction(actionKey = item.actionKey))
                    }
                }
            }
        }
        TridentLoaderWithUIBlocking(loader = mapToLoader(content = contentLoaded))
    }
}

@Composable
@Preview
fun PinInputScreenPreview() {
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
        TitleCentralizedMlcData(label = UiText.DynamicString("Sign-in code"))
    )
    _uiData.add(
        NumButtonTileOrganismData()
    )
    _uiData.add(
        TextLabelMlcData(
            text = UiText.DynamicString("I don't remember my sign-in code")
        )
    )
    PinInputScreen(data = uiData, onUIAction = { }, contentLoaded = "" to true)
}
