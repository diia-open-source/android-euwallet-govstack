package ua.govstack.digitalwallet.app.opensource.util.alert

import ua.govstack.digitalwallet.app.core.models.dialogs.TemplateDialogButton
import ua.govstack.digitalwallet.app.core.models.dialogs.TemplateDialogData
import ua.govstack.digitalwallet.app.core.models.dialogs.TemplateDialogModel
import ua.govstack.digitalwallet.app.core.ui.dynamicdialog.ActionsConst
import ua.govstack.digitalwallet.app.core.util.alert.ClientAlertDialogsFactory
import ua.govstack.digitalwallet.app.ui_base.components.DiiaResourceIcon
import javax.inject.Inject

class AndroidClientAlertDialogsFactory @Inject constructor(
) : ClientAlertDialogsFactory {

    override fun showCustomAlert(keyAlert: String, isClosable: Boolean): TemplateDialogModel {
        return when (keyAlert) {

            NO_INTERNET -> TemplateDialogModel(
                type = "middleCenterIconBlackButtonAlert",
                key = ActionsConst.FRAGMENT_USER_ACTION_RESULT_KEY,
                isClosable = true,
                data = TemplateDialogData(
                    icon = DiiaResourceIcon.ATTENTION_BLACK_ROUND_SMALL.code,
                    title = "No internet connection",
                    description = "Check your connection and try again",
                    mainButton = TemplateDialogButton(
                        name = "Try again",
                        action = ActionsConst.GENERAL_RETRY,
                    ),
                ),
            )
            UNKNOWN_ERR -> TemplateDialogModel(
                type = "middleCenterIconBlackButtonAlert",
                key = ActionsConst.FRAGMENT_USER_ACTION_RESULT_KEY,
                isClosable = isClosable,
                data = TemplateDialogData(
                    icon = DiiaResourceIcon.ATTENTION_BLACK_ROUND_SMALL.code,
                    title = "Something went wrong",
                    mainButton = TemplateDialogButton(
                        name = if (isClosable) "Try again" else "OK",
                        action = if (isClosable) ActionsConst.GENERAL_RETRY else ActionsConst.ERROR_DIALOG_DEAL_WITH_IT,
                    ),
                ),
            )

            else -> TemplateDialogModel(
                type = "middleCenterIconBlackButtonAlert",
                key = ActionsConst.FRAGMENT_USER_ACTION_RESULT_KEY,
                isClosable = isClosable,
                data = TemplateDialogData(
                    icon = DiiaResourceIcon.ATTENTION_BLACK_ROUND_SMALL.code,
                    title = "Something went wrong",
                    mainButton = TemplateDialogButton(
                        name = if (isClosable) "Try again" else "OK",
                        action = if (isClosable) ActionsConst.GENERAL_RETRY else ActionsConst.ERROR_DIALOG_DEAL_WITH_IT,
                    ),
                ),
            )
        }
    }

    companion object {
        const val UNKNOWN_ERR = "unknownErrorAlert"
        const val NO_INTERNET = "alertNoInternet"

    }
}