package ua.govstack.digitalwallet.app.wallet_sharing.ui.qrBottomSheet

import android.graphics.Bitmap
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.europa.ec.eudi.iso18013.transfer.engagement.QrCode
import eu.europa.ec.eudi.wallet.document.Document
import eu.europa.ec.eudi.wallet.document.IssuedDocument
import eu.europa.ec.eudi.wallet.document.format.MsoMdocFormat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ua.govstack.digitalwallet.app.core.models.ConsumableItem
import ua.govstack.digitalwallet.app.core.ui.dynamicdialog.ActionsConst
import ua.govstack.digitalwallet.app.core.util.delegation.Permission
import ua.govstack.digitalwallet.app.core.util.delegation.WithErrorHandlingOnFlow
import ua.govstack.digitalwallet.app.core.util.delegation.WithRetryLastAction
import ua.govstack.digitalwallet.app.core.util.extensions.mutableSharedFlowOf
import ua.govstack.digitalwallet.app.ui_base.components.atom.button.ButtonStrokeAdditionalAtomData
import ua.govstack.digitalwallet.app.ui_base.components.atom.text.textwithparameter.TextWithParametersData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIAction
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.navigation.NavigationPath
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.state.UIState
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.UiText
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.toDynamicString
import ua.govstack.digitalwallet.app.ui_base.components.molecule.message.StubMessageMlcData
import ua.govstack.digitalwallet.app.wallet_core.documents.store.DocumentStore
import ua.govstack.digitalwallet.app.wallet_core.ui.conts.WalletConst
import ua.govstack.digitalwallet.app.wallet_sharing.R
import ua.govstack.digitalwallet.app.wallet_sharing.data.manager.PresentationFeatureManager
import ua.govstack.digitalwallet.app.wallet_sharing.domain.interactor.ProximityQRInteractor
import ua.govstack.digitalwallet.app.wallet_sharing.domain.interactor.ProximityQRResult
import ua.govstack.digitalwallet.app.wallet_sharing.domain.model.PresentationMode
import ua.govstack.digitalwallet.app.wallet_sharing.domain.model.RequestUriConfig
import ua.govstack.digitalwallet.app.wallet_sharing.domain.observer.BluetoothConnectionObserver
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class SharingBottomSheetVM @Inject constructor(
    private val errorHandling: WithErrorHandlingOnFlow,
    private val retryActionDelegate: WithRetryLastAction,
    private val presentationManager: PresentationFeatureManager,
    private val bluetoothConnectionObserver: BluetoothConnectionObserver,
    private val documentStore: DocumentStore,
) : ViewModel(),
    WithErrorHandlingOnFlow by errorHandling,
    WithRetryLastAction by retryActionDelegate {

    private val _permissionFlow = MutableStateFlow<ConsumableItem?>(null)
    val permissionFlow = _permissionFlow.asSharedFlow()

    private val _qr = mutableStateOf<Bitmap?>(null)
    val qr: State<Bitmap?> = _qr

    private val _errorBody = MutableStateFlow<StubMessageMlcData?>(null)
    val errorBody = _errorBody.asStateFlow()

    private val _navigation = mutableSharedFlowOf<NavigationPath>()
    val navigation = _navigation.asSharedFlow()

    private var qrEngagementInProgress: Boolean = false
    private var connectionCreated: Boolean = false

    private val _isSharingAvailable = MutableStateFlow(false)
    val isSharingAvailable = _isSharingAvailable.asStateFlow()

    init {
        qrEngagementInProgress = false
        connectionCreated = false
        observeMsoMdocDocumentsAvailability()
    }

    val proximityQRInteractor: ProximityQRInteractor?
        get() {
            return try {
                presentationManager.getProximityQRInteractor()
            } catch (e: IllegalStateException) {
                null
            }
        }


    fun startQREngagement() {
        if (!qrEngagementInProgress) {
            _errorBody.tryEmit(null)
            presentationManager.initPresentationScope()
            qrEngagementInProgress = true
            proximityQRInteractor?.setConfig(
                RequestUriConfig(mode = PresentationMode.Ble)
            )
            generateQRCode()
        }
    }

    fun showPermissionRequiredState() {
        _errorBody.tryEmit(
            StubMessageMlcData(
                icon = "".toDynamicString(),
                title = UiText.StringResource(R.string.sharing_bottom_sheet_bluetooth_permission_title),
                description = TextWithParametersData(
                    text = UiText.StringResource(R.string.sharing_bottom_sheet_bluetooth_permission_description)
                ),
                button = ButtonStrokeAdditionalAtomData(
                    actionKey = WalletConst.TO_APP_SETTINGS,
                    title = UiText.StringResource(R.string.sharing_bottom_sheet_go_to_settings),
                    interactionState = UIState.Interaction.Enabled
                )
            )
        )
    }

    fun showMsoDocRequiredState() {
        _errorBody.tryEmit(
            StubMessageMlcData(
                icon = "".toDynamicString(),
                title = UiText.StringResource(R.string.sharing_bottom_sheet_msodoc_title),
                description = TextWithParametersData(
                    text = UiText.StringResource(R.string.sharing_bottom_sheet_msodoc_description)
                ),
                button = ButtonStrokeAdditionalAtomData(
                    actionKey = ActionsConst.DIALOG_ACTION_CANCEL,
                    title = UiText.StringResource(R.string.sharing_bottom_sheet_msodoc_btn),
                    interactionState = UIState.Interaction.Enabled
                )
            )
        )
    }

    fun showBluetoothRequiredState() {
        _errorBody.tryEmit(
            StubMessageMlcData(
                icon = "".toDynamicString(),
                title = UiText.StringResource(R.string.sharing_bottom_sheet_bluetooth_disabled_title),
                description = TextWithParametersData(
                    text = UiText.StringResource(R.string.sharing_bottom_sheet_bluetooth_disabled_description)
                ),
                button = ButtonStrokeAdditionalAtomData(
                    actionKey = WalletConst.TO_SETTINGS,
                    title = UiText.StringResource(R.string.sharing_bottom_sheet_go_to_settings),
                    interactionState = UIState.Interaction.Enabled
                )
            )
        )
    }

    fun generateQRCode() {
        viewModelScope.launch {
            proximityQRInteractor?.startQrEngagement()?.collect { response ->
                when (response) {
                    ProximityQRResult.Connected -> {
                        connectionCreated = true
                        _navigation.tryEmit(ProximityNavigation.ToSharingScreen)
                    }

                    ProximityQRResult.Disconnected -> {
                    }

                    is ProximityQRResult.Error -> {
                    }

                    is ProximityQRResult.QrReady -> {
                        _qr.value = QrCode(response.qrCode).asBitmap(400)
                    }
                }
            }
        }
    }

    fun stopQREngagement() {
        if (qrEngagementInProgress) {
            qrEngagementInProgress = false
            _qr.value = null
            if (!connectionCreated) {
                proximityQRInteractor?.cancelTransfer()
                presentationManager.stopPresentationScope()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (!connectionCreated) {
            stopQREngagement()
        }
    }

    fun onUIAction(event: UIAction) {
        when (event.actionKey) {
            WalletConst.TO_APP_SETTINGS -> {
                _navigation.tryEmit(ProximityNavigation.ToAppSettings)
            }

            WalletConst.TO_SETTINGS -> {
                _navigation.tryEmit(ProximityNavigation.ToSettings)
            }

            ActionsConst.DIALOG_ACTION_CANCEL -> {
                _navigation.tryEmit(ProximityNavigation.Cancel)
            }
        }
    }

    sealed class ProximityNavigation : NavigationPath {
        object ToSharingScreen : NavigationPath
        object ToAppSettings : NavigationPath
        object ToSettings : NavigationPath
        object Cancel : NavigationPath
    }


    fun onRequestPermissionTriggered() {
        if(_isSharingAvailable.value) {
            _permissionFlow.tryEmit(ConsumableItem(Permission.BLUETOOTH_CONNECT))
        }
    }

    fun observeBluetoothState() = bluetoothConnectionObserver.observeBluetoothState()

    fun isBluetoothEnabled() = bluetoothConnectionObserver.isBluetoothEnabled()

    private fun observeMsoMdocDocumentsAvailability() {
        viewModelScope.launch {
            documentStore.documentsData.collect { documents ->
                val revokedDocumentIds = documentStore.getRevokedDocuments().map { it.identifier }
                _isSharingAvailable.value = documents.hasAvailableToShareDocument(revokedDocumentIds)
            }
        }
    }

    private suspend fun List<Document>.hasAvailableToShareDocument(revokedDocumentIds: List<String>): Boolean {
       return filterIsInstance<IssuedDocument>().any {
           val docHasExpired = it.getValidUntil().getOrNull().hasExpiredAt(Instant.now())
            it.format is MsoMdocFormat && !revokedDocumentIds.contains(it.id) && !docHasExpired
        }
    }

    private fun Instant?.hasExpiredAt(now: Instant): Boolean {
        return this?.isBefore(now) == true
    }

}