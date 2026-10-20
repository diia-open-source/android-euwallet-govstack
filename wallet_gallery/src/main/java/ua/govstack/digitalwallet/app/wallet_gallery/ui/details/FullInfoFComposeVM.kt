package ua.govstack.digitalwallet.app.wallet_gallery.ui.details

import android.os.Parcelable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import ua.govstack.digitalwallet.app.core.models.document.Document
import ua.govstack.digitalwallet.app.core.util.delegation.WithErrorHandlingOnFlow
import ua.govstack.digitalwallet.app.core.util.delegation.WithRetryLastAction
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.DataActionWrapper
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.UIElementData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.DocAction
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIAction
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIActionKeysCompose
import ua.govstack.digitalwallet.app.ui_base.mappers.document.DocumentComposeMapper
import ua.govstack.digitalwallet.app.wallet_gallery.ui.details.mapper.DocFullInfoComposeMapper
import javax.inject.Inject

@HiltViewModel
class FullInfoFComposeVM @Inject constructor(
    private val errorHandling: WithErrorHandlingOnFlow,
    private val withRetryLastAction: WithRetryLastAction,
    private val composeMapper: DocumentComposeMapper,
    private val docFullComposeMapper: DocFullInfoComposeMapper
) : ViewModel(),
    WithErrorHandlingOnFlow by errorHandling,
    WithRetryLastAction by withRetryLastAction,
    DocumentComposeMapper by composeMapper {

    private val _bodyData = mutableStateListOf<UIElementData>()
    val bodyData: SnapshotStateList<UIElementData> = _bodyData

    private val _progressIndicatorKey = MutableStateFlow("")
    private val _progressIndicator = MutableStateFlow(false)
    val progressIndicator: Flow<Pair<String, Boolean>> =
        _progressIndicator.combine(_progressIndicatorKey) { value, key ->
            key to value
        }

    private val _docAction = MutableSharedFlow<DocAction>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val docAction = _docAction.asSharedFlow()


    fun configureBody(document: Parcelable) {
        _bodyData.clear()
        
        (document as? Document)?.let {
            docFullComposeMapper.mapDocToBody(it, _bodyData)
        }
    }

    fun onUIAction(event: UIAction) {
        when (event.actionKey) {
            UIActionKeysCompose.DOC_NUMBER_COPY -> {
                event.data?.let {
                    _docAction.tryEmit(DocActions.DocNumberCopy(event.data!!))
                }
            }

            UIActionKeysCompose.VERTICAL_TABLE_ITEM -> {
                event.data?.let {
                    _docAction.tryEmit(DocActions.ItemVerticalValueCopy(event.data!!))
                }
            }

            UIActionKeysCompose.HORIZONTAL_TABLE_ITEM -> {
                event.data?.let {
                    _docAction.tryEmit(DocActions.ItemHorizontalValueCopy(event.data!!))
                }
            }

            UIActionKeysCompose.PRIMARY_TABLE_ITEM -> {
                event.data?.let {
                    _docAction.tryEmit(DocActions.ItemPrimaryValueCopy(event.data!!))
                }
            }

            UIActionKeysCompose.BOTTOM_SHEET_DISMISS -> {
                _docAction.tryEmit(DocActions.DismissDoc)
            }

            UIActionKeysCompose.LIST_ITEM_MLC,
            UIActionKeysCompose.LIST_ITEM_GROUP_ORG -> {
                event.action?.let {
                    _docAction.tryEmit(DocActions.FullInfoAction(it))
                }
            }
        }
    }

    sealed class DocActions : DocAction {
        data class DocNumberCopy(val value: String) : DocActions()
        data class ItemHorizontalValueCopy(val value: String) : DocActions()
        data class ItemVerticalValueCopy(val value: String) : DocActions()
        data class ItemPrimaryValueCopy(val value: String) : DocActions()
        object HighBrightness : DocActions()
        object DefaultBrightness : DocActions()
        object DismissDoc : DocActions()
        data class FullInfoAction(val action: DataActionWrapper) : DocActions()
    }
}