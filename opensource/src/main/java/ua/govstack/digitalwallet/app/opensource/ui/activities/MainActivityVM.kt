package ua.govstack.digitalwallet.app.opensource.ui.activities

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ua.govstack.digitalwallet.app.core.di.actions.GlobalActionAllowAuthorizedLinks
import ua.govstack.digitalwallet.app.core.di.actions.GlobalActionLogout
import ua.govstack.digitalwallet.app.core.di.actions.GlobalActionRunDeepLinkAfterPin
import ua.govstack.digitalwallet.app.core.util.DispatcherProvider
import ua.govstack.digitalwallet.app.core.util.delegation.WithDeeplinkHandling
import ua.govstack.digitalwallet.app.core.util.delegation.WithErrorHandlingOnFlow
import ua.govstack.digitalwallet.app.core.util.delegation.WithRetryLastAction
import ua.govstack.digitalwallet.app.core.util.event.UiDataEvent
import ua.govstack.digitalwallet.app.core.util.event.UiEvent
import ua.govstack.digitalwallet.app.core.util.extensions.lifecycle.asLiveData
import ua.govstack.digitalwallet.app.core.util.extensions.lifecycle.consumeEvent
import ua.govstack.digitalwallet.app.core.util.extensions.vm.executeActionOnFlow
import ua.govstack.digitalwallet.app.diia_storage.store.repository.authorization.AuthorizationRepository
import ua.govstack.digitalwallet.app.opensource.R
import ua.govstack.digitalwallet.app.opensource.data.repository.cleaner.DataRepositoryCleaner
import ua.govstack.digitalwallet.app.pin.repository.LoginPinRepository
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.navigation.NavigationPath
import javax.inject.Inject

@HiltViewModel
class MainActivityVM @Inject constructor(
    @param:GlobalActionLogout private val actionLogout: MutableLiveData<UiEvent>,
    private val dispatcherProvider: DispatcherProvider,
    private val authorizationRepository: AuthorizationRepository,
    private val loginPinRepository: LoginPinRepository,
    private val repositoryCleaner: DataRepositoryCleaner,
    @param:GlobalActionAllowAuthorizedLinks val allowAuthorizedLinksFlow: MutableSharedFlow<UiDataEvent<Boolean>>,
    @param:GlobalActionRunDeepLinkAfterPin private val actionRunDeepLinkAfterPin: MutableSharedFlow<UiEvent>,
    private val withDeeplinkHandling: WithDeeplinkHandling,
    private val withErrorHandlingOnFlow: WithErrorHandlingOnFlow,
    private val withRetryLastAction: WithRetryLastAction,
) : ViewModel(),
    WithDeeplinkHandling by withDeeplinkHandling,
    WithErrorHandlingOnFlow by withErrorHandlingOnFlow,
    WithRetryLastAction by withRetryLastAction {

    private val _restartApp = MutableLiveData<UiEvent>()
    val restartApp = _restartApp.asLiveData()

    var allowAuthorizedDeepLinks = false
        private set

    private val _showSplash = MutableStateFlow(true)
    val showSplash = _showSplash.asStateFlow()

    private val _navigation = Channel<Navigation>(Channel.BUFFERED)
    val navigation = _navigation.receiveAsFlow()

    private var tempDeepLinkPath: String? = null

    init {
        startAuthFlow()

        viewModelScope.launch {
            actionLogout.asFlow().collectLatest { event ->
                event.consumeEvent { doLogout() }
            }
        }

        viewModelScope.launch {
            allowAuthorizedLinksFlow.collectLatest { event ->
                if (event.getContentIfNotHandled() == true) {
                    allowAuthorizedDeepLinks()
                }
            }
        }

        viewModelScope.launch {
            actionRunDeepLinkAfterPin.collectLatest {
                tempDeepLinkPath?.let {
                    emitDeeplink(UiDataEvent(buildDeepLinkAction(it)))
                }
            }
        }
    }

    fun startAuthFlow() {
        viewModelScope.launch {
            resolvePrimaryUserNavigation()
        }
    }

    fun hideSplash() {
        _showSplash.value = false
    }

    fun processIntentPath(path: String, fromNewIntent: Boolean) {
        if (fromNewIntent) {
            viewModelScope.launch {
                emitDeeplink(UiDataEvent(buildDeepLinkAction(path)))
            }
        } else {
            tempDeepLinkPath = path
        }
    }

    fun doLogout() {
        viewModelScope.launch(dispatcherProvider.work) {
            authorizationRepository.logoutUser()
            repositoryCleaner.clean()
            _restartApp.postValue(UiEvent())
        }
    }

    fun checkPinCount() {
        viewModelScope.launch {
            if (loginPinRepository.getPinTryCount() >= PIN_TRY_COUNT) {
                loginPinRepository.setPinTryCount(0)
                doLogout()
            }
        }
    }

    private fun allowAuthorizedDeepLinks() {
        viewModelScope.launch {
            allowAuthorizedDeepLinks = true
            val deepLink = deeplinkFlow.value
            if (deepLink?.notHandedYet == true) {
                emitDeeplink(deepLink)
            }
        }
    }

    private fun resolvePrimaryUserNavigation() {
        executeActionOnFlow {
            if (loginPinRepository.isPinPresent()) {
                _navigation.send(Navigation.ToProtection)
            } else {
                _navigation.send(Navigation.ToLogin)
            }
        }
    }

    sealed class Navigation(
        val destinationId: Int,
        val bundle: Bundle?
    ) : NavigationPath {
        data object ToLogin : Navigation(
            destinationId = R.id.nav_login,
            bundle = null
        )

        data object ToProtection : Navigation(
            destinationId = R.id.nav_pin_input,
            bundle = null
        )
    }

    private companion object {
        const val PIN_TRY_COUNT = 3
    }

}
