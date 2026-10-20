package ua.govstack.digitalwallet.app.login.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ua.govstack.digitalwallet.app.core.util.delegation.WithErrorHandlingOnFlow
import ua.govstack.digitalwallet.app.core.util.delegation.WithRetryLastAction
import ua.govstack.digitalwallet.app.core.util.extensions.vm.executeActionOnFlow
import ua.govstack.digitalwallet.app.pin.repository.LoginPinRepository
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.UIElementData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIAction
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIActionKeysCompose
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.navigation.NavigationPath
import javax.inject.Inject

@HiltViewModel
class LoginVM @Inject constructor(
    private val loginPinRepository: LoginPinRepository,
    private val retryErrorBehavior: WithRetryLastAction,
    private val errorHandlingBehaviour: WithErrorHandlingOnFlow,
) : ViewModel(),
    WithRetryLastAction by retryErrorBehavior,
    WithErrorHandlingOnFlow by errorHandlingBehaviour {

    private val _navigation = Channel<NavigationPath>(Channel.BUFFERED)
    val navigation = _navigation.receiveAsFlow()

    private val _verifyingUser = MutableStateFlow(false)
    private val verifyingUser = _verifyingUser.asStateFlow()

    init {
        viewModelScope.launch {
            _navigation.send(Navigation.ToPinCreation)
        }
    }

    private val _loadingIndicatorKey = MutableStateFlow(UIActionKeysCompose.PAGE_LOADING_CIRCULAR)
    private val _dataLoading = MutableStateFlow(value = false)
    val isLoading = combine(_dataLoading, verifyingUser) { dataLoadingState, verifyingUserState ->
        if (verifyingUserState) {
            _loadingIndicatorKey.emit(UIActionKeysCompose.PAGE_LOADING_CIRCULAR)
        }
        dataLoadingState || verifyingUserState
    }.combine(_loadingIndicatorKey) { value, key ->
        key to value
    }
    private val _screenProgressState = mutableStateOf(value = false)
    val progressState: State<Boolean> = _screenProgressState

    private val _toolbarData = mutableStateListOf<UIElementData>()
    val toolbarData: SnapshotStateList<UIElementData> = _toolbarData

    private val _bodyData = mutableStateListOf<UIElementData>()
    val bodyData: SnapshotStateList<UIElementData> = _bodyData

    private val _bottomData = mutableStateListOf<UIElementData>()
    val bottomData: SnapshotStateList<UIElementData> = _bottomData

    init {
        viewModelScope.launch {
            _navigation.send(Navigation.ToPinCreation)
        }
    }

    fun setPinCode(pin: String) {
        executeActionOnFlow {
            loginPinRepository.setUserAuthorized(pin)
            _navigation.send(Navigation.ToHome)
        }
    }

    fun onUIAction(uiAction: UIAction) {
        //no-op
    }

    sealed class Navigation : NavigationPath {
        object ToPinCreation : Navigation()
        object ToHome : Navigation()
    }
}