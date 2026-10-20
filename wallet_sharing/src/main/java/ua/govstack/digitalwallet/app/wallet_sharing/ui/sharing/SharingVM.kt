package ua.govstack.digitalwallet.app.wallet_sharing.ui.sharing

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ua.govstack.digitalwallet.app.core.util.delegation.WithErrorHandlingOnFlow
import ua.govstack.digitalwallet.app.core.util.delegation.WithRetryLastAction
import ua.govstack.digitalwallet.app.core.util.extensions.mutableSharedFlowOf
import ua.govstack.digitalwallet.app.core.util.extensions.vm.executeActionOnFlow
import ua.govstack.digitalwallet.app.core.util.state.Circular
import ua.govstack.digitalwallet.app.core.util.state.Loader
import ua.govstack.digitalwallet.app.core.util.state.setComponent
import ua.govstack.digitalwallet.app.core.util.state.setFullScreen
import ua.govstack.digitalwallet.app.core.util.state.start
import ua.govstack.digitalwallet.app.core.util.state.stop
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.UIElementData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.addAllFromListIfNotNull
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIAction
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIActionKeysCompose
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.findAndChangeFirstByInstance
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.navigation.NavigationPath
import ua.govstack.digitalwallet.app.ui_base.components.organism.bottom.BottomGroupOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.container.BackgroundWhiteOrgData
import ua.govstack.digitalwallet.app.ui_base.navigation.BaseNavigation
import ua.govstack.digitalwallet.app.wallet_core.documents.helper.DocumentsHelper
import ua.govstack.digitalwallet.app.wallet_core.documents.model.ExpandableListItemUi
import ua.govstack.digitalwallet.app.wallet_core.documents.model.RequestDocumentItemUi
import ua.govstack.digitalwallet.app.wallet_core.ui.alertDialogsFactory.WalletAlertDialogFactory
import ua.govstack.digitalwallet.app.wallet_core.ui.conts.WalletConst
import ua.govstack.digitalwallet.app.wallet_sharing.data.manager.PresentationFeatureManager
import ua.govstack.digitalwallet.app.wallet_sharing.domain.interactor.ProximitySharingInteractor
import ua.govstack.digitalwallet.app.wallet_sharing.domain.interactor.RemoteSharingInteractor
import ua.govstack.digitalwallet.app.wallet_sharing.domain.model.PresentationMode
import ua.govstack.digitalwallet.app.wallet_sharing.domain.model.ProximitySharingLoadingResult
import ua.govstack.digitalwallet.app.wallet_sharing.domain.model.ProximitySharingRequestResult
import ua.govstack.digitalwallet.app.wallet_sharing.domain.model.ProximitySharingSendResult
import ua.govstack.digitalwallet.app.wallet_sharing.domain.model.ProximitySharingSuccessResult
import ua.govstack.digitalwallet.app.wallet_sharing.domain.model.RemoteSharingLoadingResult
import ua.govstack.digitalwallet.app.wallet_sharing.domain.model.RemoteSharingRequestResult
import ua.govstack.digitalwallet.app.wallet_sharing.domain.model.RemoteSharingSendResult
import ua.govstack.digitalwallet.app.wallet_sharing.domain.model.RemoteSharingSuccessResult
import ua.govstack.digitalwallet.app.wallet_sharing.domain.model.RequestUriConfig
import ua.govstack.digitalwallet.app.wallet_sharing.domain.observer.BluetoothConnectionObserver
import ua.govstack.digitalwallet.app.wallet_sharing.domain.transformer.RequestTransformer
import ua.govstack.digitalwallet.app.wallet_sharing.util.SharingConst
import javax.inject.Inject

