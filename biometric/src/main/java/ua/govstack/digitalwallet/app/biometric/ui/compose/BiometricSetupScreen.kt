package ua.govstack.digitalwallet.app.biometric.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import ua.govstack.digitalwallet.app.ui_base.R
import ua.govstack.digitalwallet.app.ui_base.components.atom.button.BtnPlainAtm
import ua.govstack.digitalwallet.app.ui_base.components.atom.button.BtnPlainAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.button.BtnPrimaryDefaultAtm
import ua.govstack.digitalwallet.app.ui_base.components.atom.button.BtnPrimaryDefaultAtmData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.UIElementData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIAction
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIActionKeysCompose
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.state.UIState
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.TopPaddingMode
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.UiText
import ua.govstack.digitalwallet.app.ui_base.components.molecule.header.NavigationPanelMlcV2Data
import ua.govstack.digitalwallet.app.ui_base.components.molecule.text.SubTitleCentralizedMlc
import ua.govstack.digitalwallet.app.ui_base.components.molecule.text.SubTitleCentralizedMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.text.TitleCentralizedMlc
import ua.govstack.digitalwallet.app.ui_base.components.molecule.text.TitleCentralizedMlcData
import ua.govstack.digitalwallet.app.ui_base.components.organism.header.TopGroupOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.header.TopGroupOrgData
import ua.govstack.digitalwallet.app.ui_base.components.provideTestTagsAsResourceId

@Composable
fun BiometricSetupScreen(
    modifier: Modifier = Modifier,
    data: SnapshotStateList<UIElementData>,
    onUIAction: (UIAction) -> Unit
) {
    BiometricSetupScreenPortrait(modifier, data, onUIAction)
}

@Composable
private fun BiometricSetupScreenPortrait(
    modifier: Modifier = Modifier,
    data: SnapshotStateList<UIElementData>,
    onUIAction: (UIAction) -> Unit
) {
    ConstraintLayout(
        modifier = modifier
            .paint(
                painterResource(id = R.drawable.bg_blue_yellow_gradient),
                contentScale = ContentScale.FillBounds
            )
            .fillMaxSize()
            .safeDrawingPadding()
            .provideTestTagsAsResourceId()
    ) {
        val topGroup = createRef()
        val textGroup = createRef()
        val button = createRef()
        val altButton = createRef()
        val topItem = data.filterIsInstance<TopGroupOrgData>().firstOrNull()
        topItem?.let { item ->
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


        Icon(
            modifier = modifier
                .size(height = 56.dp, width = 131.dp)
                .constrainAs(createRef()) {
                    top.linkTo(topGroup.bottom)
                    bottom.linkTo(button.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .testTag(stringResource(id = ua.govstack.digitalwallet.app.biometric.R.string.biometric_screen_icon_test_tag)),
            painter = painterResource(id = R.drawable.ic_biometric_auth),
            contentDescription = stringResource(id = R.string.accessibility_biometric_methods_icon),
        )

        data.filterIsInstance<BtnPrimaryDefaultAtmData>().firstOrNull()?.let { item ->
            val accessabilityText =
                stringResource(id = R.string.accessibility_biometric_methods_button)
            BtnPrimaryDefaultAtm(
                modifier = modifier
                    .constrainAs(button) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(altButton.top)
                    }
                    .semantics(mergeDescendants = true) {
                        contentDescription = accessabilityText
                    },
                data = item,
                onUIAction = onUIAction
            )
        }

        data.filterIsInstance<BtnPlainAtmData>().firstOrNull()?.let { item ->
            val accessabilityText =
                stringResource(id = R.string.accessibility_biometric_methods_alt_button)
            BtnPlainAtm(
                modifier = modifier
                    .constrainAs(altButton) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom, margin = 16.dp)
                    }
                    .semantics(mergeDescendants = true) {
                        contentDescription = accessabilityText
                    },
                data = item,
                onUIAction = onUIAction
            )
        }
    }
}

@Composable
@Preview
fun BiometricSetupScreenPreview() {
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
        TitleCentralizedMlcData(label = UiText.DynamicString("Enable sign-in with biometrics"))
    )
    _uiData.add(
        SubTitleCentralizedMlcData(label = UiText.DynamicString("Allow Diia to use fingerprint scanning and/or face recognition to sign in to the app."), paddingTop = TopPaddingMode.LARGE)
    )
    _uiData.add(
        BtnPrimaryDefaultAtmData(title = UiText.DynamicString("Allow"))
    )
    _uiData.add(
        BtnPlainAtmData(
            actionKey = UIActionKeysCompose.BUTTON_ALTERNATIVE,
            id = "",
            title = UiText.DynamicString("I'll allow later"),
            interactionState = UIState.Interaction.Enabled
        )
    )
    BiometricSetupScreen(data = uiData, onUIAction = { })
}
