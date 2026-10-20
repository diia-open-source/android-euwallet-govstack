package ua.govstack.digitalwallet.app.home.ui

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ua.govstack.digitalwallet.app.core.models.ConsumableString
import ua.govstack.digitalwallet.app.core.models.common.BackStackEvent
import ua.govstack.digitalwallet.app.core.ui.dynamicdialog.ActionsConst
import ua.govstack.digitalwallet.app.core.util.delegation.Permission
import ua.govstack.digitalwallet.app.core.util.delegation.WithPermission
import ua.govstack.digitalwallet.app.core.util.event.observeUiDataEvent
import ua.govstack.digitalwallet.app.core.util.event.observeUiEvent
import ua.govstack.digitalwallet.app.core.util.extensions.fragment.navigate
import ua.govstack.digitalwallet.app.core.util.extensions.fragment.registerForNavigationResult
import ua.govstack.digitalwallet.app.home.R
import ua.govstack.digitalwallet.app.home.model.HomeMenuItem
import ua.govstack.digitalwallet.app.home.ui.views.HomeBottomBar
import ua.govstack.digitalwallet.app.menu.navigation.MenuHomeNavigation
import ua.govstack.digitalwallet.app.menu.ui.ScreenMenuRoute
import ua.govstack.digitalwallet.app.menu.ui.menuNavGraph
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.state.UIState
import ua.govstack.digitalwallet.app.ui_base.fragments.dialog.system.DiiaSystemDFVM
import ua.govstack.digitalwallet.app.ui_base.util.navigation.openTemplateDialog
import ua.govstack.digitalwallet.app.wallet_gallery.ui.navigation.DocumentsHomeNavigation
import ua.govstack.digitalwallet.app.wallet_gallery.ui.navigation.ScreenDocGalleryRoute
import ua.govstack.digitalwallet.app.wallet_gallery.ui.navigation.docGalleryGraphBuilder
import javax.inject.Inject

