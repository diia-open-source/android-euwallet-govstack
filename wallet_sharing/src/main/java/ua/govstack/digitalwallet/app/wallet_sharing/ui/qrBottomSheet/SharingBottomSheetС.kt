package ua.govstack.digitalwallet.app.wallet_sharing.ui.qrBottomSheet

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.provider.Settings.ACTION_BLUETOOTH_SETTINGS
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import kotlinx.coroutines.launch
import ua.govstack.digitalwallet.app.core.util.extensions.activity.getActivity
import ua.govstack.digitalwallet.app.ui_base.R
import ua.govstack.digitalwallet.app.ui_base.components.DiiaResourceIcon
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.collectAsEffect
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.permission.PermissionHandler
import ua.govstack.digitalwallet.app.ui_base.components.molecule.message.StubMessageMlc
import ua.govstack.digitalwallet.app.ui_base.components.noRippleClickable
import ua.govstack.digitalwallet.app.ui_base.components.subatomic.loader.LoaderSpinnerLoaderAtm
import ua.govstack.digitalwallet.app.ui_base.components.theme.BlackAlpha50
import ua.govstack.digitalwallet.app.ui_base.components.theme.DiiaTextStyle
import ua.govstack.digitalwallet.app.ui_base.components.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharingBottomSheet(
    alpha: Float,
    scaffoldState: BottomSheetScaffoldState,
    vm: SharingBottomSheetVM,
    navigateToSharingScreen: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val qr = vm.qr

    val context = LocalContext.current

    val errorBody by vm.errorBody.collectAsState()
    val bluetoothStateFlow = remember(vm) { vm.observeBluetoothState() }
    val isBluetoothOn by bluetoothStateFlow.collectAsState(
        initial = vm.isBluetoothEnabled()
    )
    val isSharingAvailable by vm.isSharingAvailable.collectAsState()

    DisposableEffect(scaffoldState.bottomSheetState.currentValue) {
        if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
            vm.onRequestPermissionTriggered()
        } else {
            vm.stopQREngagement()
        }
        onDispose {
            vm.stopQREngagement()
        }
    }


    val permissionFlow = vm.permissionFlow.collectAsState(null)

    LaunchedEffect(isBluetoothOn, isSharingAvailable,  scaffoldState.bottomSheetState.currentValue) {
        if (!isSharingAvailable && scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
            vm.showMsoDocRequiredState()
        } else if (!isBluetoothOn && scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
            vm.stopQREngagement()
            vm.showBluetoothRequiredState()
        }
    }

    vm.navigation.collectAsEffect { navigation ->
        when (navigation) {
            is SharingBottomSheetVM.ProximityNavigation.ToSharingScreen -> {
                navigateToSharingScreen.invoke()
            }
            is SharingBottomSheetVM.ProximityNavigation.ToAppSettings -> {
                goToAppSettings(context)
            }
            is SharingBottomSheetVM.ProximityNavigation.ToSettings -> {
                goToSettings(context)
            }
            is SharingBottomSheetVM.ProximityNavigation.Cancel -> {
                coroutineScope.launch {
                    scaffoldState.bottomSheetState.partialExpand()
                }
            }
        }
    }

    PermissionHandler(
        permission = permissionFlow.value,
        onPermissionGranted = {
            if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
                if (isBluetoothOn && isSharingAvailable) {
                    vm.startQREngagement()
                } else {
                    vm.showBluetoothRequiredState()
                }
            }
        },
        onPermissionDenied = {
            vm.showPermissionRequiredState()
        }
    )

    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(horizontal = 32.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                color = White.copy(alpha = 1 - (alpha * 2)),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        val (brow, card, button) = createRefs()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
                .constrainAs(brow) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .noRippleClickable {
                    coroutineScope.launch {
                        when (scaffoldState.bottomSheetState.currentValue) {
                            SheetValue.Hidden -> scaffoldState.bottomSheetState.expand()
                            SheetValue.Expanded -> scaffoldState.bottomSheetState.partialExpand()
                            SheetValue.PartiallyExpanded -> scaffoldState.bottomSheetState.expand()
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier
                    .padding(top = 12.dp),
                text = "QR-code",
                color = BlackAlpha50.copy(alpha = 1 - (alpha * 2)),
                style = DiiaTextStyle.t4TextSmallDescription
            )
        }
        Box(
            modifier = Modifier
                .constrainAs(card) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .aspectRatio(0.7F)
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
                .padding(horizontal = 20.dp), //should be 40 but look wierd
            contentAlignment = Alignment.Center
        ) {
            if (errorBody == null) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier.padding(top = 48.dp, bottom = 32.dp),
                        text = "Hold the QR code up to the reader",
                        color = Color.Black,
                        style = DiiaTextStyle.t3TextBody
                    )
                    if (qr.value != null && qr.value is Bitmap) {
                        Image(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f),
                            bitmap = (qr.value as Bitmap).asImageBitmap(),
                            contentScale = ContentScale.Fit,
                            contentDescription = stringResource(id = R.string.qr_code)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            LoaderSpinnerLoaderAtm()
                        }
                    }
                }
            } else {
                errorBody?.let { data ->
                    StubMessageMlc(
                        modifier = Modifier.padding(bottom = 64.dp),
                        data = data,
                        onUIAction = {
                            vm.onUIAction(it)
                        }
                    )
                }
            }
        }

        Image(
            modifier = Modifier
                .size(44.dp)
                .constrainAs(button) {
                    top.linkTo(card.bottom)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .noRippleClickable {
                    coroutineScope.launch {
                        scaffoldState.bottomSheetState.partialExpand()
                    }
                },
            painter = painterResource(
                id = DiiaResourceIcon.getResourceId(DiiaResourceIcon.ELLIPSE_CROSS.code)
            ),
            contentDescription = ""
        )
    }
}

fun goToAppSettings(context: Context) {
    val intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.getActivity()?.packageName, null)
    }
    context.startActivity(intent)
}

fun goToSettings(context: Context) {
    val intent = Intent(ACTION_BLUETOOTH_SETTINGS)
    context.startActivity(intent)
}
