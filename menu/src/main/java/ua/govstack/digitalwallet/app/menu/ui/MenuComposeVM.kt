package ua.govstack.digitalwallet.app.menu.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import ua.govstack.digitalwallet.app.core.di.actions.GlobalActionLogout
import ua.govstack.digitalwallet.app.core.util.delegation.WithBuildConfig
import ua.govstack.digitalwallet.app.core.util.delegation.WithErrorHandlingOnFlow
import ua.govstack.digitalwallet.app.core.util.delegation.WithRetryLastAction
import ua.govstack.digitalwallet.app.core.util.event.UiDataEvent
import ua.govstack.digitalwallet.app.core.util.event.UiEvent
import ua.govstack.digitalwallet.app.core.util.extensions.vm.executeActionOnFlow
import ua.govstack.digitalwallet.app.menu.MenuContentController
import ua.govstack.digitalwallet.app.menu.R
import ua.govstack.digitalwallet.app.menu.helper.MenuHelper
import ua.govstack.digitalwallet.app.menu.ui.MenuActionsKey.LOGOUT
import ua.govstack.digitalwallet.app.menu.ui.MenuActionsKey.OPEN_QR_CODE_SCAN
import ua.govstack.digitalwallet.app.menu.ui.MenuActionsKey.OPEN_SETTINGS
import ua.govstack.digitalwallet.app.menu.ui.MenuActionsKey.OPEN_TRANSACTION_LOG
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.UIElementData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.addIfNotNull
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIAction
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIActionKeysCompose
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.navigation.NavigationPath
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.UiText
import ua.govstack.digitalwallet.app.ui_base.components.molecule.header.TitleGroupMlcData
import ua.govstack.digitalwallet.app.ui_base.components.organism.header.TopGroupOrgData
import ua.govstack.digitalwallet.app.ui_base.navigation.BaseNavigation
import javax.inject.Inject

@HiltViewModel
class MenuComposeVM @Inject constructor(
    @param:GlobalActionLogout private val actionLogout: MutableLiveData<UiEvent>,
    private val errorHandling: WithErrorHandlingOnFlow,
    private val retryLastAction: WithRetryLastAction,
    private val withBuildConfig: WithBuildConfig,
    private val menuContentController: MenuContentController,
    private val menuHelper: MenuHelper
) : WithErrorHandlingOnFlow by errorHandling, WithRetryLastAction by retryLastAction,
    MenuHelper by menuHelper, ViewModel() {

    private val _topBarData = mutableStateListOf<UIElementData>()
    val topBarData: SnapshotStateList<UIElementData> = _topBarData

    private val _bodyData = mutableStateListOf<UIElementData>()
    val bodyData: SnapshotStateList<UIElementData> = _bodyData

    private val _navigation = MutableSharedFlow<NavigationPath>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val navigation = _navigation.asSharedFlow()

    private val _settingsAction = MutableSharedFlow<UiDataEvent<MenuAction>>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val settingsAction = _settingsAction.asSharedFlow()

    private val _progressIndicatorKey = MutableStateFlow("")
    private val _progressIndicator = MutableStateFlow(false)
    val progressIndicator: Flow<Pair<String, Boolean>> =
        _progressIndicator.combine(_progressIndicatorKey) { value, key ->
            key to value
        }

    private val _contentLoadedKey =
        MutableStateFlow(UIActionKeysCompose.PAGE_LOADING_CIRCULAR)
    private val _contentLoaded = MutableStateFlow(true)
    val contentLoaded: Flow<Pair<String, Boolean>> =
        _contentLoaded.combine(_contentLoadedKey) { value, key ->
            key to value
        }

    init {
        configureTopBar()
        configureBody()
    }

    fun logoutApprove() {
        actionLogout.value = UiEvent()
    }

    private suspend fun selectMenuAction(menuAction: MenuAction) {
        _settingsAction.emit(UiDataEvent(menuAction))
    }

    fun onUIAction(event: UIAction) {
        executeActionOnFlow {
            when (event.actionKey) {
                UIActionKeysCompose.TOOLBAR_NAVIGATION_BACK -> {
                    _navigation.tryEmit(BaseNavigation.Back)
                }

                UIActionKeysCompose.LIST_ITEM_MLC,
                UIActionKeysCompose.LIST_ITEM_GROUP_ORG -> {
                    event.action?.type?.let {
                        when (it) {
                            OPEN_TRANSACTION_LOG -> {
                                _navigation.tryEmit(MenuTabNavigation.NavigateToTransactionLog)
                            }

                            else -> {
                                getMenuActionForString(it)?.let { menuAction ->
                                    selectMenuAction(menuAction)
                                }
                            }
                        }
                    }
                }

                LOGOUT -> {
                    selectMenuAction(MenuAction.Logout)
                }
            }
        }
    }

    fun configureTopBar() {
        if (_topBarData.isEmpty()) {
            val toolbar = TopGroupOrgData(
                titleGroupMlcData = TitleGroupMlcData(
                    heroText = UiText.StringResource(R.string.menu),
                    componentId = UiText.StringResource(R.string.menu_title_menu_test_tag),
                    label = UiText.StringResource(
                        R.string.version_s,
                        withBuildConfig.getVersionName()
                    )
                )
            )
            _topBarData.addIfNotNull(toolbar)
        }
    }

    private fun getMenuActionForString(
        actionKey: String
    ): MenuAction? {
        return when (actionKey) {
            OPEN_SETTINGS -> MenuAction.OpenSettings
            LOGOUT -> MenuAction.Logout
            OPEN_QR_CODE_SCAN -> MenuAction.OpenQrCodeScan
            else -> null
        }
    }

    fun configureBody() {
        _bodyData.addAll(
            menuContentController.configureBody(
            )
        )
    }
}

sealed class MenuTabNavigation : NavigationPath {
    object NavigateToTransactionLog : MenuTabNavigation()
}