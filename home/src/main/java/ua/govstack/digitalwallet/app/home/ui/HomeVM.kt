package ua.govstack.digitalwallet.app.home.ui

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ua.govstack.digitalwallet.app.core.controller.DeeplinkProcessor
import ua.govstack.digitalwallet.app.core.di.actions.GlobalActionAllowAuthorizedLinks
import ua.govstack.digitalwallet.app.core.di.actions.GlobalActionSelectedMenuItem
import ua.govstack.digitalwallet.app.core.models.common.BackStackEvent
import ua.govstack.digitalwallet.app.core.models.deeplink.DeepLinkActionOpenMenu
import ua.govstack.digitalwallet.app.core.models.deeplink.DeepLinkActionStartFlow
import ua.govstack.digitalwallet.app.core.util.delegation.WithDeeplinkHandling
import ua.govstack.digitalwallet.app.core.util.delegation.WithErrorHandling
import ua.govstack.digitalwallet.app.core.util.delegation.WithRetryLastAction
import ua.govstack.digitalwallet.app.core.util.event.UiDataEvent
import ua.govstack.digitalwallet.app.core.util.extensions.mutableSharedFlowOf
import ua.govstack.digitalwallet.app.core.util.navigation.HomeNavigation
import ua.govstack.digitalwallet.app.home.R
import ua.govstack.digitalwallet.app.home.model.HomeMenuItem
import ua.govstack.digitalwallet.app.menu.helper.MenuHelper
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIAction
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIActionKeysCompose
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.state.UIState
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.UiText
import ua.govstack.digitalwallet.app.ui_base.components.molecule.tab.TabItemMoleculeData
import ua.govstack.digitalwallet.app.ui_base.components.organism.bottom.TabBarOrganismData
import ua.govstack.digitalwallet.app.ui_base.models.homescreen.HomeMenuItemConstructor
import ua.govstack.digitalwallet.app.wallet_core.documents.helper.DocumentsHelper
import ua.govstack.digitalwallet.app.wallet_gallery.ui.navigation.DocGalleryNavigationHelper
import javax.inject.Inject

