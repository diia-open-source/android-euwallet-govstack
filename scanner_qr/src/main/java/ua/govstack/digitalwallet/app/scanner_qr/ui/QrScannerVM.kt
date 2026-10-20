package ua.govstack.digitalwallet.app.scanner_qr.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ua.govstack.digitalwallet.app.core.models.deeplink.DeepLinkAction
import ua.govstack.digitalwallet.app.core.util.DispatcherProvider
import ua.govstack.digitalwallet.app.core.util.deeplink.DeepLinkRoutes
import ua.govstack.digitalwallet.app.core.util.delegation.WithDeeplinkHandling
import ua.govstack.digitalwallet.app.core.util.event.UiDataEvent
import ua.govstack.digitalwallet.app.scanner_qr.di.BarcodeScannerQR
import ua.govstack.digitalwallet.app.scanner_qr.model.BarcodeScannerResult
import ua.govstack.digitalwallet.app.scanner_qr.util.DiiaScanner
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIAction
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIActionKeysCompose
import ua.govstack.digitalwallet.app.ui_base.navigation.BaseNavigation
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@HiltViewModel
class QrScannerVM @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val withDeeplinkHandling: WithDeeplinkHandling,
    @BarcodeScannerQR private val qrScanner: DiiaScanner
) : ViewModel() {

    private val _scannerUiState = MutableStateFlow(QrScannerUiState())
    val scannerUiState = _scannerUiState.asStateFlow()

    private val _navigation = MutableSharedFlow<BaseNavigation>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val navigation = _navigation.asSharedFlow()

    private val frameProcessing = AtomicBoolean(false)

    init {
        observeScannerResults()
    }

    fun onUIAction(event: UIAction) {
        when (event.actionKey) {
            UIActionKeysCompose.TOOLBAR_NAVIGATION_BACK -> {
                _navigation.tryEmit(BaseNavigation.Back)
            }

            UIActionKeysCompose.BTN_FLASH_ON_OFF -> {
                _scannerUiState.update { state ->
                    state.copy(isFlashOn = state.isFlashOn.not())
                }
            }
        }
    }

    fun onCameraPermissionResult(granted: Boolean) {
        if (granted) {
            _scannerUiState.update { state ->
                state.copy(
                    hasCameraPermission = true,
                    closeCameraView = false,
                    description = DEFAULT_DESCRIPTION,
                    errorDescription = ""
                )
            }
            qrScanner.init()
        } else {
            _scannerUiState.update { state ->
                state.copy(
                    closeCameraView = true,
                    description = PERMISSION_DENIED_DESCRIPTION,
                    errorDescription = ""
                )
            }
        }
    }

    fun processInputImage(inputImage: InputImage) {
        val state = _scannerUiState.value
        if (!state.hasCameraPermission || state.closeCameraView) return
        if (!frameProcessing.compareAndSet(false, true)) return

        viewModelScope.launch(dispatcherProvider.ioDispatcher()) {
            try {
                qrScanner.processImage(inputImage)
            } finally {
                frameProcessing.set(false)
            }
        }
    }

    private fun observeScannerResults() {
        viewModelScope.launch {
            qrScanner.scanResultFlow.collect { result ->
                when (result) {
                    is BarcodeScannerResult.BarcodeDetected -> {
                        if (result.barcode.isRegisteredQrCode()) {
                            qrScanner.dismiss()
                            frameProcessing.set(false)
                            _scannerUiState.update { state ->
                                state.copy(
                                    closeCameraView = true,
                                    isFlashOn = false
                                )
                            }
                            emitScannedValueAsDeeplink(result.barcode)
                        } else {
                            showQrNotRegisteredError()
                        }
                    }

                    is BarcodeScannerResult.BarcodeCorrupted -> {
                        showQrNotRegisteredError()
                    }

                    is BarcodeScannerResult.BarcodeNotDetected -> Unit
                }
            }
        }
    }

    private suspend fun emitScannedValueAsDeeplink(barcode: String) {
        val action = withDeeplinkHandling.buildDeepLinkAction(barcode)
        emitDeeplink(action)
        _navigation.tryEmit(BaseNavigation.Back)
    }

    private suspend fun emitDeeplink(action: DeepLinkAction) {
        withDeeplinkHandling.emitDeeplink(UiDataEvent(action))
    }

    private fun showQrNotRegisteredError() {
        _scannerUiState.update { state ->
            state.copy(errorDescription = QR_NOT_REGISTERED_DESCRIPTION)
        }
    }

    private fun String.isRegisteredQrCode(): Boolean {
        return REGISTERED_QR_MARKERS.any { marker -> contains(marker) }
    }

    override fun onCleared() {
        qrScanner.dismiss()
        super.onCleared()
    }

    data class QrScannerUiState(
        val hasCameraPermission: Boolean = false,
        val isFlashOn: Boolean = false,
        val closeCameraView: Boolean = false,
        val description: String = DEFAULT_DESCRIPTION,
        val errorDescription: String = ""
    )

    companion object {
        private const val DEFAULT_DESCRIPTION =
            "Point your camera at the document's QR code"
        private const val PERMISSION_DENIED_DESCRIPTION =
            "Camera permission is required to scan QR codes."
        private const val QR_NOT_REGISTERED_DESCRIPTION =
            "The QR code is not registered"
        private val REGISTERED_QR_MARKERS = DeepLinkRoutes.qrMarkers.toSet()
    }
}
