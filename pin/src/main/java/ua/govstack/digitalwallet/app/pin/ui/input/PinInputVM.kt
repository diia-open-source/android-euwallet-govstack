package ua.govstack.digitalwallet.app.pin.ui.input

import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.transform
import ua.govstack.digitalwallet.app.core.di.actions.GlobalActionLogout
import ua.govstack.digitalwallet.app.core.di.actions.GlobalActionRunDeepLinkAfterPin
import ua.govstack.digitalwallet.app.core.models.dialogs.TemplateDialogButton
import ua.govstack.digitalwallet.app.core.models.dialogs.TemplateDialogData
import ua.govstack.digitalwallet.app.core.models.dialogs.TemplateDialogModel
import ua.govstack.digitalwallet.app.core.ui.dynamicdialog.ActionsConst.DIALOG_ACTION_CANCEL
import ua.govstack.digitalwallet.app.core.ui.dynamicdialog.ActionsConst.DIALOG_ACTION_CODE_LOGOUT
import ua.govstack.digitalwallet.app.core.ui.dynamicdialog.ActionsConst.FRAGMENT_USER_ACTION_RESULT_KEY
import ua.govstack.digitalwallet.app.core.util.delegation.WithErrorHandlingOnFlow
import ua.govstack.digitalwallet.app.core.util.delegation.WithRetryLastAction
import ua.govstack.digitalwallet.app.core.util.event.UiEvent
import ua.govstack.digitalwallet.app.core.util.extensions.vm.executeActionOnFlow
import ua.govstack.digitalwallet.app.pin.R
import ua.govstack.digitalwallet.app.pin.helper.PinHelper
import ua.govstack.digitalwallet.app.pin.repository.LoginPinRepository
import ua.govstack.digitalwallet.app.pin.util.AndroidClientAlertDialogsFactory
import ua.govstack.digitalwallet.app.pin.util.AndroidClientAlertDialogsFactory.Companion.INVALID_PIN
import ua.govstack.digitalwallet.app.pin.util.PinConst
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.UIElementData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIAction
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIActionKeysCompose
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.findAndChangeFirstByInstance
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.navigation.NavigationPath
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.UiText
import ua.govstack.digitalwallet.app.ui_base.components.molecule.header.NavigationPanelMlcV2Data
import ua.govstack.digitalwallet.app.ui_base.components.molecule.text.TextLabelMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.text.TitleCentralizedMlcData
import ua.govstack.digitalwallet.app.ui_base.components.organism.header.TopGroupOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.tile.NumButtonTileOrganismData
import ua.govstack.digitalwallet.app.ui_base.provider.ResourceProvider
import javax.inject.Inject

