package ua.govstack.digitalwallet.app.wallet_issuing.ui.preAuthPin

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.text.input.KeyboardType
import ua.govstack.digitalwallet.app.ui_base.components.atom.button.BtnPrimaryDefaultAtmData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.DataActionWrapper
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.UIElementData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIActionKeysCompose
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.state.UIState
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.SidePaddingMode
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.TopPaddingMode
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.UiText
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.toDynamicString
import ua.govstack.digitalwallet.app.ui_base.components.molecule.header.NavigationPanelMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.input.InputTextMlcV2Data
import ua.govstack.digitalwallet.app.ui_base.components.molecule.text.TitleCentralizedMlcData
import ua.govstack.digitalwallet.app.ui_base.components.organism.block.TextBlockOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.bottom.BottomGroupOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.container.InputBlockOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.header.TopGroupOrgData
import ua.govstack.digitalwallet.app.ui_base.provider.ResourceProvider
import ua.govstack.digitalwallet.app.wallet_core.ui.conts.WalletConst.ACCEPT_OFFER_PIN
import ua.govstack.digitalwallet.app.wallet_issuing.R
import javax.inject.Inject

class PreAuthPinMapper @Inject constructor(
    private val resourceProvider: ResourceProvider,
) {
    fun toolbar(): UIElementData {
        return TopGroupOrgData(
            navigationPanelMlcData = NavigationPanelMlcData(
                isContextMenuExist = false
            )
        )
    }

    fun bottom(): UIElementData {
        return BottomGroupOrgData(
            primaryButton = BtnPrimaryDefaultAtmData(
                actionKey = ACCEPT_OFFER_PIN_BTN,
                componentId = ACCEPT_OFFER_PIN_BTN.toDynamicString(),
                title = UiText.StringResource(R.string.pre_auth_pin_done),
                id = ACCEPT_OFFER_PIN_BTN,
                action = DataActionWrapper(type = ACCEPT_OFFER_PIN),
                interactionState = UIState.Interaction.Disabled
            ),
        )
    }

    fun body(keyboardType: KeyboardType): List<UIElementData> {
        val centralizedMlc = TitleCentralizedMlcData(
            label = UiText.StringResource(R.string.pre_auth_pin_transaction_code_title)
        )
        val textBlockOrg = TextBlockOrgData(
            text = null,
            title = UiText.StringResource(R.string.pre_auth_pin_transaction_code_description),
        )
        val inputCode = InputBlockOrgData(
            actionKey = UIActionKeysCompose.TEXT_INPUT_V2,
            items = SnapshotStateList<UIElementData>().apply {
                addAll(generateInputAtmNumbers(keyboardType))
            }
        )
        return listOf(centralizedMlc, textBlockOrg, inputCode)
    }

    private fun generateInputAtmNumbers(keyboardType: KeyboardType): List<InputTextMlcV2Data> {
        return mutableStateListOf(
            InputTextMlcV2Data(
                actionKey = UIActionKeysCompose.TEXT_INPUT_V2,
                id = CODE_INPUT_ID,
                mandatory = true,
                label = resourceProvider.getString(R.string.pre_auth_pin_code_label),
                placeholder = resourceProvider.getString(R.string.pre_auth_pin_code_placeholder),
                paddingHorizontal = SidePaddingMode.NONE,
                keyboardType = keyboardType,
                paddingTop = TopPaddingMode.NONE,
            )
        )
    }

    companion object {
        const val ACCEPT_OFFER_PIN_BTN = "ACCEPT_OFFER_PIN_BTN"
        const val CODE_INPUT_ID = "code_input"
    }
}
