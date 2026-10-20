package ua.govstack.digitalwallet.app.menu.ui

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import ua.govstack.digitalwallet.app.core.models.common.BackStackEvent
import ua.govstack.digitalwallet.app.core.util.navigation.HomeNavigation
import ua.govstack.digitalwallet.app.ui_base.fragments.dialog.system.DiiaSystemDFVM

fun NavGraphBuilder.menuNavGraph(
    systemDFViewModel: DiiaSystemDFVM,
    navigationBackStackEventFlow: Flow<BackStackEvent>,
    homeNavigationActionFlow: MutableSharedFlow<HomeNavigation>,
) {
    composable<ScreenMenuRoute> {
        val viewModel: MenuComposeVM = hiltViewModel()
        MenuC(
            viewModel = viewModel,
            systemDFViewModel = systemDFViewModel,
            navigationBackStackEventFlow = navigationBackStackEventFlow,
            homeNavigationActionFlow = homeNavigationActionFlow
        )
    }
}
