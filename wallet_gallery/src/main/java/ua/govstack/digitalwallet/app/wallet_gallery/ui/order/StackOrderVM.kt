package ua.govstack.digitalwallet.app.wallet_gallery.ui.order

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import ua.govstack.digitalwallet.app.core.models.document.Document
import ua.govstack.digitalwallet.app.core.models.document.DocumentWithMetadata
import ua.govstack.digitalwallet.app.core.network.Http
import ua.govstack.digitalwallet.app.core.ui.dynamicdialog.ActionsConst.ACTION_NAVIGATE_BACK
import ua.govstack.digitalwallet.app.core.util.delegation.WithErrorHandlingOnFlow
import ua.govstack.digitalwallet.app.core.util.delegation.WithRetryLastAction
import ua.govstack.digitalwallet.app.core.util.extensions.vm.executeActionOnFlow
import ua.govstack.digitalwallet.app.ui_base.R
import ua.govstack.digitalwallet.app.ui_base.components.DiiaResourceIcon
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.DataActionWrapper
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.UIElementData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.addIfNotNull
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIAction
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIActionKeysCompose
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.findAndChangeFirstByInstance
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.navigation.NavigationPath
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.UiText
import ua.govstack.digitalwallet.app.ui_base.components.molecule.header.TitleGroupMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.list.ListItemDragMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.message.PaginationMessageMlcData
import ua.govstack.digitalwallet.app.ui_base.components.organism.header.TopGroupOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.list.ListItemDragOrgData
import ua.govstack.digitalwallet.app.ui_base.mappers.document.DocumentComposeMapper
import ua.govstack.digitalwallet.app.ui_base.navigation.BaseNavigation
import ua.govstack.digitalwallet.app.wallet_core.documents.model.DocOrder
import ua.govstack.digitalwallet.app.wallet_gallery.data.repository.DocumentsRepository
import javax.inject.Inject

@HiltViewModel
class StackOrderVM @Inject constructor(
    private val documentsRepository: DocumentsRepository,
    private val errorHandling: WithErrorHandlingOnFlow,
    private val withRetryLastAction: WithRetryLastAction,
    private val composeMapper: DocumentComposeMapper,
) : ViewModel(),
    WithErrorHandlingOnFlow by errorHandling,
    WithRetryLastAction by withRetryLastAction,
    DocumentComposeMapper by composeMapper {

    private val _bodyData = mutableStateListOf<UIElementData>()
    val bodyData: SnapshotStateList<UIElementData> = _bodyData

    private val _topBarData = mutableStateListOf<UIElementData>()
    val topBarData: SnapshotStateList<UIElementData> = _topBarData

    private val _navigation = MutableSharedFlow<NavigationPath>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val navigation = _navigation.asSharedFlow()

    init {
        buildInitialScreenContent()
    }

    fun onUIAction(event: UIAction) {
        when (event.actionKey) {
            UIActionKeysCompose.TOOLBAR_NAVIGATION_BACK -> {
                _navigation.tryEmit(BaseNavigation.Back)
            }

            UIActionKeysCompose.TITLE_GROUP_MLC -> {
                event.action?.type?.let {
                    if (it == ACTION_NAVIGATE_BACK) {
                        _navigation.tryEmit(BaseNavigation.Back)
                    }
                }
            }
        }
    }

    private fun buildInitialScreenContent() {
        executeActionOnFlow {
            documentsRepository.invalidate()
            documentsRepository.documentsData.collectLatest { documents ->
                val docs = documents
                    .sortedBy { it.getDocOrder() }
                    .map { it.copy(document = it.document?.makeCopy()) }
                showScreenContent(docs)
            }
        }
    }

    private fun showScreenContent(documents: List<DocumentWithMetadata>) {
        clearContent()
        showToolbar()
        showBody(documents)
    }

    private fun showToolbar() {
        _topBarData.addIfNotNull(getNavigationData())
    }

    private fun showBody(documents: List<DocumentWithMetadata>) {
        documents.ifEmpty {
            _bodyData.add(
                PaginationMessageMlcData(
                    id = "",
                    title = UiText.StringResource(ua.govstack.digitalwallet.app.wallet_gallery.R.string.no_documents_yet),
                    description = UiText.StringResource(ua.govstack.digitalwallet.app.wallet_gallery.R.string.add_documents_to_your_wallet_to_change_their_order)
                )
            )
        }
        _bodyData.add(
            ListItemDragOrgData(
                documents.toTypeStateList(),
                componentId = UiText.StringResource(R.string.stack_order_docs_list_test_tag)
            )
        )
    }

    fun clearContent() {
        _topBarData.clear()
        _bodyData.clear()
    }

    private fun getDocOrder(documents: List<ListItemDragMlcData>): List<DocOrder> {
        return documents.mapIndexed { index, documentCard ->
            DocOrder(documentCard.id, index.inc())
        }
    }

    private fun List<DocumentWithMetadata>?.toTypeStateList(): SnapshotStateList<ListItemDragMlcData> {
        val list = SnapshotStateList<ListItemDragMlcData>()
        if (this == null) return list
        forEach {
            if (it.document?.getStatus() == Http.HTTP_204) return@forEach
            it.toTypedDragMlcData()?.let { d -> list.add(d) }
        }
        return list
    }

    private fun DocumentWithMetadata.toTypedDragMlcData(): ListItemDragMlcData? {
        if (document == null) return null
        val doc = document as Document
        val id = doc.docId()
        val label = doc.getDocOrderLabel()
        val description = doc.getDocOrderDescription() ?: doc.getDisplayDate()
        return if (!label.isNullOrEmpty()) {
            ListItemDragMlcData(
                id = id,
                label = UiText.DynamicString(label),
                desc = UiText.DynamicString(description)
            )
        } else {
            null
        }
    }

    fun onMove(a: Int, b: Int) {
        _bodyData.findAndChangeFirstByInstance<ListItemDragOrgData> {
            val list = it.items.toMutableList()
            list.add(a, list.removeAt(b))

            val state = SnapshotStateList<ListItemDragMlcData>().apply { addAll(list) }
            ListItemDragOrgData(state)
        }
    }

    fun saveCurrentOrder() {
        val element = (_bodyData.find { it is ListItemDragOrgData } as? ListItemDragOrgData)
        val currentOrder = getDocOrder(element?.items.orEmpty())
        documentsRepository.saveDocTypeOrder(currentOrder)
    }

    private fun getNavigationData(): UIElementData {
        return TopGroupOrgData(
            titleGroupMlcData = TitleGroupMlcData(
                heroText = UiText.StringResource(R.string.stack_order_title),
                leftNavIcon = TitleGroupMlcData.LeftNavIcon(
                    code = DiiaResourceIcon.BACK.code,
                    accessibilityDescription = UiText.StringResource(R.string.accessibility_back_button),
                    action = DataActionWrapper(
                        type = ACTION_NAVIGATE_BACK,
                        subtype = null,
                        resource = null
                    )
                ),
                componentId = UiText.StringResource(R.string.stack_order_title_test_tag)
            )
        )
    }
}
