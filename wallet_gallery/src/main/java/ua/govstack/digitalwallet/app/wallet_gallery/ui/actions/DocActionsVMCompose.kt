package ua.govstack.digitalwallet.app.wallet_gallery.ui.actions

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ua.govstack.digitalwallet.app.core.models.document.Document
import ua.govstack.digitalwallet.app.core.models.document.ManualDocs
import ua.govstack.digitalwallet.app.core.models.document.ui.DocumentsContextMenuActions
import ua.govstack.digitalwallet.app.core.util.event.UiEvent
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.DocAction
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIAction
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIActionKeysCompose
import ua.govstack.digitalwallet.app.ui_base.components.organism.list.ActivityViewOrgData
import javax.inject.Inject

@HiltViewModel
class DocActionsVMCompose @Inject constructor(
    private val docActionMapper: DocActionMapper,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _docAction = MutableSharedFlow<DocAction>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val docAction = _docAction.asSharedFlow()

    private val _dismiss = MutableSharedFlow<UiEvent>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val dismiss = _dismiss.asSharedFlow()

    val document = savedStateHandle["doc"] as? Document
    val manualDocs = savedStateHandle.get<ManualDocs?>("manualDocs")

    private val _body = MutableStateFlow<ActivityViewOrgData?>(null)
    val body = _body.asStateFlow()

    init {
        viewModelScope.launch {
            document?.let { getData(it, manualDocs) }
        }
    }

    fun onUIAction(event: UIAction) {
        when (event.action?.type ?: event.actionKey) {

            DocumentsContextMenuActions.REMOVE_DOC.action -> {
                _docAction.tryEmit(DocActions.RemoveDoc)
            }

            DocumentsContextMenuActions.UPDATE_DOC.action -> {
                event.data?.let {
                    _docAction.tryEmit(DocActions.UpdateDoc(event.data!!))
                }
            }


            UIActionKeysCompose.BUTTON_REGULAR -> {
                _dismiss.tryEmit(UiEvent())
            }

            else -> {
                event.action?.type?.let {
                    _docAction.tryEmit(DocActions.NavigateByDocAction(action = it))
                }
            }
        }
    }


    suspend fun getData(document: Document, manualDocs: ManualDocs?) {
        _body.emit(
            docActionMapper.body(document, manualDocs)
        )
    }

    sealed class DocActions : DocAction {
        object RemoveDoc : DocActions()
        data class UpdateDoc(val type: String) : DocActions()
        data class OpenVerificationCode(val id: String) : DocActions()
        data class NavigateByDocAction(val action: String, val data: String? = null) : DocActions()
    }
}