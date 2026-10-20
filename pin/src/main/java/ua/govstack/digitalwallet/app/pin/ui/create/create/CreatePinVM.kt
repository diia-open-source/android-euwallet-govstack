package ua.govstack.digitalwallet.app.pin.ui.create.create

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import ua.govstack.digitalwallet.app.core.ui.dynamicdialog.ActionsConst
import ua.govstack.digitalwallet.app.core.util.delegation.WithErrorHandlingOnFlow
import ua.govstack.digitalwallet.app.pin.R
import ua.govstack.digitalwallet.app.pin.model.CreatePinFlowType
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
class CreatePinVM @Inject constructor(
    private val errorHandling: WithErrorHandlingOnFlow
) : ViewModel(), WithErrorHandlingOnFlow by errorHandling {

    private val _uiData = mutableStateListOf<UIElementData>()
    val uiData: SnapshotStateList<UIElementData> = _uiData

    private val _navigation = MutableSharedFlow<NavigationPath>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val navigation = _navigation.asSharedFlow()

    fun doInit(flowType: CreatePinFlowType) {
        setAuthFlowScreenContent(flowType)
    }

    private fun setAuthFlowScreenContent(flowType: CreatePinFlowType) {
        var title = R.string.create_screen_title_text
        var descText = R.string.create_screen_description_text
        val codeSize = 6
        when (flowType) {
            CreatePinFlowType.RESET_PIN -> {
                title = R.string.pin_reset_create_title_text
                descText = R.string.pin_reset_create_description_text
            }

            else -> {
            }
        }
        _uiData.clear()
        _uiData.add(
            TopGroupOrgData(
                navigationPanelMlcV2Data = NavigationPanelMlcV2Data(
                    iconLeft = if (flowType == CreatePinFlowType.RESET_PIN) MiddleIconAtmData(
                        code = DiiaResourceIcon.BACK.code
                    ) else null,
                    isClosed = true
                )
            )
        )
        _uiData.add(
            TitleCentralizedMlcData(label = UiText.StringResource(title))
        )
        _uiData.add(
            SubTitleCentralizedMlcData(label = UiText.StringResource(descText), paddingTop = TopPaddingMode.LARGE)
        )

        _uiData.add(
            NumButtonTileOrganismData(
                pinLength = codeSize,
                componentId = UiText.StringResource(R.string.create_pin_screen_btn_num_test_tag),
                componentIdEllipse = UiText.StringResource(R.string.create_pin_screen_ellips_test_tag)
            )
        )

    }

    fun onUIAction(uiAction: UIAction) {
        when (uiAction.actionKey) {
            UIActionKeysCompose.PIN_CREATED_NUM_BUTTON_ORGANISM -> {
                _uiData.findAndChangeFirstByInstance<NumButtonTileOrganismData> {
                    it.copy(clearPin = true)
                }
                _navigation.tryEmit(Navigation.ToPinConformation(uiAction.data ?: return))
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

    sealed class Navigation : NavigationPath {
        data class ToPinConformation(val pin: String) : Navigation()
    }
}