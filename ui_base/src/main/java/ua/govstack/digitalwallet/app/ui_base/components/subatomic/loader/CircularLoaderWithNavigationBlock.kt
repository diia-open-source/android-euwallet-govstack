package ua.govstack.digitalwallet.app.ui_base.components.subatomic.loader

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIAction
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIActionKeysCompose
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.UiText
import ua.govstack.digitalwallet.app.ui_base.components.molecule.header.NavigationPanelMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.loading.CircularLoaderMolecule
import ua.govstack.digitalwallet.app.ui_base.components.organism.header.TopGroupOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.header.TopGroupOrgData

@Composable
fun CircularLoaderWithNavigationBlock(
    modifier: Modifier = Modifier,
    contentLoaded: Pair<String, Boolean>,
    onUIAction: (UIAction) -> Unit
) {
    if (!contentLoaded.second && (contentLoaded.first == UIActionKeysCompose.PAGE_LOADING_CIRCULAR_WITH_BACK_NAVIGATION)) {
        Box(modifier = modifier.fillMaxWidth()) {
            TopGroupOrg(
                modifier = Modifier.align(Alignment.TopCenter),
                data = TopGroupOrgData(
                    navigationPanelMlcData = NavigationPanelMlcData(
                        title = UiText.DynamicString(""),
                        isContextMenuExist = false
                    )
                ),
                onUIAction = onUIAction
            )
            CircularLoaderMolecule(modifier = Modifier.align(Alignment.Center))
        }
    }
}
