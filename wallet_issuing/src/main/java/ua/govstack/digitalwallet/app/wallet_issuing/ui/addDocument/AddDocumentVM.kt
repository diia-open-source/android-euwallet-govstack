package ua.govstack.digitalwallet.app.wallet_issuing.ui.addDocument

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ua.govstack.digitalwallet.app.core.controller.DeeplinkProcessor
import ua.govstack.digitalwallet.app.core.di.actions.GlobalActionNetworkState
import ua.govstack.digitalwallet.app.core.models.deeplink.DeepLinkActionOpenNotify
import ua.govstack.digitalwallet.app.core.network.connectivity.ConnectivityObserver
import ua.govstack.digitalwallet.app.core.ui.dynamicdialog.ActionsConst
import ua.govstack.digitalwallet.app.core.util.deeplink.DeepLinkRoutes
import ua.govstack.digitalwallet.app.core.util.delegation.WithDeeplinkHandling
import ua.govstack.digitalwallet.app.core.util.delegation.WithErrorHandlingOnFlow
import ua.govstack.digitalwallet.app.core.util.delegation.WithRetryLastAction
import ua.govstack.digitalwallet.app.core.util.event.UiDataEvent
import ua.govstack.digitalwallet.app.core.util.extensions.mutableSharedFlowOf
import ua.govstack.digitalwallet.app.core.util.extensions.vm.executeActionOnFlow
import ua.govstack.digitalwallet.app.core.util.state.CircularBody
import ua.govstack.digitalwallet.app.core.util.state.CircularWithBackNav
import ua.govstack.digitalwallet.app.core.util.state.Loader
import ua.govstack.digitalwallet.app.core.util.state.setComponent
import ua.govstack.digitalwallet.app.core.util.state.setFullScreen
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.DataActionWrapper
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.UIElementData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.addAllFromListIfNotNull
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIAction
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIActionKeysCompose
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.navigation.NavigationPath
import ua.govstack.digitalwallet.app.ui_base.navigation.BaseNavigation
import ua.govstack.digitalwallet.app.wallet_core.issuance.model.IssuanceMethod
import ua.govstack.digitalwallet.app.wallet_core.ui.alertDialogsFactory.WalletAlertDialogFactory
import ua.govstack.digitalwallet.app.wallet_issuing.domain.interactor.IssueDocumentsInteractor
import ua.govstack.digitalwallet.app.wallet_issuing.domain.model.result.GetAddDocumentOptionsResult
import ua.govstack.digitalwallet.app.wallet_issuing.domain.model.result.IssueDocumentsResult
import ua.govstack.digitalwallet.app.wallet_issuing.util.IssuingConsts
import ua.govstack.digitalwallet.app.wallet_issuing.util.withLoader
import javax.inject.Inject