@AndroidEntryPoint
class HomeF @Inject constructor(
    private val permission: WithPermission
) : Fragment(),
    WithPermission by permission {

    private var composeView: ComposeView? = null

    private val viewModel: HomeVM by viewModels()
    private val systemDialogVM: DiiaSystemDFVM by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super<Fragment>.onCreate(savedInstanceState)
        lifecycle.addObserver(permission)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.handleDeepLinks()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.showTemplateDialog.observeUiDataEvent(viewLifecycleOwner, ::openTemplateDialog)
        viewModel.allowAuthorizedDeepLinks()
        viewModel.apply {
            processNavigation.observeUiDataEvent(viewLifecycleOwner) { action ->
                navigate(action)
            }
        }
        composeView = ComposeView(requireContext())
        return composeView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission.apply {
                approvePermission(Permission.POST_NOTIFICATIONS)
            }
        }

        doOnCameraPermissionGrantedEvent.observeUiEvent(
            viewLifecycleOwner,
            ::navigateToQrScannerDestination
        )

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.homeNavigationActionFlow.collectLatest {
                    when (it) {
                        is MenuHomeNavigation -> {
                            handleMenuNavigation(it)
                        }

                        is DocumentsHomeNavigation -> {
                            handleDocumentsNavigation(it)
                        }
                    }
                }
            }
        }

        composeView?.setContent {
            val localDensity = LocalDensity.current
            val bottomSystemNavBarHeight = with(LocalDensity.current) {
                WindowInsets.navigationBars.getBottom(localDensity).toDp()
            }
            val selectedMenuItem = viewModel.selectedMenuItem.collectAsState(initial = null)
            val bottomBar = viewModel.bottomBar.collectAsState()
            val navController = rememberNavController()
            val navBackStackEntry = navController.currentBackStackEntryAsState().value
            val displayDynamicBackground by remember {
                derivedStateOf {
                    (selectedMenuItem.value?.peekContent()?.position == HomeActions.HOME_DOCUMENTS)
                            || navBackStackEntry?.destination?.navigatorName == ScreenDocGalleryRoute.javaClass.name
                }
            }
            val height =
                bottomSystemNavBarHeight + 70.dp

            val viewModelStoreOwner: ViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
                Log.d("HomeF", "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner")
            }

            Box(modifier = Modifier.fillMaxSize()) {
                HomeBackground(animated = displayDynamicBackground)
                bottomBar.value?.let { data ->
                    Scaffold(
                        modifier = Modifier.background(Color.Transparent),
                        containerColor = Color.Transparent,
                        contentWindowInsets = WindowInsets(0.dp),
                        bottomBar = {
                            HomeBottomBar(
                                modifier = Modifier.height(height),
                                data = data,
                                onTabClick = { action ->
                                    viewModel.onUIAction(action)
                                })
                        }, content = { padding ->
                            NavHost(
                                modifier = Modifier.padding(padding),
                                navController = navController,
                                enterTransition = {
                                    fadeIn(animationSpec = tween(250))
                                },
                                exitTransition = {
                                    fadeOut(animationSpec = tween(250))
                                },
                                startDestination = ScreenDocGalleryRoute
                            ) {
                                docGalleryGraphBuilder(
                                    navigationBackStackEventFlow = viewModel.navigationBackStackEventFlow,
                                    homeNavigationActionFlow = viewModel.homeNavigationActionFlow,
                                    viewModelStoreOwner = viewModelStoreOwner
                                )
                                menuNavGraph(
                                    systemDFViewModel = systemDialogVM,
                                    navigationBackStackEventFlow = viewModel.navigationBackStackEventFlow,
                                    homeNavigationActionFlow = viewModel.homeNavigationActionFlow
                                )
                            }
                        })

                    //Navigate to selected tab
                    val selectedTabId = data.tabs.find {
                        it.selectionState == UIState.Selection.Selected
                    }?.id
                    if (selectedTabId != null && navController.currentDestination != null) {
                        LaunchedEffect(key1 = selectedTabId) {
                            navigateToTab(navController, selectedTabId)
                        }
                    }
                }
            }
        }

        registerForNavigationResult<ConsumableString>(ActionsConst.ACTION_ITEM_SELECTED) { event ->
            event.consumeEvent { action ->
                when (action) {
                    ActionsConst.ACTION_MENU_DOCUMENTS -> viewModel.setSelectedMenuItem(HomeMenuItem.DOCUMENTS)
                    ActionsConst.ACTION_MENU_MENU -> viewModel.setSelectedMenuItem(HomeMenuItem.MENU)
                }
            }
        }

        registerForNavigationResult<ConsumableString>(
            ActionsConst.FRAGMENT_USER_ACTION_RESULT_KEY,
            viewLifecycleOwner
        ) { event ->
            viewModel.navigationBackStackEventFlow.tryEmit(
                BackStackEvent.UserActionResult(event)
            )
        }

        viewModel.navigationSubscriptionHandler.subscribeForNavigationEvents(
            fragment = this,
            navigationBackStackEventFlow = viewModel.navigationBackStackEventFlow
        )
    }

    private fun handleMenuNavigation(navigation: MenuHomeNavigation) {
        when (navigation) {
            is MenuHomeNavigation.ToCameraRequest -> {
                navigation.consumeEvent {
                    permission.apply {
                        approvePermission(Permission.CAMERA)
                    }
                }
            }

            is MenuHomeNavigation.ToLinkDialog -> {
                navigation.consumeEvent {
                    viewModel.navigateToSettingsSystemDialog(this@HomeF)
                }
            }

            is MenuHomeNavigation.ToLogout -> {
                navigation.consumeEvent {
                    viewModel.navigateToLogout(this@HomeF)
                }
            }

            is MenuHomeNavigation.ToSettings -> {
                navigation.consumeEvent {
                    viewModel.navigateToSettings(this@HomeF)
                }
            }

            is MenuHomeNavigation.ToTransactions -> {
                navigation.consumeEvent {
                    viewModel.navigateToTransaction(this@HomeF)
                }
            }

            is MenuHomeNavigation.ToTemplateDialog -> {
                navigation.consumeEvent {
                    openTemplateDialog(navigation.template)
                }
            }

        }
    }

    private fun handleDocumentsNavigation(navigation: DocumentsHomeNavigation) {
        when (navigation) {
            is DocumentsHomeNavigation.ToCredentialList -> {
                navigation.consumeEvent {
                    viewModel.navigateToAddDocument(this@HomeF)
                }
            }

            is DocumentsHomeNavigation.ToStartNewFlow -> {
                navigation.consumeEvent {
                    viewModel.handleStartFlowDeeplink(navigation.deeplink)
                }
            }

            is DocumentsHomeNavigation.ToDocActions -> {
                navigation.consumeEvent {
                    viewModel.navigateToDocActions(
                        this@HomeF,
                        navigation.doc,
                        navigation.position,
                        navigation.manualDocs
                    )
                }
            }

            is DocumentsHomeNavigation.ToTemplateDialog -> {
                navigation.consumeEvent {
                    openTemplateDialog(navigation.template)
                }
            }

            is DocumentsHomeNavigation.ToDocsOrder -> {
                navigation.consumeEvent {
                    viewModel.navigateToDocsOrder(this@HomeF)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        composeView = null
    }

    private fun navigateToTab(navController: NavHostController, tab: String?) {
        navController.navigate(
            when (tab) {
                HomeActions.HOME_DOCUMENTS.toString() -> ScreenDocGalleryRoute
                HomeActions.HOME_MENU.toString() -> ScreenMenuRoute
                else -> ScreenDocGalleryRoute
            }
        ) {
            navController.graph.startDestinationRoute?.let { route ->
                popUpTo(route) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    private fun navigateToQrScannerDestination() {
        viewModel.navigateToQrScan(fragment = this)
    }

}

@Composable
fun HomeBackground(
    animated: Boolean?
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.gradient_bg))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )
    AnimatedVisibility(
        visible = animated == true,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        LottieAnimation(
            modifier = Modifier.fillMaxSize(),
            alignment = Alignment.Center,
            contentScale = ContentScale.FillBounds,
            composition = composition,
            progress = { progress },

            )
    }
    AnimatedVisibility(
        visible = animated == null || !animated,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(R.drawable.bg_blue_yellow_gradient_with_bottom),
            contentDescription = "",
            contentScale = ContentScale.FillBounds
        )
    }
}