@HiltViewModel
class PinInputVM @Inject constructor(
    @param:GlobalActionLogout private val actionLogout: MutableLiveData<UiEvent>,
    @param:GlobalActionRunDeepLinkAfterPin private val actionRunDeepLinkAfterPin: MutableSharedFlow<UiEvent>,
    private val loginPinRepository: LoginPinRepository,
    private val pinHelper: PinHelper,
    private val clientAlertDialogsFactory: AndroidClientAlertDialogsFactory,
    private val errorHandling: WithErrorHandlingOnFlow,
    private val retryLastAction: WithRetryLastAction,
    private val resourceProvider: ResourceProvider,
    savedStateHandle: SavedStateHandle,
    ) : ViewModel(),
    WithRetryLastAction by retryLastAction,
    WithErrorHandlingOnFlow by errorHandling,
    AlternativeAuthCallback {

    private val _validationFinished = MutableStateFlow(true)
    val validationFinished: Flow<Pair<String, Boolean>> =
        _validationFinished.transform { value ->
            UIActionKeysCompose.PAGE_LOADING_CIRCULAR to value
        }

    private var isVerification = false
    private var alternativeAuthCount = 0

    private val _navigation = MutableSharedFlow<NavigationPath>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val navigation = _navigation.asSharedFlow()

    private val _uiData = mutableStateListOf<UIElementData>()
    val uiData: SnapshotStateList<UIElementData> = _uiData

    private val _accessibilityMessage = MutableStateFlow<Int?>(null)
    val accessibilityMessage: StateFlow<Int?> = _accessibilityMessage

    init {
        isVerification = savedStateHandle.get<Boolean>(PinConst.VERIFICATION) ?: false
        _uiData.clear()
        _uiData.add(
            TopGroupOrgData(
                navigationPanelMlcV2Data = NavigationPanelMlcV2Data(
                    isClosed = true
                )
            )
        )
        _uiData.add(
            TitleCentralizedMlcData(
                label = UiText.StringResource(R.string.pin_input_title_text),
                componentId = UiText.StringResource(R.string.enter_pin_screen_title_test_tag),
            )
        )
        _uiData.add(
            NumButtonTileOrganismData(
                componentId = UiText.StringResource(R.string.enter_pin_screen_btn_num_test_tag),
                componentIdEllipse = UiText.StringResource(R.string.enter_pin_screen_ellips_test_tag),
                pinLength = 6
            )
        )
        _uiData.add(TextLabelMlcData(text = UiText.StringResource(R.string.pin_input_description_text)))
        checkForAlternativeAuth()
        resetAlternativeAuthCount()
    }

    fun doInit(verification: Boolean) {
        isVerification = verification
    }

    @VisibleForTesting
    fun checkForAlternativeAuth() {
        executeActionOnFlow(contentLoadedIndicator = _validationFinished) {
            if (pinHelper.isAlternativeAuthEnabled()) {
                enabledAlternativeAuthBtn(true)
                alternativeAuth()
            }
        }
    }

    private fun approvePin(pin: String) {
        executeActionOnFlow(contentLoadedIndicator = _validationFinished) {
            if (loginPinRepository.isPinValid(pin)) {
                completeVerification(pin)
            } else {
                _accessibilityMessage.value = R.string.accessibility_wrong_pin_try_again
                validateTryCount {
                    clearPinWithShake()
                }
            }
        }
    }

    fun clearAccessibilityMessage() {
        _accessibilityMessage.value = null
    }

    private fun showResetPinRationale() {
        val dialogData = TemplateDialogModel(
            key = FRAGMENT_USER_ACTION_RESULT_KEY,
            type = ALERT_TYPE_HORIZONTAL_BUTTONS,
            isClosable = false,
            data = TemplateDialogData(
                icon = null,
                title = resourceProvider.getString(R.string.forgot_pin_dialog_title),
                description = resourceProvider.getString(R.string.forgot_pin_dialog_desc),
                mainButton = TemplateDialogButton(
                    name = resourceProvider.getString(R.string.forgot_pin_dialog_cancel_btn),
                    action = DIALOG_ACTION_CANCEL
                ),
                alternativeButton = TemplateDialogButton(
                    name = resourceProvider.getString(R.string.forgot_pin_dialog_accept_btn),
                    action = DIALOG_ACTION_CODE_LOGOUT
                )
            )
        )
        showTemplateDialog(dialogData)
    }

    fun resetPin() {
        actionLogout.value = UiEvent()
    }

    private fun alternativeAuth() {
        _navigation.tryEmit(Navigation.AlternativeAuth)
    }

    override fun onAlternativeAuthSuccessful() {
        executeActionOnFlow(contentLoadedIndicator = _validationFinished) {
            completeVerification()
        }
    }

    override fun onAlternativeAuthFailed() {
        executeActionOnFlow(contentLoadedIndicator = _validationFinished) {
            validateAlternativeAuthTryCount()
        }
    }

    fun onUIAction(uiAction: UIAction) {
        when (uiAction.actionKey) {
            UIActionKeysCompose.TEXT_LABEL_MLC -> {
                showResetPinRationale()
            }

            UIActionKeysCompose.PIN_CLEARED_NUM_BUTTON_ORGANISM -> {
                _uiData.findAndChangeFirstByInstance<NumButtonTileOrganismData> {
                    it.copy(clearPin = false, clearWithShake = false)
                }
            }

            UIActionKeysCompose.TOUCH_ID_BUTTON -> {
                alternativeAuth()
            }

            UIActionKeysCompose.PIN_CREATED_NUM_BUTTON_ORGANISM -> {
                approvePin(uiAction.data ?: return)
            }
        }
    }

    private fun enabledAlternativeAuthBtn(enable: Boolean) {
        _uiData.findAndChangeFirstByInstance<NumButtonTileOrganismData> {
            it.copy(hasBiometric = enable)
        }
    }

    private fun clearPinWithShake() {
        _uiData.findAndChangeFirstByInstance<NumButtonTileOrganismData> {
            it.copy(clearWithShake = true)
        }
    }

    private suspend fun completeVerification(pin: String? = null) {
        loginPinRepository.setPinTryCount(0)
        resetAlternativeAuthCount()
        if (isVerification) {
            _navigation.tryEmit(Navigation.PinApproved)
        } else {
            pin?.let {
                loginPinRepository.setUserAuthorized(it)
            }
           _navigation.tryEmit(Navigation.ToHome)
            actionRunDeepLinkAfterPin.tryEmit(UiEvent())
        }
    }

    private suspend inline fun validateTryCount(doIdHasAttempt: () -> Unit) {
        val incrementedPinTryCount = loginPinRepository.getPinTryCount().inc()
        loginPinRepository.setPinTryCount(incrementedPinTryCount)
        if (incrementedPinTryCount >= PIN_TRY_COUNT) {
            showTemplateDialog(clientAlertDialogsFactory.showCustomAlert(INVALID_PIN))
        } else {
            doIdHasAttempt.invoke()
        }
    }

    private fun validateAlternativeAuthTryCount() {
        alternativeAuthCount += 1
        if (alternativeAuthCount >= ALT_AUTH_TRY_COUNT) {
            enabledAlternativeAuthBtn(false)
        }
    }

    private fun resetAlternativeAuthCount() {
        alternativeAuthCount = 0
    }

    private companion object {
        const val PIN_TRY_COUNT = 3
        const val ALT_AUTH_TRY_COUNT = 5
        const val ALERT_TYPE_HORIZONTAL_BUTTONS = "horizontalButtons"
    }

    sealed class Navigation : NavigationPath {
        object PinApproved : Navigation()
        object ToHome : Navigation()
        object AlternativeAuth : Navigation()
    }
}