@HiltViewModel
class SharingVM @Inject constructor(
    private val errorHandling: WithErrorHandlingOnFlow,
    private val retryActionDelegate: WithRetryLastAction,
    private val walletAlertDialogsFactory: WalletAlertDialogFactory,
    private val presentationManager: PresentationFeatureManager,
    private val documentsHelper: DocumentsHelper,
    private val bluetoothConnectionObserver: BluetoothConnectionObserver,
    private val mapper: SharingMapper,
) : ViewModel(),
    WithErrorHandlingOnFlow by errorHandling,
    WithRetryLastAction by retryActionDelegate,
    DocumentsHelper by documentsHelper {

    private val _loader = MutableStateFlow(Loader.create())
    val loader: Flow<Loader> = _loader

    private val _toolbarData = mutableStateListOf<UIElementData>()
    val toolbarData: SnapshotStateList<UIElementData> = _toolbarData

    private val _bodyData = mutableStateListOf<UIElementData>()
    val bodyData: SnapshotStateList<UIElementData> = _bodyData

    private val _bottomData = mutableStateListOf<UIElementData>()
    val bottomData: SnapshotStateList<UIElementData> = _bottomData

    private val _snackBarData = mutableStateListOf<UIElementData>()
    val snackBarData: SnapshotStateList<UIElementData> = _snackBarData

    private val _navigation = mutableSharedFlowOf<NavigationPath>()
    val navigation = _navigation.asSharedFlow()

    private val _showSnackBarEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val showSnackBarEvent = _showSnackBarEvent.asSharedFlow()

    private var verifierName: String? = null

    private var link: String? = null

    private val _sharingData = MutableStateFlow<List<RequestDocumentItemUi?>>(emptyList())
    val sharingData = _sharingData.asStateFlow()

    private fun <T> interactorOrNull(block: () -> T): T? =
        try {
            block()
        } catch (_: IllegalStateException) {
            null
        }

    val remoteSharingInteractor: RemoteSharingInteractor?
        get() = interactorOrNull { presentationManager.getRemoteSharingInteractor() }

    val proximitySharingInteractor: ProximitySharingInteractor?
        get() = interactorOrNull { presentationManager.getProximitySharingInteractor() }

    var isProximitySharing: Boolean = false

    init {
        presentationManager.initPresentationScope()
        buildInitialScreenContent()
        observeSharingDataChanges()
        observeBluetoothState()
    }

    sealed interface SharingNavigation : NavigationPath {
        data class OpenBrowser(val link: String) : SharingNavigation
        data object OpenPin : SharingNavigation
    }

    private fun buildInitialScreenContent() {
        generateScreenTopContent()
        generateSnackBarDataContent()
    }

    fun onUIAction(event: UIAction) {
        when (event.actionKey) {

            WalletConst.START_REMOTE_PRESENTATION -> {
                event.data?.let {
                   startPresentation(it)
                }
            }

            WalletConst.START_PROXIMITY_PRESENTATION -> {
               startProximity()
            }

            UIActionKeysCompose.BACKGROUND_WHITE_ORG -> {
                val selectedElementId = event.actionBundles.first().componentId ?: ""
                val wasSelected = sharingData.value.isElementSelected(selectedElementId)
                val changeResult = changeSharingDataStateById(selectedElementId)
                if (wasSelected && !changeResult.isElementSelected(selectedElementId)) {
                    showSnackBarMessage()
                }
                _sharingData.tryEmit(changeResult)
            }

            UIActionKeysCompose.TOOLBAR_NAVIGATION_BACK -> {
                _navigation.tryEmit(BaseNavigation.Back)
            }

            SharingConst.BTN_SHARE_DATA_ACTION_KEY -> {
                _loader.setComponent(SharingConst.BTN_SHARE_DATA_ACTION_KEY)
                _loader.start()
                _navigation.tryEmit(SharingNavigation.OpenPin)
            }

            WalletConst.PIN_ACCEPTED -> {
                doAfterPin()
            }
        }
    }

    private fun startPresentation(link: String) {
        if (this.link != link) {
            this.link = link
            doRemoteRequestWork(link)
            doRemoteLoadingWork()
        }
    }

    private fun startProximity() {
        if (!isProximitySharing) {
            isProximitySharing = true
            doProximityRequestWork()
            doProximityLoadingWork()
        }
    }

    private fun doAfterPin() {
        executeActionOnFlow(dispatcher = Dispatchers.IO) {
            _loader.setComponent(SharingConst.BTN_SHARE_DATA_ACTION_KEY)
            _loader.start()
            if (isProximitySharing) {
                sendProximityRequestedDocuments()
            } else {
                sendRemoteRequestedDocuments()
            }
        }
    }

    private fun generateScreenTopContent() {
        _toolbarData.addAllFromListIfNotNull(listOf(mapper.toolbar()))
    }

    private fun generateScreenBottomContent() {
        _bottomData.addAllFromListIfNotNull(listOf(mapper.bottomGroup()))
    }

    private fun generateSnackBarDataContent() {
        _snackBarData.addAllFromListIfNotNull(listOf(mapper.snackBar()))
    }

    private fun showSnackBarMessage() {
        _showSnackBarEvent.tryEmit(Unit)
    }

    private fun observeBluetoothState() {
        viewModelScope.launch {
            bluetoothConnectionObserver.observeBluetoothState().collect { isBluetoothOn ->
                if (isProximitySharing && !isBluetoothOn) {
                    isProximitySharing = false
                    proximitySharingInteractor?.stopPresentation()
                    _loader.stop()
                    showTemplateDialog(walletAlertDialogsFactory.bluetoothLost())
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        remoteSharingInteractor?.stopPresentation()
        proximitySharingInteractor?.stopPresentation()
        presentationManager.stopPresentationScope()
    }

    private fun doRemoteRequestWork(uri: String) {
        val config = RequestUriConfig(
            PresentationMode.OpenId4Vp(
                uri = uri,
            )
        )
        remoteSharingInteractor?.setConfig(config)
        viewModelScope.launch {
            _loader.setFullScreen(Circular)
            _loader.start()
            remoteSharingInteractor?.getRequestDocuments()?.collect { result ->
                when (result) {
                    is RemoteSharingRequestResult.Failure -> {
                        showTemplateDialog(walletAlertDialogsFactory.failToHandleSharingRequest())
                    }

                    is RemoteSharingRequestResult.UntrustedVerifier -> {
                        remoteSharingInteractor?.stopPresentation()
                        showTemplateDialog(walletAlertDialogsFactory.untrustedVerifier())
                    }

                    is RemoteSharingRequestResult.Success -> {
                        result.verifierName?.let {
                            verifierName = it
                        }
                        val items = mapper.toRequestDocumentItems(result.documentPayloads)
                        val disclosed = RequestTransformer.createDisclosedDocuments(items)
                        remoteSharingInteractor?.updateRequestedDocuments(disclosed)
                        _sharingData.tryEmit(items)
                    }

                    is RemoteSharingRequestResult.Disconnect -> {
                        showTemplateDialog(walletAlertDialogsFactory.serviceDisconnected())
                    }

                    is RemoteSharingRequestResult.NoData -> {
                        showTemplateDialog(walletAlertDialogsFactory.failToShareNoDocFound())
                    }
                }
                _loader.stop()
            }
        }
    }

    private fun doProximityRequestWork() {
        viewModelScope.launch {
            _loader.setFullScreen(Circular)
            _loader.start()
            proximitySharingInteractor?.getRequestDocuments()?.collect { response ->
                when (response) {
                    ProximitySharingRequestResult.Disconnect -> {
                        showTemplateDialog(walletAlertDialogsFactory.failToHandleSharingRequest())
                    }

                    is ProximitySharingRequestResult.Failure -> {
                        showTemplateDialog(walletAlertDialogsFactory.failToHandleSharingRequest())
                    }

                    is ProximitySharingRequestResult.UntrustedVerifier -> {
                        proximitySharingInteractor?.stopPresentation()
                        showTemplateDialog(walletAlertDialogsFactory.untrustedVerifier())
                    }

                    is ProximitySharingRequestResult.NoData -> {
                        showTemplateDialog(walletAlertDialogsFactory.failToShareNoDocFound())
                    }

                    is ProximitySharingRequestResult.Success -> {
                        response.verifierName?.let {
                            verifierName = it
                        }
                        val items = mapper.toRequestDocumentItems(response.documentPayloads)
                        val disclosed = RequestTransformer.createDisclosedDocuments(items)
                        proximitySharingInteractor?.updateRequestedDocuments(disclosed)
                        _sharingData.tryEmit(items)
                    }
                }
                _loader.stop()
            }
        }
    }

    private fun observeSharingDataChanges() {
        viewModelScope.launch(start = CoroutineStart.UNDISPATCHED) {
            sharingData.collect { data ->
                if (data.isEmpty()) return@collect
                val result: MutableList<UIElementData> = mutableListOf()
                val newDataAsUi = mapper.mapToClaimList(data)
                if (_bodyData.isEmpty()) {
                    result.add(mapper.sharingHeader())
                    result.add(mapper.infoBlock())
                    result.add(mapper.requesterHeader(verifierName))
                    result.addAll(newDataAsUi)
                    _bodyData.addAllFromListIfNotNull(result)
                    generateScreenBottomContent()
                } else {
                    updateSharingUI(newDataAsUi)
                    updateBottomUI(data)
                }
            }
        }
    }

    fun changeSharingDataStateById(clickedElementId: String): List<RequestDocumentItemUi> {
        val currentState = sharingData.value
        val newState = mutableListOf<RequestDocumentItemUi>()
        val (documentId, _) = clickedElementId.split(",")
        newState.apply {
            currentState.filterNotNull().forEach { document ->
                if (document.headerUi.header.headerId != documentId) {
                    newState.add(document)
                } else {
                    add(mapper.changedDocument(document, clickedElementId))
                }
            }
        }
        val disclosed = RequestTransformer.createDisclosedDocuments(newState)
        if (isProximitySharing) {
            proximitySharingInteractor?.updateRequestedDocuments(disclosed)
        } else {
            remoteSharingInteractor?.updateRequestedDocuments(disclosed)
        }
        return newState
    }


    fun updateSharingUI(newState: List<UIElementData>) {
        _bodyData.forEachIndexed { index, data ->
            if (data is BackgroundWhiteOrgData) {
                newState.forEach { newStateItem ->
                    if (data.componentId == (newStateItem as BackgroundWhiteOrgData).componentId
                        && data.hashCode() != newStateItem.hashCode()
                    ) {
                        _bodyData[index] = newStateItem
                    }
                }
            }
        }
    }

    fun updateBottomUI(newState: List<RequestDocumentItemUi?>) {
        var buttonEnabled = true
        newState.filterNotNull().forEach { document ->
            if (document.headerUi.nestedItems.none { item -> item.header.isSelected }) {
                buttonEnabled = false
            }
        }
        _bottomData.findAndChangeFirstByInstance<BottomGroupOrgData> {
            mapper.bottomGroupWithState(it, buttonEnabled)
        }
    }

    private fun doRemoteLoadingWork() {
        viewModelScope.launch {
            remoteSharingInteractor?.observeResponse()?.collect { state ->
                when (state) {
                    is RemoteSharingLoadingResult.Failure -> {
                        showTemplateDialog(walletAlertDialogsFactory.failToHandleSharingRequest())
                    }

                    is RemoteSharingLoadingResult.Redirect -> {
                        _navigation.tryEmit(SharingNavigation.OpenBrowser(state.uri.toString()))
                    }

                    RemoteSharingLoadingResult.RequestReadyToBeSent -> {
                        //no-op
                    }

                    RemoteSharingLoadingResult.Success -> {
                        doRemoteSuccessWork()
                    }
                }

            }
        }
    }

    private fun doProximityLoadingWork() {
        viewModelScope.launch {
            proximitySharingInteractor?.observeResponse()?.collect { state ->
                when (state) {
                    is ProximitySharingLoadingResult.Failure -> {
                        showTemplateDialog(
                            walletAlertDialogsFactory.failToHandleSharingRequest()
                        )
                    }

                    ProximitySharingLoadingResult.RequestReadyToBeSent -> Unit

                    ProximitySharingLoadingResult.Success -> {
                        doProximitySuccessWork()
                    }
                }

            }
        }
    }

    private fun doRemoteSuccessWork() {
        viewModelScope.launch {
            remoteSharingInteractor?.getSharedDocuments()?.collect { state ->
                when (state) {
                    is RemoteSharingSuccessResult.Failed -> {
                        showTemplateDialog(walletAlertDialogsFactory.errorOccurred())
                    }

                    is RemoteSharingSuccessResult.Success -> {
                        remoteSharingInteractor?.stopPresentation()
                        showTemplateDialog(
                            walletAlertDialogsFactory.dataSharedSuccessfully()
                        )
                    }
                }

            }
        }
    }

    private fun doProximitySuccessWork() {
        viewModelScope.launch {
            proximitySharingInteractor?.getSharedDocuments()?.collect { state ->
                when (state) {
                    is ProximitySharingSuccessResult.Failed -> {
                        //no-op
                    }

                    is ProximitySharingSuccessResult.Success -> {
                        proximitySharingInteractor?.stopPresentation()
                        showTemplateDialog(
                            walletAlertDialogsFactory.dataSharedSuccessfully()
                        )
                    }
                }

            }
        }
    }

    private fun sendRemoteRequestedDocuments() {
        remoteSharingInteractor?.sendRequestedDocuments()?.let {
            when (it) {
                is RemoteSharingSendResult.Success -> {
                    //no-op
                }

                is RemoteSharingSendResult.Failure -> {
                    showTemplateDialog(walletAlertDialogsFactory.errorOccurred())
                }
            }
        }
    }

    private fun sendProximityRequestedDocuments() {
        proximitySharingInteractor?.sendRequestedDocuments()?.let {
            when (it) {
                is ProximitySharingSendResult.Failure -> {
                    showTemplateDialog(walletAlertDialogsFactory.errorOccurred())
                }

                ProximitySharingSendResult.Success -> {
                    //no-op
                }
            }
        }
    }
}

private fun List<RequestDocumentItemUi?>.isElementSelected(elementId: String): Boolean {
    return filterNotNull().any { document ->
        document.headerUi.findItemById(elementId)?.header?.isSelected == true
    }
}

private fun ExpandableListItemUi.findItemById(elementId: String): ExpandableListItemUi? = when (this) {
    is ExpandableListItemUi.SingleListItem -> takeIf { header.headerId == elementId }
    is ExpandableListItemUi.NestedListItem -> {
        if (header.headerId == elementId) {
            this
        } else {
            nestedItems.firstNotNullOfOrNull { it.findItemById(elementId) }
        }
    }
}
