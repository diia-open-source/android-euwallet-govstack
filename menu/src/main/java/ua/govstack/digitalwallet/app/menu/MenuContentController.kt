package ua.govstack.digitalwallet.app.menu

import androidx.compose.runtime.mutableStateListOf
import ua.govstack.digitalwallet.app.menu.ui.MenuActionsKey
import ua.govstack.digitalwallet.app.ui_base.components.DiiaResourceIcon
import ua.govstack.digitalwallet.app.ui_base.components.atom.button.BtnPrimaryDefaultAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.space.SpacerAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.space.SpacerAtmType
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.DataActionWrapper
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.UIElementData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.addAllIfNotNull
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.UiIcon
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.UiText
import ua.govstack.digitalwallet.app.ui_base.components.molecule.list.ListItemMlcData
import ua.govstack.digitalwallet.app.ui_base.components.organism.list.ListItemGroupOrgData
import javax.inject.Inject

class MenuContentController @Inject constructor() {

    fun configureBody(): List<UIElementData> {

        val qrCodeScan: List<ListItemMlcData> = listOf(
            ListItemMlcData(
                id = MenuActionsKey.OPEN_QR_CODE_SCAN,
                label = UiText.StringResource(R.string.scan_qr),
                iconLeft = UiIcon.DrawableResource(DiiaResourceIcon.QR_SCAN.code),
                action = DataActionWrapper(
                    type = MenuActionsKey.OPEN_QR_CODE_SCAN
                ),
                componentId = UiText.StringResource(R.string.menu_item_qr_code_scan_test_tag)
            )
        )

        val menuTransactionLog: List<ListItemMlcData> = listOf(
            ListItemMlcData(
                id = MenuActionsKey.OPEN_TRANSACTION_LOG,
                label = UiText.StringResource(R.string.transaction_log),
                iconLeft = UiIcon.DrawableResource(DiiaResourceIcon.TRANSACTION_LOG.code),
                action = DataActionWrapper(
                    type = MenuActionsKey.OPEN_TRANSACTION_LOG
                ),
                componentId = UiText.StringResource(R.string.menu_group_transaction_log_test_tag)
            )
        )

        val menuSettings: List<ListItemMlcData> = listOf(
            ListItemMlcData(
                id = MenuActionsKey.OPEN_SETTINGS,
                label = UiText.StringResource(R.string.menu_settings),
                iconLeft = UiIcon.DrawableResource(DiiaResourceIcon.SETTINGS.code),
                action = DataActionWrapper(
                    type = MenuActionsKey.OPEN_SETTINGS
                ),
                componentId = UiText.StringResource(R.string.menu_item_settings_test_tag)
            )
        )

        val bodyData = mutableStateListOf<UIElementData>()
        bodyData.addAllIfNotNull(
            ListItemGroupOrgData(itemsList = menuTransactionLog, componentId = UiText.StringResource(R.string.menu_group_transaction_log_test_tag)),
            ListItemGroupOrgData(itemsList = qrCodeScan, componentId = UiText.StringResource(R.string.menu_group_support_test_tag)),
            ListItemGroupOrgData(itemsList = menuSettings, componentId = UiText.StringResource(R.string.menu_group_settings_and_support_test_tag)),
            SpacerAtmData(SpacerAtmType.MEDIUM),
            BtnPrimaryDefaultAtmData(
                id = MenuActionsKey.LOGOUT,
                actionKey = MenuActionsKey.LOGOUT,
                componentId = UiText.StringResource(R.string.menu_btn_primary_exit_test_tag),
                title = UiText.StringResource(R.string.log_out)
            ),
            SpacerAtmData(SpacerAtmType.EXTRA_LARGE)
        )
        return bodyData
    }

}