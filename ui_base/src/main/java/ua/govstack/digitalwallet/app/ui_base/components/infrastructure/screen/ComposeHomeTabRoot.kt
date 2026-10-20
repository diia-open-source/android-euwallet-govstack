package ua.govstack.digitalwallet.app.ui_base.components.infrastructure.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ua.govstack.digitalwallet.app.core.util.state.Loader
import ua.govstack.digitalwallet.app.core.util.state.getLegacyContentLoaded
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIAction
import ua.govstack.digitalwallet.app.ui_base.components.subatomic.loader.CircularLoaderWithNavigationBlock
import ua.govstack.digitalwallet.app.ui_base.components.subatomic.loader.TridentLoaderBlock
import ua.govstack.digitalwallet.app.ui_base.components.subatomic.loader.TridentLoaderWithNavigationBlock
import ua.govstack.digitalwallet.app.ui_base.components.subatomic.loader.TridentLoaderWithUIBlocking

@Composable
fun ComposeHomeTabRoot(
    modifier: Modifier = Modifier,
    loader: Loader,
    topBar: @Composable (() -> Unit)? = null,
    body: @Composable (ColumnScope.() -> Unit)? = null,
    bottom: @Composable (ColumnScope.() -> Unit)? = null,
    onEvent: (UIAction) -> Unit
) {
    Box(modifier = modifier.fillMaxSize())
    {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            topBar?.let {
                topBar()
            }
            body?.let {
                body()
            }
            bottom?.let {
                bottom()
            }
        }
        TridentLoaderBlock(loader)
        TridentLoaderWithNavigationBlock(
            contentLoaded = loader.getLegacyContentLoaded(),
            onUIAction = onEvent
        )
        CircularLoaderWithNavigationBlock(
            contentLoaded = loader.getLegacyContentLoaded(),
            onUIAction = onEvent
        )
        TridentLoaderWithUIBlocking(
            loader = loader
        )
    }
}
