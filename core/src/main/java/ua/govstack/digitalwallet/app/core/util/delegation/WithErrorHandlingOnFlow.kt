package ua.govstack.digitalwallet.app.core.util.delegation

import kotlinx.coroutines.flow.SharedFlow
import ua.govstack.digitalwallet.app.core.models.dialogs.TemplateDialogModel
import ua.govstack.digitalwallet.app.core.ui.dynamicdialog.ActionsConst
import ua.govstack.digitalwallet.app.core.util.event.UiDataEvent

interface WithTemplateDialogOnFlow {

    val showTemplateDialog: SharedFlow<UiDataEvent<TemplateDialogModel>>

    fun showTemplateDialog(
        templateDialog: TemplateDialogModel,
        key: String = ActionsConst.FRAGMENT_USER_ACTION_RESULT_KEY
    )
}

interface WithErrorHandlingOnFlow : WithTemplateDialogOnFlow {

    fun consumeException(
        exception: Exception,
        key: String = ActionsConst.FRAGMENT_USER_ACTION_RESULT_KEY,
        needRetry: Boolean = true
    )

    fun resetErrorCounter()
}
