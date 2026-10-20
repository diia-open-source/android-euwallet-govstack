package ua.govstack.digitalwallet.app.opensource.ui.fragments.dialogs.system

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import ua.govstack.digitalwallet.app.core.models.common.template_dialogs.SystemDialogData
import ua.govstack.digitalwallet.app.core.ui.dynamicdialog.ActionsConst
import ua.govstack.digitalwallet.app.ui_base.components.atom.button.ButtonSystemAtomData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIAction
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIActionKeysCompose
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.navigation.NavigationPath
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.screen.SystemDialogScreenData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.UiText

class SystemDialogVM : ViewModel() {

    private val _navigation = MutableSharedFlow<NavigationPath>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val navigation = _navigation.asSharedFlow()
    val uiData = mutableStateOf(SystemDialogScreenData())

    fun doInit(dialogData: SystemDialogData) {
        val title = dialogData.title?.let { UiText.StringResource(it) }
        val description = dialogData.message?.let { UiText.StringResource(it) }
        val positiveButton = dialogData.positiveButtonTitle?.let {
            ButtonSystemAtomData(
                title = UiText.StringResource(it),
                actionKey = UIActionKeysCompose.BUTTON_REGULAR
            )
        }
        val negativeButton = dialogData.negativeButtonTitle?.let {
            ButtonSystemAtomData(
                title = UiText.StringResource(it),
                actionKey = UIActionKeysCompose.BUTTON_ALTERNATIVE
            )
        }

        uiData.value = uiData.value.copy(
            titleText = title,
            descriptionText = description,
            positiveButton = positiveButton,
            negativeButton = negativeButton
        )
    }

    private fun onPositiveButtonClicked() {
        _navigation.tryEmit(
            Navigation.SendActionResult(ActionsConst.SYSTEM_DIALOG_POSITIVE)
        )
    }

    private fun onNegativeButtonClicked() {
        _navigation.tryEmit(
            Navigation.SendActionResult(ActionsConst.SYSTEM_DIALOG_NEGATIVE)
        )
    }

    fun onUIAction(uiAction: UIAction) {
        when (uiAction.actionKey) {
            UIActionKeysCompose.BUTTON_REGULAR -> {
                onPositiveButtonClicked()
            }

            UIActionKeysCompose.BUTTON_ALTERNATIVE -> {
                onNegativeButtonClicked()
            }
        }
    }

    sealed class Navigation : NavigationPath {
        data class SendActionResult(val result: String) : Navigation()

    }
}