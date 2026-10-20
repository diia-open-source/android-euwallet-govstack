package ua.govstack.digitalwallet.app.opensource.util.delegation

import androidx.lifecycle.MutableLiveData
import ua.govstack.digitalwallet.app.core.models.dialogs.TemplateDialogModel
import ua.govstack.digitalwallet.app.core.util.alert.ClientAlertDialogsFactory
import ua.govstack.digitalwallet.app.core.util.delegation.WithErrorHandling
import ua.govstack.digitalwallet.app.core.util.event.UiDataEvent
import ua.govstack.digitalwallet.app.core.util.extensions.lifecycle.asLiveData
import ua.govstack.digitalwallet.app.core.util.extensions.noInternetException
import ua.govstack.digitalwallet.app.opensource.util.alert.AndroidClientAlertDialogsFactory.Companion.NO_INTERNET
import ua.govstack.digitalwallet.app.opensource.util.alert.AndroidClientAlertDialogsFactory.Companion.UNKNOWN_ERR
import ua.govstack.digitalwallet.app.ui_base.fragments.errordialog.RequestTryCountTracker
import javax.inject.Inject

class DefaultErrorHandlingBehaviour @Inject constructor(
    private val alertFactory: ClientAlertDialogsFactory,
) : WithErrorHandling {

    private val tryCountTracker = RequestTryCountTracker()

    private val _showTemplateDialog = MutableLiveData<UiDataEvent<TemplateDialogModel>>()
    override val showTemplateDialog = _showTemplateDialog.asLiveData()

    override fun consumeException(
        exception: Exception,
        key: String,
        needRetry: Boolean
    ) {
        val templateData = if (exception.noInternetException()) {
            alertFactory.showCustomAlert(NO_INTERNET)
        } else {
            val closable = (tryCountTracker.tryCount < 1 && needRetry)
            alertFactory.showCustomAlert(UNKNOWN_ERR, closable)
        }
        tryCountTracker.increment()
        _showTemplateDialog.postValue(UiDataEvent(templateData.setKey(key)))
    }

    override fun resetErrorCounter() {
        tryCountTracker.reset()
    }

    override fun showTemplateDialog(
        templateDialog: TemplateDialogModel,
        key: String
    ) {
        _showTemplateDialog.postValue(UiDataEvent(templateDialog.setKey(key)))
    }

}