package ua.govstack.digitalwallet.app.wallet_gallery.ui.navigation

import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import ua.govstack.digitalwallet.app.core.models.common.BackStackEvent
import ua.govstack.digitalwallet.app.core.util.navigation.HomeNavigation
import ua.govstack.digitalwallet.app.wallet_gallery.ui.lobby.DocLobbyC

fun NavGraphBuilder.docGalleryGraphBuilder(
    navigationBackStackEventFlow: Flow<BackStackEvent>,
    homeNavigationActionFlow: MutableSharedFlow<HomeNavigation>,
    viewModelStoreOwner: ViewModelStoreOwner
) {
    composable<ScreenDocGalleryRoute> {
        DocLobbyC(
            navigationBackStackEventFlow = navigationBackStackEventFlow,
            homeNavigationActionFlow = homeNavigationActionFlow,
            viewModelStoreOwner = viewModelStoreOwner
        )
    }
}
