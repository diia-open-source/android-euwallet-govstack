package ua.govstack.digitalwallet.app.login.ui.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ua.govstack.digitalwallet.app.login.R
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.UIElementData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIAction
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIActionKeysCompose
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.UiText
import ua.govstack.digitalwallet.app.ui_base.components.molecule.header.TitleGroupMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.loading.FullScreenLoadingMolecule
import ua.govstack.digitalwallet.app.ui_base.components.molecule.loading.TridentLoaderMolecule
import ua.govstack.digitalwallet.app.ui_base.components.organism.header.TopGroupOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.header.TopGroupOrgData
import ua.govstack.digitalwallet.app.ui_base.components.provideTestTagsAsResourceId
import ua.govstack.digitalwallet.app.ui_base.components.theme.BlackAlpha30

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    toolbar: SnapshotStateList<UIElementData>,
    body: SnapshotStateList<UIElementData>,
    bottom: SnapshotStateList<UIElementData>,
    screenNavigationProcessing: State<Boolean>,
    isLoading: Pair<String, Boolean>,
    onEvent: (UIAction) -> Unit
) {
    Box(modifier = Modifier
        .fillMaxSize()
        .provideTestTagsAsResourceId()) {
        Column(
            modifier = modifier
                .paint(
                    painterResource(id = R.drawable.bg_blue_yellow_gradient),
                    contentScale = ContentScale.FillBounds
                )
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            if (screenNavigationProcessing.value) {
                FullScreenLoadingMolecule(
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {}
                        )
                        .fillMaxSize()
                        .testTag(stringResource(R.string.login_screen_loading_test_tag)),
                    backgroundColor = BlackAlpha30
                )
            }
            Column {
                toolbar.forEach { item ->
                    when (item) {
                        is TopGroupOrgData -> {
                            TopGroupOrg(
                                data = item,
                                onUIAction = onEvent
                            )
                        }
                    }
                }
            }

        }
        if (isLoading.first == UIActionKeysCompose.PAGE_LOADING_TRIDENT && isLoading.second) {
             TridentLoaderMolecule()
        }
        FullScreenLoadingMolecule()
    }
}

@Preview
@Composable
fun LoginScreenPreview(
) {
    val _toolbarData = remember { mutableStateListOf<UIElementData>() }
    val _bodyData = remember { mutableStateListOf<UIElementData>() }
    val _bottomData = remember { mutableStateListOf<UIElementData>() }

    _toolbarData.add(
        TopGroupOrgData(
            titleGroupMlcData = TitleGroupMlcData(
                heroText = UiText.DynamicString("Sign in"),
                label = UiText.DynamicString("App version: 3.0.78.1722")
            )
        )
    )

    val navState = remember { mutableStateOf(value = false) }
    LoginScreen(
        toolbar = _toolbarData,
        body = _bodyData,
        bottom = _bottomData,
        screenNavigationProcessing = navState,
        isLoading = "" to false,
    ) {}
}