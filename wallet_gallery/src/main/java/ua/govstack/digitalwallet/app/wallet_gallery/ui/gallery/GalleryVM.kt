package ua.govstack.digitalwallet.app.wallet_gallery.ui.gallery

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ua.govstack.digitalwallet.app.core.di.actions.GlobalActionNetworkState
import ua.govstack.digitalwallet.app.core.models.common.DeferredIssuingEvent
import ua.govstack.digitalwallet.app.core.models.document.Document
import ua.govstack.digitalwallet.app.core.models.document.DocumentCard
import ua.govstack.digitalwallet.app.core.models.document.ManualDocs
import ua.govstack.digitalwallet.app.core.network.connectivity.ConnectivityObserver
import ua.govstack.digitalwallet.app.core.util.DispatcherProvider
import ua.govstack.digitalwallet.app.core.util.delegation.WithErrorHandlingOnFlow
import ua.govstack.digitalwallet.app.core.util.delegation.WithRetryLastAction
import ua.govstack.digitalwallet.app.core.util.event.UiDataEvent
import ua.govstack.digitalwallet.app.core.util.extensions.vm.executeActionOnFlow
import ua.govstack.digitalwallet.app.core.util.state.Circular
import ua.govstack.digitalwallet.app.core.util.state.Loader
import ua.govstack.digitalwallet.app.core.util.state.setFullScreen
import ua.govstack.digitalwallet.app.core.util.state.stop
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.UIElementData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.DocAction
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIAction
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIActionKeysCompose
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.findAndChangeFirstByInstance
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.navigation.NavigationPath
import ua.govstack.digitalwallet.app.ui_base.components.organism.pager.DocCarouselOrgData
import ua.govstack.digitalwallet.app.ui_base.navigation.BaseNavigation
import ua.govstack.digitalwallet.app.wallet_core.ui.alertDialogsFactory.WalletAlertDialogFactory
import ua.govstack.digitalwallet.app.wallet_core.ui.conts.WalletConst
import ua.govstack.digitalwallet.app.wallet_gallery.domain.interactor.DeleteDocumentResult
import ua.govstack.digitalwallet.app.wallet_gallery.domain.interactor.GetDocumentResult
import ua.govstack.digitalwallet.app.wallet_gallery.domain.interactor.GetDocumentsInteractor
import ua.govstack.digitalwallet.app.wallet_gallery.ui.actions.DocumentActions
import javax.inject.Inject

