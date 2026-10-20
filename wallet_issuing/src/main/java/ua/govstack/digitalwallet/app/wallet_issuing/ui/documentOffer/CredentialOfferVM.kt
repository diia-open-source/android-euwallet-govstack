package ua.govstack.digitalwallet.app.wallet_issuing.ui.documentOffer

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
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
import ua.govstack.digitalwallet.app.core.models.deeplink.DeepLinkActionOpenNotify
import ua.govstack.digitalwallet.app.core.ui.dynamicdialog.ActionsConst
import ua.govstack.digitalwallet.app.core.util.deeplink.DeepLinkRoutes
import ua.govstack.digitalwallet.app.core.util.delegation.WithDeeplinkHandling
import ua.govstack.digitalwallet.app.core.util.delegation.WithErrorHandlingOnFlow
import ua.govstack.digitalwallet.app.core.util.delegation.WithRetryLastAction
import ua.govstack.digitalwallet.app.core.util.event.UiDataEvent
import ua.govstack.digitalwallet.app.core.util.extensions.mutableSharedFlowOf
import ua.govstack.digitalwallet.app.core.util.extensions.vm.executeActionOnFlow
import ua.govstack.digitalwallet.app.core.util.state.Loader
import ua.govstack.digitalwallet.app.core.util.state.TridentWithBackNav
import ua.govstack.digitalwallet.app.core.util.state.setComponent
import ua.govstack.digitalwallet.app.core.util.state.setFullScreen
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.UIElementData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.addAllFromListIfNotNull
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIAction
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIActionKeysCompose
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.navigation.NavigationPath
import ua.govstack.digitalwallet.app.ui_base.navigation.BaseNavigation
import ua.govstack.digitalwallet.app.wallet_core.documents.helper.DocumentsHelper
import ua.govstack.digitalwallet.app.wallet_core.ui.alertDialogsFactory.WalletAlertDialogFactory
import ua.govstack.digitalwallet.app.wallet_core.ui.conts.WalletConst
import ua.govstack.digitalwallet.app.wallet_issuing.domain.interactor.DocumentOfferInteractor
import ua.govstack.digitalwallet.app.wallet_issuing.domain.model.DocumentOffer
import ua.govstack.digitalwallet.app.wallet_issuing.domain.model.result.IssueDocumentOfferResult
import ua.govstack.digitalwallet.app.wallet_issuing.domain.model.result.ResolveDocumentOfferResult
import ua.govstack.digitalwallet.app.wallet_issuing.util.IssuingConsts
import ua.govstack.digitalwallet.app.wallet_issuing.util.withLoader
import javax.inject.Inject

