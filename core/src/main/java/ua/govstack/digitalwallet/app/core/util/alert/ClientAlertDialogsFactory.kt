package ua.govstack.digitalwallet.app.core.util.alert

import ua.govstack.digitalwallet.app.core.models.dialogs.TemplateDialogModel

interface ClientAlertDialogsFactory {

    fun showCustomAlert(keyAlert: String, isClosable: Boolean = false): TemplateDialogModel

}