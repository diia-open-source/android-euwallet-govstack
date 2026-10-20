package ua.govstack.digitalwallet.app.wallet_core.ui.alertDialogsFactory

import ua.govstack.digitalwallet.app.core.models.dialogs.TemplateDialogModel
import ua.govstack.digitalwallet.app.core.ui.dynamicdialog.ActionsConst

interface WalletAlertDialogFactory {

    fun docIssued(key: String = ActionsConst.FRAGMENT_USER_ACTION_RESULT_KEY): TemplateDialogModel

    fun deferredDocRequest(key: String = ActionsConst.FRAGMENT_USER_ACTION_RESULT_KEY): TemplateDialogModel

    fun docFailToReceive(key: String = ActionsConst.FRAGMENT_USER_ACTION_RESULT_KEY): TemplateDialogModel

    fun failToShareNoDocFound(key: String = ActionsConst.FRAGMENT_USER_ACTION_RESULT_KEY): TemplateDialogModel

    fun failToHandleSharingRequest(key: String = ActionsConst.FRAGMENT_USER_ACTION_RESULT_KEY): TemplateDialogModel

    fun dataSharedSuccessfully(key: String = ActionsConst.FRAGMENT_USER_ACTION_RESULT_KEY): TemplateDialogModel

    fun serviceDisconnected(key: String = ActionsConst.FRAGMENT_USER_ACTION_RESULT_KEY): TemplateDialogModel

    fun errorOccurred(key: String = ActionsConst.FRAGMENT_USER_ACTION_RESULT_KEY): TemplateDialogModel

    fun somethingGoesWrong(key: String = ActionsConst.FRAGMENT_USER_ACTION_RESULT_KEY): TemplateDialogModel

    fun documentAlreadyExist(key: String = ActionsConst.FRAGMENT_USER_ACTION_RESULT_KEY): TemplateDialogModel

    fun documentsDeleted(key: String = ActionsConst.FRAGMENT_USER_ACTION_RESULT_KEY): TemplateDialogModel

    fun confirmDeleteDocument(key: String = ActionsConst.FRAGMENT_USER_ACTION_RESULT_KEY): TemplateDialogModel

    fun bluetoothLost(key: String = ActionsConst.FRAGMENT_USER_ACTION_RESULT_KEY): TemplateDialogModel

    fun pidNotFound(key: String = ActionsConst.FRAGMENT_USER_ACTION_RESULT_KEY): TemplateDialogModel

    fun untrustedIssuer(key: String = ActionsConst.FRAGMENT_USER_ACTION_RESULT_KEY): TemplateDialogModel

    fun untrustedVerifier(key: String = ActionsConst.FRAGMENT_USER_ACTION_RESULT_KEY): TemplateDialogModel

    fun deferredDocIssued(key: String = ActionsConst.FRAGMENT_USER_ACTION_RESULT_KEY): TemplateDialogModel

    fun deferredDocFailed(message: String? = null, key: String = ActionsConst.FRAGMENT_USER_ACTION_RESULT_KEY): TemplateDialogModel

    fun deferredDocExpired(key: String = ActionsConst.FRAGMENT_USER_ACTION_RESULT_KEY): TemplateDialogModel

}