@HiltViewModel
internal class DocumentOfferVM @Inject constructor(
    private val documentOfferInteractor: DocumentOfferInteractor,
    private val errorHandling: WithErrorHandlingOnFlow,
    private val retryLastAction: WithRetryLastAction,
    private val deepLinkDelegate: WithDeeplinkHandling,
    private val deeplinkProcessor: DeeplinkProcessor,
    private val ewalletAlertDialogsFactory: WalletAlertDialogFactory,
    private val documentOfferMapper: DocumentOfferMapper,
    private val documentsHelper: DocumentsHelper,
    savedStateHandle: SavedStateHandle
) : ViewModel(),
    WithRetryLastAction by retryLastAction,
    WithDeeplinkHandling by deepLinkDelegate,
    WithErrorHandlingOnFlow by errorHandling,
    DocumentsHelper by documentsHelper {

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

    val linkToProcess: String? = savedStateHandle[WalletConst.LINK_TO_PROCESS]

    init {
        processLink()
    }

    fun onUIAction(event: UIAction) {
        when (event.actionKey) {
            UIActionKeysCompose.TOOLBAR_NAVIGATION_BACK -> {
                _navigation.tryEmit(BaseNavigation.Back)
            }

            WalletConst.GO_HOME -> {
                _navigation.tryEmit(Navigation.Home)
            }

            WalletConst.PIN_ACCEPTED -> {
                pinAccepted()
            }
        }
        when (event.action?.type) {
            IssuingConsts.ACCEPT_OFFER -> {
                acceptOffer()
            }

            ActionsConst.ACTION_NAVIGATE_BACK -> {
                _navigation.tryEmit(BaseNavigation.Back)
            }
        }
    }

    suspend fun handleDeepLinks() {
        deeplinkFlow.collectLatest {
            it?.getContentIfNotHandled()?.let { action ->
                val startFlow = action as? DeepLinkActionOpenNotify
                if (startFlow?.flowType == DeepLinkRoutes.CREDENTIAL_OFFER_RESUME) {
                    resumeOfferWithAuthorization(startFlow.resId)
                    return@let
                }

                deeplinkProcessor.handleDeepLinkAction(action).let { route ->
                    if (route != null) {
                        _processNavigation.value = UiDataEvent(route)
                    }
                }
            }
        }
    }

    private fun acceptOffer() {
        linkToProcess?.let {
            if (linkToProcess.isPreAuth()) {
                _navigation.tryEmit(Navigation.PreAuthPinF(linkToProcess))
            } else {
                _navigation.tryEmit(Navigation.PinInput)
            }
        }

    }

    private fun pinAccepted() {
        linkToProcess?.let {
            if (it.isPreAuth()) {
                viewModelScope.launch {
                    _navigation.emit(Navigation.PreAuthPinF(it))
                }
            } else {
                acceptDocumentOffer()
            }
        }
    }

    private fun showScreenContent(documents: List<DocumentOffer>) {
        clearScreen()
        showToolbar()
        showBottom()
        showBody(documents)
    }

    private fun processLink() {
        executeActionOnFlow {
            if (linkToProcess != null) {
                if (linkToProcess.isCredentialOfferLink()) {
                    resolveDocumentOffer(linkToProcess)
                } else {
                    resumeOfferWithAuthorization(linkToProcess)
                }
            }
        }

    }

    private fun resumeOfferWithAuthorization(urlToHandle: String) {
        documentOfferInteractor.resumeOpenId4VciWithAuthorization(uri = urlToHandle)
    }

    private suspend fun resolveDocumentOffer(urlToHandle: String) {
        documentOfferInteractor.resolveDocumentOffer(urlToHandle)
            .withLoader(_loader.setFullScreen(TridentWithBackNav))
            .collect { state ->
                when (state) {
                    is ResolveDocumentOfferResult.UntrustedIssuer -> {
                        showTemplateDialog(ewalletAlertDialogsFactory.untrustedIssuer())
                    }

                    is ResolveDocumentOfferResult.Failure -> {
                        showTemplateDialog(ewalletAlertDialogsFactory.docFailToReceive())
                    }

                    is ResolveDocumentOfferResult.PIDRequired -> {
                        showTemplateDialog(ewalletAlertDialogsFactory.pidNotFound())
                    }

                    is ResolveDocumentOfferResult.NoDocument -> {
                        showTemplateDialog(ewalletAlertDialogsFactory.docFailToReceive())
                    }

                    is ResolveDocumentOfferResult.Success -> {
                        showScreenContent(state.documents)
                    }
                }
            }
    }

    private fun acceptDocumentOffer() {
        if (!linkToProcess.isNullOrEmpty()) {
           executeActionOnFlow {
                documentOfferInteractor.issueDocumentsOffer(offerUri = linkToProcess)
                    .withLoader(_loader.setComponent(IssuingConsts.ACCEPT_OFFER_BTN))
                    .collect { state ->
                    when (state) {
                        is IssueDocumentOfferResult.DeferredSuccess -> {
                            showTemplateDialog(ewalletAlertDialogsFactory.deferredDocRequest())
                        }
                        is IssueDocumentOfferResult.Failure -> {
                            showTemplateDialog(ewalletAlertDialogsFactory.docFailToReceive())
                        }

                        is IssueDocumentOfferResult.Success -> {
                            showTemplateDialog(ewalletAlertDialogsFactory.docIssued())
                        }

                        is IssueDocumentOfferResult.AlreadyExists -> {
                            showTemplateDialog(ewalletAlertDialogsFactory.documentAlreadyExist())
                        }
                    }
                }
            }
        }
    }

    private fun showBottom() {
        _bottomData.add(documentOfferMapper.bottom())
    }

    private fun clearScreen() {
        _toolbarData.clear()
        _bottomData.clear()
        _bodyData.clear()
    }


    private fun showToolbar() {
        _toolbarData.add(documentOfferMapper.toolbar())
    }

    private fun showBody(documents: List<DocumentOffer>) {
        _bodyData.addAllFromListIfNotNull(documentOfferMapper.body(documents))
    }

    private fun String.isPreAuth() = this.contains("pre-authorized_code")
    private fun String.isCredentialOfferLink() = DeepLinkRoutes.isCredentialOfferLink(this)

    sealed interface Navigation : NavigationPath {
        object Home : Navigation
        data class PreAuthPinF(val linkToProcess: String) : Navigation
        data object PinInput : Navigation
    }
}
