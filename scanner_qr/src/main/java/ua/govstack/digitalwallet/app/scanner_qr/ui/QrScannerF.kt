package ua.govstack.digitalwallet.app.scanner_qr.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ua.govstack.digitalwallet.app.core.util.extensions.fragment.doOnSystemBackPressed
import ua.govstack.digitalwallet.app.scanner_qr.util.prepareInputImage
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.collectAsEffect
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIAction
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIActionKeysCompose
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.UiText
import ua.govstack.digitalwallet.app.ui_base.components.molecule.header.NavigationPanelMlcData
import ua.govstack.digitalwallet.app.ui_base.components.theme.White
import ua.govstack.digitalwallet.app.ui_base.models.orientation.Orientation
import ua.govstack.digitalwallet.app.ui_base.navigation.BaseNavigation

@AndroidEntryPoint
class QrScannerF : Fragment() {

    private val viewModel: QrScannerVM by viewModels()
    private var composeView: ComposeView? = null

    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            viewModel.onCameraPermissionResult(granted)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        composeView = ComposeView(requireContext())
        return composeView!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        composeView?.setContent {
            val scannerUiState by viewModel.scannerUiState.collectAsState()

            viewModel.navigation.collectAsEffect { navigation ->
                if (navigation is BaseNavigation.Back) {
                    findNavController().popBackStack()
                }
            }

            val context = LocalContext.current
            LaunchedEffect(scannerUiState.hasCameraPermission) {
                if (!scannerUiState.hasCameraPermission && !scannerUiState.closeCameraView) {
                    val isGranted = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                    if (isGranted) {
                        viewModel.onCameraPermissionResult(true)
                    } else {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }
            }

            BarcodeScannerScreen(
                data = BarcodeScannerScreenData(
                    navigationPanelMlcData = NavigationPanelMlcData(
                        title = UiText.DynamicString("Scanner"),
                        isContextMenuExist = false,
                        tintColor = White
                    ),
                    description = UiText.DynamicString(scannerUiState.description),
                    errorDescription = UiText.DynamicString(scannerUiState.errorDescription),
                    closeCameraView = !scannerUiState.hasCameraPermission || scannerUiState.closeCameraView,
                    isFlashOn = scannerUiState.isFlashOn
                ),
                onUIAction = { viewModel.onUIAction(it) },
                processFrame = { frame, camera, boundingRect ->
                    if (!scannerUiState.hasCameraPermission || scannerUiState.closeCameraView) {
                        return@BarcodeScannerScreen
                    }
                    prepareInputImage(frame, camera, boundingRect)?.let(viewModel::processInputImage)
                },
                orientation = Orientation.Portrait
            )
        }

        doOnSystemBackPressed {
            viewModel.onUIAction(UIAction(actionKey = UIActionKeysCompose.TOOLBAR_NAVIGATION_BACK))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        composeView = null
    }
}