@HiltViewModel
internal class AddDocumentVM @Inject constructor(
    @param:GlobalActionNetworkState private val connectivityObserver: ConnectivityObserver,
    private val issueDocumentsInteractor: IssueDocumentsInteractor,
    private val errorHandling: WithErrorHandlingOnFlow,
    private val retryLastAction: WithRetryLastAction,
    private val deepLinkDelegate: WithDeeplinkHandling,
    private val deeplinkProcessor: DeeplinkProcessor,
    private val walletAlertDialogsFactory: WalletAlertDialogFactory,
    private val addDocumentMapper: AddDocumentMapper,
) : ViewModel(),
    WithRetryLastAction by retryLastAction,
    WithErrorHandlingOnFlow by errorHandling,
    WithDeeplinkHandling by deepLinkDelegate
{
    private val _loader = MutableStateFlow(Loader.create())
    val loader: Flow<Loader> = _loader

    private val _toolbarData = mutableStateListOf<UIElementData>()
    val toolbarData: SnapshotStateList<UIElementData> = _toolbarData

    private val _bodyData = mutableStateListOf<UIElementData>()
    val bodyData: SnapshotStateList<UIElementData> = _bodyData

    private val _bottomData = mutableStateListOf<UIElementData>()
    val bottomData: SnapshotStateList<UIElementData> = _bottomData

    private val _navigation = mutableSharedFlowOf<NavigationPath>()
    val navigation = _navigation.asSharedFlow()

    private var _processNavigation = MutableLiveData<UiDataEvent<NavDirections>>()
    val processNavigation: LiveData<UiDataEvent<NavDirections>>
        get() = _processNavigation


    init {
        buildScreenContent()
    }

    private fun buildScreenContent(isNetworkUpdate: Boolean = false) {
        executeActionOnFlow(
            if (isNetworkUpdate){
                _loader.setComponent(IssuingConsts.NO_INTERNET_TRY_AGAIN)
            }else{
                _loader.setFullScreen(CircularWithBackNav)
            }

        ) {
            issueDocumentsInteractor.getAddDocumentOption()
                .collect {
                    clearScreen()
                    showToolbar()
                    showBody(it)
                }
        }
    }

    private fun showToolbar() {
        _toolbarData.add(addDocumentMapper.toolbar())
    }

    private fun showBody(documentsState: GetAddDocumentOptionsResult) {
        _bodyData.addAllFromListIfNotNull(
            addDocumentMapper.body(
                documentsState = documentsState,
                isNetworkAvailable = connectivityObserver.isAvailable
            )
        )
        if (documentsState is GetAddDocumentOptionsResult.Failure && connectivityObserver.isAvailable) {
            showTemplateDialog(walletAlertDialogsFactory.somethingGoesWrong())
        }
    }

    suspend fun handleDeepLinks() {
        deeplinkFlow.collectLatest {
            it?.getContentIfNotHandled()?.let { action ->
                (action as? DeepLinkActionOpenNotify)?.let { startFlow ->
                    if (startFlow.flowType == DeepLinkRoutes.CREDENTIAL_OFFER_RESUME) {
                        resumeOfferWithAuthorization(startFlow.resId)
                    }
                }

                deeplinkProcessor.handleDeepLinkAction(action).let { route ->
                    if (route != null) {
                        _processNavigation.value = UiDataEvent(route)
                    }
                }
            }
        }
    }

    private fun resumeOfferWithAuthorization(urlToHandle: String) {
        viewModelScope.launch {
            issueDocumentsInteractor.resumeOpenId4VciWithAuthorization(uri = urlToHandle)
        }

    }


    fun clearScreen() {
        _toolbarData.clear()
        _bodyData.clear()
    }

    fun onUIAction(event: UIAction) {
        when (event.actionKey) {
            UIActionKeysCompose.TOOLBAR_NAVIGATION_BACK -> {
                _navigation.tryEmit(BaseNavigation.Back)
            }

            IssuingConsts.ADD_DOCUMENT_QR_ACTION -> {
                _navigation.tryEmit(Navigation.ToQR)
            }

        }
        when (event.action?.type) {
            IssuingConsts.ADD_DOCUMENT_ISSUANCE_ACTION -> {
                issueDocuments(event.action)
            }

            ActionsConst.ACTION_NAVIGATE_BACK -> {
                _navigation.tryEmit(BaseNavigation.Back)
            }

            IssuingConsts.NO_INTERNET_TRY_AGAIN -> {
                buildScreenContent(isNetworkUpdate = true)
            }
        }
    }

    private fun issueDocuments(action: DataActionWrapper?) {
        executeActionOnFlow {
            issueDocumentsInteractor.issueDocuments(
                issuanceMethod = IssuanceMethod.OPENID4VCI,
                configIds = listOf(action?.resource.toString()),
                issuerId = action?.subresource.toString()
            )
                .withLoader(_loader.setFullScreen(CircularBody))
                .collect { state ->
                    when (state) {
                        is IssueDocumentsResult.Success -> {
                            showTemplateDialog(walletAlertDialogsFactory.docIssued())
                        }

                        is IssueDocumentsResult.Failure -> {
                            showTemplateDialog(walletAlertDialogsFactory.docFailToReceive())
                        }

                        is IssueDocumentsResult.DeferredSuccess -> {
                            showTemplateDialog(walletAlertDialogsFactory.deferredDocRequest())
                        }
                    }
                }
        }
    }

    sealed class Navigation : NavigationPath {
        object ToQR : Navigation()
    }

}
