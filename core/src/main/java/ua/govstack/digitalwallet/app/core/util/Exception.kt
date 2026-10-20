package ua.govstack.digitalwallet.app.core.util

import ua.govstack.digitalwallet.app.core.models.dialogs.TemplateDialogButton
import ua.govstack.digitalwallet.app.core.models.dialogs.TemplateDialogData
import ua.govstack.digitalwallet.app.core.models.dialogs.TemplateDialogModel
import ua.govstack.digitalwallet.app.core.ui.dynamicdialog.ActionsConst

fun throwExceptionInDebug(message: String) {
    if (isDebugMode()) {
        throw Exception(message)
    }
}

fun String.throwExceptionInDebugOrShowAlert(isClosable: Boolean): TemplateDialogModel {
    if (isDebugMode()) {
        throw Exception(this)
    }

    return TemplateDialogModel(
        type = "smallAlert",
        key = ActionsConst.FRAGMENT_USER_ACTION_RESULT_KEY,
        isClosable = isClosable,
        data = TemplateDialogData(
            icon = "attentionBlackRoundSmall",
            title = "Something went wrong",
            description = if (isClosable) null else "Please try again later.",
            mainButton = TemplateDialogButton(
                name = if (isClosable) "Retry" else "Got it",
                action = if (isClosable) ActionsConst.GENERAL_RETRY else ActionsConst.ERROR_DIALOG_DEAL_WITH_IT,
            ),
        )
    )
}