@HiltViewModel
class HomeVM @Inject constructor(
    @param:GlobalActionAllowAuthorizedLinks val allowAuthorizedLinksFlow: MutableSharedFlow<UiDataEvent<Boolean>>,
    @param:GlobalActionSelectedMenuItem val globalActionSelectedMenuItem: MutableStateFlow<UiDataEvent<HomeMenuItemConstructor>?>,
    private val withRetryLastAction: WithRetryLastAction,
    private val errorHandlingDelegate: WithErrorHandling,
    private val deepLinkDelegate: WithDeeplinkHandling,
    private val composeMapper: HomeScreenComposeMapper,
    private val deeplinkProcessor: DeeplinkProcessor,
    private val menuHelper: MenuHelper,
    private val documentsHelper: DocumentsHelper,
    val navigationSubscriptionHandler: DocGalleryNavigationHelper,
) : ViewModel(),
    WithErrorHandling by errorHandlingDelegate,
    WithRetryLastAction by withRetryLastAction,
    WithDeeplinkHandling by deepLinkDelegate,
    HomeScreenComposeMapper by composeMapper,
    MenuHelper by menuHelper,
    DocumentsHelper by documentsHelper {

    private val _bottomBar = MutableStateFlow<TabBarOrganismData?>(null)
    val bottomBar: StateFlow<TabBarOrganismData?> = _bottomBar.asStateFlow()

    private var _processNavigation = MutableLiveData<UiDataEvent<NavDirections>>()
    val processNavigation: LiveData<UiDataEvent<NavDirections>>
        get() = _processNavigation

    private val _hasUnreadNotifications = MediatorLiveData<Boolean?>(null)
    val hasUnreadNotifications: LiveData<Boolean?>
        get() = _hasUnreadNotifications

    val selectedMenuItem = globalActionSelectedMenuItem.asSharedFlow()

    /**
     * Flow that emits back stack navigation actions from HomeF to child tabs.
     * HomeF register all events in appropriate callback methods (for instance registerForNavigationResult)
     * and emits them into this flow
     */
    val navigationBackStackEventFlow = mutableSharedFlowOf<BackStackEvent>()

    /**
     * Flow that collects navigation actions that were emitted from child tabs (Doc, PC, Menu)
     */
    val homeNavigationActionFlow = mutableSharedFlowOf<HomeNavigation>()

    init {
        configureBottomTabBar()
    }

    override fun navigateToQrScan(fragment: Fragment) {
        documentsHelper.navigateToQrScan(fragment)
    }

    private fun configureBottomTabBar() {
        val tabDocuments = TabItemMoleculeData(
            label = "Documents",
            iconSelected = UiText.StringResource(R.drawable.ic_tab_documents_selected),
            iconUnselected = UiText.StringResource(R.drawable.ic_tab_documents_unselected),
            actionKey = HomeActions.HOME_DOCUMENTS.toString(),
            id = HomeActions.HOME_DOCUMENTS.toString(),
            componentId = UiText.StringResource(R.string.home_tab_documents_test_tag),
            selectionState = UIState.Selection.Selected
        )
        val tabMenu = TabItemMoleculeData(
            label = "Menu",
            iconSelected = UiText.StringResource(R.drawable.ic_tab_menu_selected),
            iconUnselected = UiText.StringResource(R.drawable.ic_tab_menu_unselected),
            iconSelectedWithBadge = UiText.StringResource(R.drawable.ic_tab_menu_selected_badge),
            iconUnselectedWithBadge = UiText.StringResource(R.drawable.ic_tab_menu_unselected_badge),
            actionKey = HomeActions.HOME_MENU.toString(),
            id = HomeActions.HOME_MENU.toString(),
            showBadge = hasUnreadNotifications.value ?: false,
            componentId = UiText.StringResource(R.string.home_tab_menu_test_tag)
        )

        _bottomBar.tryEmit(
            TabBarOrganismData(
                componentId = UiText.StringResource(R.string.home_tab_bar_test_tag),
                tabs = SnapshotStateList<TabItemMoleculeData>().apply {
                    add(tabDocuments)
                    add(tabMenu)
                }
            )
        )
    }

    fun onUIAction(event: UIAction) {
        when (event.actionKey) {
            UIActionKeysCompose.MENU_ITEM_CLICK -> {
                when (event.data) {
                    HomeActions.HOME_MENU.toString() -> setSelectedMenuItem(HomeMenuItem.MENU)
                    HomeActions.HOME_DOCUMENTS.toString() -> setSelectedMenuItem(HomeMenuItem.DOCUMENTS)
                }
            }
        }

    }

    fun setSelectedMenuItem(menuItem: HomeMenuItemConstructor) {
        globalActionSelectedMenuItem.tryEmit(UiDataEvent(menuItem))
        _bottomBar.tryEmit(_bottomBar.value?.onTabClicked(menuItem.position.toString()))
    }

    fun allowAuthorizedDeepLinks() {
        viewModelScope.launch {
            allowAuthorizedLinksFlow.emit(UiDataEvent(true))
        }
    }

    suspend fun handleDeepLinks() {
        deeplinkFlow.collectLatest {
            it?.getContentIfNotHandled()?.let { action ->
                deeplinkProcessor.handleDeepLinkAction(action).let { route ->
                    if (route == null) {
                        if (action is DeepLinkActionOpenMenu) {
                            setSelectedMenuItem(HomeMenuItem.MENU)
                        } else {
                            setSelectedMenuItem(HomeMenuItem.DOCUMENTS)
                        }
                    } else {
                        _processNavigation.value = UiDataEvent(route)
                    }
                }
            }
        }
    }

    fun handleStartFlowDeeplink(deeplink: DeepLinkActionStartFlow) {
        viewModelScope.launch {
            deeplinkProcessor.handleDeepLinkAction(deeplink)?.let { route ->
                _processNavigation.value = UiDataEvent(route)
            }
        }
    }
}
