package ua.govstack.digitalwallet.app.core.util.delegation

import android.Manifest
import android.os.Build
import android.os.Parcelable
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LiveData
import kotlinx.parcelize.Parcelize
import ua.govstack.digitalwallet.app.core.R
import ua.govstack.digitalwallet.app.core.models.common.template_dialogs.SystemDialogData
import ua.govstack.digitalwallet.app.core.util.event.UiEvent

@Parcelize
enum class Permission : Parcelable{
    CAMERA,
    LOCATION,
    AUDIO,
    PHONE_STATE,
    STORAGE_READ,
    STORAGE_WRITE,
    BLUETOOTH_CONNECT,
    POST_NOTIFICATIONS;

    val value: Array<String>
        get() = when (this) {
            CAMERA -> arrayOf(Manifest.permission.CAMERA)
            LOCATION -> arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            STORAGE_READ -> arrayOf("Manifest.permission.READ_EXTERNAL_STORAGE")
            STORAGE_WRITE -> arrayOf("Manifest.permission.WRITE_EXTERNAL_STORAGE")
            BLUETOOTH_CONNECT -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // Android 12+ (API 31+)
                arrayOf(
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_ADVERTISE
                )
            } else {
                // Android < 12 (API < 31)
                arrayOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
            POST_NOTIFICATIONS -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    arrayOf("android.permission.POST_NOTIFICATIONS")
                }
            }
            AUDIO -> arrayOf(Manifest.permission.RECORD_AUDIO)
            PHONE_STATE -> arrayOf(Manifest.permission.READ_PHONE_STATE)
        }

    val permissionDialog: SystemDialogData
        get() = SystemDialogData(
            title = dialogTitle,
            message = dialogText,
            positiveButtonTitle = R.string.allow,
            negativeButtonTitle = R.string.deny,
            cancelable = false,
            rationale = rationale,
            rationaleTitle = rationaleTitle
        )

    val rationaleDialog: SystemDialogData
        get() = SystemDialogData(
            title = rationaleDialogTitle,
            message = rationalDialogText,
            positiveButtonTitle = R.string.open_settings,
            negativeButtonTitle = R.string.close,
            cancelable = false,
            rationale = rationale,
            rationaleTitle = rationaleTitle
        )

    private val dialogTitle: Int
        @StringRes
        get() = when (this) {
            CAMERA -> R.string.camera_permission_request_title
            LOCATION  -> R.string.location_permission_request_title
            STORAGE_READ,
            STORAGE_WRITE -> R.string.file_permission_request_title

            POST_NOTIFICATIONS -> R.string.notifications_permission_request_title
            AUDIO -> R.string.audio_permission_request_title
            PHONE_STATE -> R.string.phone_state_permission_request_title
            BLUETOOTH_CONNECT -> R.string.bluetooth_permission_request_title

        }

    private val rationaleDialogTitle: Int
        @StringRes
        get() = when (this) {
            CAMERA -> R.string.camera_permission_request_title_rational
            LOCATION -> R.string.location_permission_request_title
            STORAGE_READ,
            STORAGE_WRITE -> R.string.file_permission_request_title

            POST_NOTIFICATIONS -> R.string.notifications_permission_request_title
            AUDIO -> R.string.audio_permission_request_title
            PHONE_STATE -> R.string.phone_state_permission_request_title
            BLUETOOTH_CONNECT -> R.string.bluetooth_permission_request_title
        }

    private val dialogText: Int
        @StringRes
        get() = when (this) {
            CAMERA -> R.string.camera_permission_request
            LOCATION -> R.string.location_permission_request
            STORAGE_READ,
            STORAGE_WRITE -> R.string.file_permission_request

            POST_NOTIFICATIONS -> R.string.notifications_permission_request
            AUDIO -> R.string.audio_permission_request
            PHONE_STATE -> R.string.phone_state_permission_request
            BLUETOOTH_CONNECT -> R.string.bluetooth_permission_request
        }

    private val rationalDialogText: Int
        @StringRes
        get() = when (this) {
            CAMERA -> R.string.camera_permission_request_rational
            LOCATION -> R.string.location_permission_request
            STORAGE_READ,
            STORAGE_WRITE -> R.string.file_permission_request

            POST_NOTIFICATIONS -> R.string.notifications_permission_request
            AUDIO -> R.string.audio_permission_request
            PHONE_STATE -> R.string.phone_state_permission_request
            BLUETOOTH_CONNECT -> R.string.bluetooth_permission_request
        }

    private val rationaleTitle: Int?
        @StringRes
        get() = when (this) {
            CAMERA -> null
            LOCATION -> R.string.location_permission_rationale_title
            STORAGE_READ,
            STORAGE_WRITE -> R.string.file_permission_rationale_title

            POST_NOTIFICATIONS -> null
            AUDIO -> R.string.audio_permission_rationale_title
            PHONE_STATE -> R.string.phone_state_permission_rationale_title
            BLUETOOTH_CONNECT ->  R.string.bluetooth_permission_rationale_title
        }

    private val rationale: Int?
        @StringRes
        get() = when (this) {
            CAMERA -> R.string.camera_permission_rationale
            LOCATION -> R.string.location_permission_rationale
            STORAGE_READ,
            STORAGE_WRITE -> R.string.file_permission_rationale

            POST_NOTIFICATIONS -> null
            AUDIO -> R.string.audio_permission_rationale
            PHONE_STATE -> R.string.phone_state_permission_rationale
            BLUETOOTH_CONNECT -> R.string.bluetooth_permission_rationale
        }
}

interface WithPermission : DefaultLifecycleObserver {

    val doOnCameraPermissionGrantedEvent: LiveData<UiEvent>

    val doOnGeoPermissionGrantedEvent: LiveData<UiEvent>

    val doOnPostNotificationPermissionGrantedEvent: LiveData<UiEvent>

    val doOnStoragePermissionGrantedEvent: LiveData<UiEvent>

    val doOnAudioPermissionGrantedEvent: LiveData<UiEvent>

    val doOnPhoneStatePermissionGrantedEvent: LiveData<UiEvent>

    val doOnBluetoothPermissionGrantedEvent: LiveData<UiEvent>

    val doOnPermissionDeniedEvent: LiveData<UiEvent>

    fun <T : Fragment> T.approvePermission(permission: Permission)

    fun <T : Fragment> T.requestAppSettings(permission: Permission)
}
