package ua.govstack.digitalwallet.app.menu.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import ua.govstack.digitalwallet.app.core.models.common.BackStackEvent
import ua.govstack.digitalwallet.app.core.ui.dynamicdialog.ActionsConst
import ua.govstack.digitalwallet.app.core.util.extensions.activity.getActivity
import ua.govstack.digitalwallet.app.core.util.extensions.fragment.collapseApp
import ua.govstack.digitalwallet.app.core.util.navigation.HomeNavigation
import ua.govstack.digitalwallet.app.menu.models.EventType
import ua.govstack.digitalwallet.app.menu.navigation.MenuHomeNavigation
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.HomeScreenTab
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.collectAsEffect
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIActionKeysCompose
import ua.govstack.digitalwallet.app.ui_base.fragments.dialog.system.DiiaSystemDFVM
import ua.govstack.digitalwallet.app.ui_base.navigation.BaseNavigation

@Composable
fun MenuC(
    modifier: Modifier = Modifier,
    viewModel: MenuComposeVM,
    systemDFViewModel: DiiaSystemDFVM,
    navigationBackStackEventFlow: Flow<BackStackEvent>,
    homeNavigationActionFlow: MutableSharedFlow<HomeNavigation>,
) {
    val event = remember { mutableStateOf<EventType?>(null) }
    val context = LocalContext.current
    val topBar = viewModel.topBarData
    val body = viewModel.bodyData
    val systemDialogAction = systemDFViewModel.action.observeAsState()
    val navigationBackStackEvent = navigationBackStackEventFlow.collectAsStateWithLifecycle(
        initialValue = BackStackEvent.Empty
    )

    val progressIndicator = viewModel.progressIndicator.collectAsState(
        initial = Pair("", true)
    )
    val contentLoaded = viewModel.contentLoaded.collectAsState(
        initial = Pair(UIActionKeysCompose.PAGE_LOADING_CIRCULAR, true)
    )

    viewModel.navigation.collectAsEffect { navigation ->
        when (navigation) {
            is BaseNavigation.Back -> {
                context.getActivity()?.collapseApp()
            }

            is MenuTabNavigation.NavigateToTransactionLog -> {
                homeNavigationActionFlow.tryEmit(MenuHomeNavigation.ToTransactions())
            }
        }
    }

    viewModel.showTemplateDialog.collectAsEffect { template ->
        homeNavigationActionFlow.tryEmit(MenuHomeNavigation.ToTemplateDialog(template.peekContent()))
    }

    viewModel.settingsAction.collectAsEffect {
        when (it.getContentIfNotHandled()) {
            MenuAction.OpenQrCodeScan -> {
                homeNavigationActionFlow.tryEmit(MenuHomeNavigation.ToCameraRequest())
            }

            MenuAction.OpenSettings -> {
                homeNavigationActionFlow.tryEmit(MenuHomeNavigation.ToSettings())
            }

            MenuAction.Logout -> {
                event.value = EventType.LOGOUT
                homeNavigationActionFlow.tryEmit(MenuHomeNavigation.ToLogout())
            }

            else -> {
                //no-op
            }
        }
    }

    LaunchedEffect(key1 = systemDialogAction.value) {
        when (systemDialogAction.value?.getContentIfNotHandled()) {
            DiiaSystemDFVM.Action.NEGATIVE -> {
                if (event.value == EventType.LOGOUT) {
                    viewModel.logoutApprove()
                }
            }

            DiiaSystemDFVM.Action.POSITIVE -> {
                //no-op
            }

            else -> {}
        }
    }

    handleNavigationResultEvent(navigationBackStackEvent.value, viewModel)

    HomeScreenTab(
        modifier = modifier,
        topBar = topBar,
        body = body,
        progressIndicator = progressIndicator.value,
        contentLoaded = contentLoaded.value,
        onEvent = { viewModel.onUIAction(it) }
    )
}

fun handleNavigationResultEvent(
    event: BackStackEvent,
    viewModel: MenuComposeVM,
) {
    when (event) {
        is BackStackEvent.UserActionResult -> {
            event.data.consumeEvent { action ->
                when (action) {
                    ActionsConst.GENERAL_RETRY -> viewModel.retryLastAction()
                }
            }
        }
    }
}