@HiltViewModel
class GalleryVM @Inject constructor(
    @param:GlobalActionNetworkState private val connectivityObserver: ConnectivityObserver,
    private val dispatcherProvider: DispatcherProvider,
    private val errorHandling: WithErrorHandlingOnFlow,
    private val withRetryLastAction: WithRetryLastAction,
    private val walletAlertDialogsFactory: WalletAlertDialogFactory,
    private val getDocumentsInteractor: GetDocumentsInteractor,
    private val galleryMapper: GalleryMapper,
) : ViewModel(), WithErrorHandlingOnFlow by errorHandling,
    WithRetryLastAction by withRetryLastAction {

    private val _bodyData = mutableStateListOf<UIElementData>()
    val bodyData: SnapshotStateList<UIElementData> = _bodyData

    private val _navigation = MutableSharedFlow<NavigationPath>(
        extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val navigation = _navigation.asSharedFlow()

    private val _docAction = MutableSharedFlow<DocAction>(
        extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private val _loader = MutableStateFlow(Loader.create())
    val loader: Flow<Loader> = _loader

    val deferredIssuingEvents: Flow<UiDataEvent<DeferredIssuingEvent>> =
        getDocumentsInteractor.deferredIssuingEvents

    val connectivity = connectivityObserver.observe()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), connectivityObserver.isAvailable)

    private val _sharingAvailable = MutableStateFlow(false)
    val sharingAvailable = _sharingAvailable.asStateFlow()

    private var currPos: Int = 0
    private var documents: List<DocumentCard>? = null
    private var docToDelete: Document? = null


    init {
        subscribeToDocumentDataSourceUpdates()
        getDocumentsInteractor.setupRevocationWork()
    }

    private fun subscribeToDocumentDataSourceUpdates() {
        viewModelScope.launch(dispatcherProvider.work) {
            getDocumentsInteractor.documents.collect {
                documents = (it as? GetDocumentResult.Success)?.documents.orEmpty()
                _bodyData.clear()
                _bodyData.addAll(galleryMapper.mapToBody(it) { currPos })
                if (it is GetDocumentResult.Empty) {
                    _sharingAvailable.emit(false)
                } else {
                    _sharingAvailable.emit(true)
                }
            }

        }
    }

    fun handleDeferredIssuingEvent(event: UiDataEvent<DeferredIssuingEvent>) {
        val deferredEvent = event.getContentIfNotHandled() ?: return
        when (deferredEvent.status) {
            DEFERRED_ISSUING_STATUS_ISSUED -> showTemplateDialog(
                walletAlertDialogsFactory.deferredDocIssued()
            )

            DEFERRED_ISSUING_STATUS_FAILED -> showTemplateDialog(
                walletAlertDialogsFactory.deferredDocFailed()
            )

            DEFERRED_ISSUING_STATUS_EXPIRED -> showTemplateDialog(
                walletAlertDialogsFactory.deferredDocExpired()
            )
        }
    }


    fun displayModeContent() {
        executeActionOnFlow(
            dispatcher = dispatcherProvider.work, loader = _loader.setFullScreen(Circular)
        ) {
            getDocumentsInteractor.fetchDocument()
        }
    }

    fun onUIAction(event: UIAction) {
        when (event.actionKey) {
            UIActionKeysCompose.DOC_CARD_SWIPE_FINISHED -> {
                _bodyData.findAndChangeFirstByInstance<DocCarouselOrgData> {
                    it.flipAllCardsToFrontSideIfNeeded()
                }
                _docAction.tryEmit(DocActions.DefaultBrightness)
            }

            UIActionKeysCompose.TOOLBAR_NAVIGATION_BACK -> {
                _navigation.tryEmit(BaseNavigation.Back)
            }

            WalletConst.TO_ADD_DOCUMENT_ACTION -> {
                _navigation.tryEmit(Navigation.ToCredentialList)
            }

            UIActionKeysCompose.DOC_ELLIPSE_MENU -> {
                val data = event.data
                val optionalId = event.optionalId
                if (data != null && optionalId != null) {
                    val doc = findDoc(data)
                    if (doc != null) {
                        _navigation.tryEmit(
                            Navigation.ToDocActions(
                                doc, optionalId.toIntOrNull() ?: 0, null
                            )
                        )
                    }
                }
            }

            UIActionKeysCompose.CHANGE_DOC_ORDER -> {
                _navigation.tryEmit(Navigation.ToDocStackOrder)
            }

            UIActionKeysCompose.ADD_DOC_ORG -> {
                _navigation.tryEmit(Navigation.ToCredentialList)
            }

            UIActionKeysCompose.DOC_PAGE_SELECTED -> {
                val currentIndex = event.data?.toIntOrNull()
                documentSelected(currentIndex)
            }

            DocumentActions.DOC_COVER_ACTION_DELETE -> {
                val doc = event.data?.let { findDoc(it) }
                if (doc != null) {
                    initRemoveDoc(doc)
                }
            }
        }
    }

    private var selectDocumentJob: Job? = null

    private fun documentSelected(index: Int?) {
        index ?: return
        selectDocumentJob?.cancel()
        selectDocumentJob = viewModelScope.launch {
            delay(100) // fix blink qr code sheet

            val docs = documents.orEmpty()
            val newValue = !(docs.isEmpty() || docs.lastIndex == index)

            _sharingAvailable.emit(newValue)
        }
    }

    private fun findDoc(docId: String): Document? {
        val doc = documents?.find {
            it.doc.document?.id == docId
        }
        return doc?.doc?.document
    }

    /**
     * Show template to get approve
     */
    fun initRemoveDoc(document: Document) {
        docToDelete = document
        showTemplateDialog(walletAlertDialogsFactory.confirmDeleteDocument())
    }

    fun approveRemoveDoc() {
        viewModelScope.launch(dispatcherProvider.work) {
            docToDelete?.let {
                getDocumentsInteractor.deleteDocument(it.docId()).collect { result ->
                    when (result) {
                        is DeleteDocumentResult.DocumentsDeleted -> {
                            showTemplateDialog(walletAlertDialogsFactory.documentsDeleted())
                        }

                        is DeleteDocumentResult.Failure -> {
                            showTemplateDialog(walletAlertDialogsFactory.somethingGoesWrong())
                        }
                    }

                }
            }
        }
    }

    fun showConfirmDeleteTemplateRemote() {
        //no-op
    }

    fun stopLoading() {
        _loader.stop()
    }

    sealed class Navigation : NavigationPath {
        data class ToOnboarding(
            val docType: String
        ) : Navigation()

        data class ToDocActions(
            val doc: Document, val position: Int, val manualDocs: ManualDocs?
        ) : Navigation()

        object ToCredentialList : Navigation()

        object ToDocStackOrder : Navigation()
    }

    sealed class DocActions : DocAction {
        object DefaultBrightness : DocActions()
    }

    private companion object {
        const val DEFERRED_ISSUING_STATUS_ISSUED = "issued"
        const val DEFERRED_ISSUING_STATUS_FAILED = "failed"
        const val DEFERRED_ISSUING_STATUS_EXPIRED = "expired"
    }
}
