package ua.govstack.digitalwallet.app.pin.ui.reset

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.transform
import ua.govstack.digitalwallet.app.core.di.actions.GlobalActionLogout
import ua.govstack.digitalwallet.app.core.ui.dynamicdialog.ActionsConst
import ua.govstack.digitalwallet.app.core.util.delegation.WithErrorHandlingOnFlow
import ua.govstack.digitalwallet.app.core.util.delegation.WithRetryLastAction
import ua.govstack.digitalwallet.app.core.util.event.UiEvent
import ua.govstack.digitalwallet.app.core.util.extensions.vm.executeActionOnFlow
import ua.govstack.digitalwallet.app.pin.R
import ua.govstack.digitalwallet.app.pin.repository.LoginPinRepository
import ua.govstack.digitalwallet.app.pin.util.AndroidClientAlertDialogsFactory
import ua.govstack.digitalwallet.app.pin.util.AndroidClientAlertDialogsFactory.Companion.INVALID_PIN
import ua.govstack.digitalwallet.app.ui_base.components.DiiaResourceIcon
import ua.govstack.digitalwallet.app.ui_base.components.atom.icon.MiddleIconAtmData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.UIElementData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIAction
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIActionKeysCompose
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.findAndChangeFirstByInstance
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.navigation.NavigationPath
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.TopPaddingMode
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.UiText
import ua.govstack.digitalwallet.app.ui_base.components.molecule.header.NavigationPanelMlcV2Data
import ua.govstack.digitalwallet.app.ui_base.components.molecule.text.SubTitleCentralizedMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.text.TitleCentralizedMlcData
import ua.govstack.digitalwallet.app.ui_base.components.organism.header.TopGroupOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.tile.NumButtonTileOrganismData
import ua.govstack.digitalwallet.app.ui_base.navigation.BaseNavigation
import javax.inject.Inject

@HiltViewModel
class ResetPinVM @Inject constructor(
    @param:GlobalActionLogout private val actionLogout: MutableLiveData<UiEvent>,
    private val loginPinRepository: LoginPinRepository,
    private val clientAlertDialogsFactory: AndroidClientAlertDialogsFactory,
    private val errorHandling: WithErrorHandlingOnFlow,
    private val retryLastAction: WithRetryLastAction
) : ViewModel(),
    WithErrorHandlingOnFlow by errorHandling,
    WithRetryLastAction by retryLastAction {

    private val _validationFinished = MutableStateFlow(true)
    val validationFinished: Flow<Pair<String, Boolean>> =
        _validationFinished.transform { value ->
            UIActionKeysCompose.PAGE_LOADING_CIRCULAR to value
        }

    private val _navigation = MutableSharedFlow<NavigationPath>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val navigation = _navigation.asSharedFlow()

    private val _uiData = mutableStateListOf<UIElementData>()
    val uiData: SnapshotStateList<UIElementData> = _uiData

    init {
        _uiData.add(
            TopGroupOrgData(
                navigationPanelMlcV2Data = NavigationPanelMlcV2Data(
                    iconLeft = MiddleIconAtmData(
                        code = DiiaResourceIcon.BACK.code
                    ),
                    isClosed = true
                )
            )
        )
        _uiData.add(
            TitleCentralizedMlcData(
                label = UiText.StringResource(R.string.pin_reset_title_text),
                componentId = UiText.StringResource(R.string.confirm_pin_screen_title_test_tag),
            )
        )
        _uiData.add(
            SubTitleCentralizedMlcData(
                label = UiText.StringResource(R.string.pin_reset_description_text),
                componentId = UiText.StringResource(R.string.confirm_pin_screen_title_test_tag),
                paddingTop = TopPaddingMode.LARGE
            )
        )
        _uiData.add(
            NumButtonTileOrganismData(
                componentId = UiText.StringResource(R.string.confirm_pin_screen_btn_num_test_tag),
                componentIdEllipse = UiText.StringResource(R.string.confirm_pin_screen_ellips_test_tag),
                pinLength = 6
            )
        )
    }

    private fun approvePin(pin: String) {
        executeActionOnFlow(contentLoadedIndicator = _validationFinished) {
            if (loginPinRepository.isPinValid(pin)) {
                loginPinRepository.setPinTryCount(0)
                _navigation.tryEmit(Navigation.CreateNewPin)
            } else {
                val incrementedPinTryCount = loginPinRepository.getPinTryCount().inc()
                if (incrementedPinTryCount >= PIN_TRY_COUNT) {
                    showTemplateDialog(clientAlertDialogsFactory.showCustomAlert(INVALID_PIN))
                    loginPinRepository.setPinTryCount(incrementedPinTryCount)
                } else {
                    clearPinWithShake()
                    loginPinRepository.setPinTryCount(incrementedPinTryCount)
                }
            }
        }
    }

    fun resetPin() {
        actionLogout.value = UiEvent()
    }

    fun onUIAction(uiAction: UIAction) {
        when (uiAction.actionKey) {
            UIActionKeysCompose.PIN_CLEARED_NUM_BUTTON_ORGANISM -> {
                _uiData.findAndChangeFirstByInstance<NumButtonTileOrganismData> {
                    it.copy(clearWithShake = false)
                }
            }

            UIActionKeysCompose.PIN_CREATED_NUM_BUTTON_ORGANISM -> {
                approvePin(uiAction.data ?: return)
            }

            UIActionKeysCompose.TOOLBAR_NAVIGATION_BACK -> {
                _navigation.tryEmit(BaseNavigation.Back)
            }

            UIActionKeysCompose.TITLE_GROUP_MLC -> {
                uiAction.action?.type.let {
                    if (it == ActionsConst.ACTION_NAVIGATE_BACK) {
                        _navigation.tryEmit(BaseNavigation.Back)
                    }
                }
            }
        }
    }

    private fun clearPinWithShake() {
        _uiData.findAndChangeFirstByInstance<NumButtonTileOrganismData> {
            it.copy(clearWithShake = true)
        }
    }

    sealed class Navigation : NavigationPath {
        object CreateNewPin : Navigation()
    }

    private companion object {
        const val PIN_TRY_COUNT = 3
    }
}