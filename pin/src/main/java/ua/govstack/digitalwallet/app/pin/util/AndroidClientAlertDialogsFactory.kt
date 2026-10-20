package ua.govstack.digitalwallet.app.pin.util

import ua.govstack.digitalwallet.app.core.models.dialogs.TemplateDialogButton
import ua.govstack.digitalwallet.app.core.models.dialogs.TemplateDialogData
import ua.govstack.digitalwallet.app.core.models.dialogs.TemplateDialogModel
import ua.govstack.digitalwallet.app.core.ui.dynamicdialog.ActionsConst
import ua.govstack.digitalwallet.app.core.util.alert.ClientAlertDialogsFactory
import ua.govstack.digitalwallet.app.core.util.throwExceptionInDebugOrShowAlert
import javax.inject.Inject

class AndroidClientAlertDialogsFactory @Inject constructor(
) : ClientAlertDialogsFactory {

    override fun showCustomAlert(keyAlert: String, isClosable: Boolean): TemplateDialogModel {
        return when (keyAlert) {

            INVALID_PIN -> TemplateDialogModel(
                key = ActionsConst.FRAGMENT_USER_ACTION_RESULT_KEY,
                type = "horizontalButton",
                isClosable = false,
                data = TemplateDialogData(
                    icon = null,
                    title = "You entered the wrong code three times.",
                    description = "Re-authorize in the application",
                    mainButton = TemplateDialogButton(
                        name = "Log out",
                        action = ActionsConst.DIALOG_ACTION_CODE_LOGOUT
                    )
                )
            )

            CONFIRM_PIN -> TemplateDialogModel(
                key = ActionsConst.FRAGMENT_USER_ACTION_RESULT_KEY,
                type = "middleCenterIconBlackButtonAlert",
                isClosable = false,
                data = TemplateDialogData(
                    icon = "ellipseCheck",
                    title = "Code changed",
                    description = "You have changed your app login code",
                    mainButton = TemplateDialogButton(
                        name = "Done",
                        action = ActionsConst.DIALOG_DEAL_WITH_IT
                    )
                )
            )

            else -> "Unhandled keyAlert".throwExceptionInDebugOrShowAlert(isClosable)
        }
    }

    companion object {
        const val UNKNOWN_ERR = "unknownErrorAlert"
        const val INVALID_PIN = "showAlertAfterInvalidPin"
        const val CONFIRM_PIN = "showAlertAfterConfirmPin"
    }
}