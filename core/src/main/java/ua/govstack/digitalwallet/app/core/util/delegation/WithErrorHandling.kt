package ua.govstack.digitalwallet.app.core.util.delegation

import androidx.lifecycle.LiveData
import ua.govstack.digitalwallet.app.core.models.dialogs.TemplateDialogModel
import ua.govstack.digitalwallet.app.core.ui.dynamicdialog.ActionsConst
import ua.govstack.digitalwallet.app.core.util.event.UiDataEvent

interface WithTemplateDialog {

    val showTemplateDialog: LiveData<UiDataEvent<TemplateDialogModel>>

    fun showTemplateDialog(
        templateDialog: TemplateDialogModel,
        key: String = ActionsConst.FRAGMENT_USER_ACTION_RESULT_KEY
    )
}

interface WithErrorHandling : WithTemplateDialog {

    fun consumeException(
        exception: Exception,
        key: String = ActionsConst.FRAGMENT_USER_ACTION_RESULT_KEY,
        needRetry: Boolean = true
    )

    fun resetErrorCounter()
}
