package ua.govstack.digitalwallet.app.wallet_issuing.ui.preAuthPin

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.text.input.KeyboardType
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import ua.govstack.digitalwallet.app.core.controller.DeeplinkProcessor
import ua.govstack.digitalwallet.app.core.ui.dynamicdialog.ActionsConst
import ua.govstack.digitalwallet.app.core.util.delegation.WithDeeplinkHandling
import ua.govstack.digitalwallet.app.core.util.delegation.WithErrorHandlingOnFlow
import ua.govstack.digitalwallet.app.core.util.delegation.WithRetryLastAction
import ua.govstack.digitalwallet.app.core.util.event.UiDataEvent
import ua.govstack.digitalwallet.app.core.util.extensions.mutableSharedFlowOf
import ua.govstack.digitalwallet.app.core.util.extensions.vm.executeActionOnFlow
import ua.govstack.digitalwallet.app.core.util.state.CircularWithBackNav
import ua.govstack.digitalwallet.app.core.util.state.Loader
import ua.govstack.digitalwallet.app.core.util.state.setComponent
import ua.govstack.digitalwallet.app.core.util.state.setFullScreen
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.UIElementData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.addAllFromListIfNotNull
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIAction
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIActionKeysCompose
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.findAndChangeFirstByInstance
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.navigation.NavigationPath
import ua.govstack.digitalwallet.app.ui_base.components.molecule.input.InputTextMlcV2Data
import ua.govstack.digitalwallet.app.ui_base.components.organism.bottom.BottomGroupOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.container.InputBlockOrgData
import ua.govstack.digitalwallet.app.ui_base.navigation.BaseNavigation
import ua.govstack.digitalwallet.app.wallet_core.ui.alertDialogsFactory.WalletAlertDialogFactory
import ua.govstack.digitalwallet.app.wallet_core.ui.conts.WalletConst
import ua.govstack.digitalwallet.app.wallet_issuing.domain.interactor.DocumentOfferInteractor
import ua.govstack.digitalwallet.app.wallet_issuing.domain.model.result.IssueDocumentOfferResult
import ua.govstack.digitalwallet.app.wallet_issuing.util.withLoader
import javax.inject.Inject

@HiltViewModel
class PreAuthPinVM @Inject constructor(
    private val documentOfferInteractor: DocumentOfferInteractor,
    private val errorHandling: WithErrorHandlingOnFlow,
    private val retryLastAction: WithRetryLastAction,
    private val deepLinkDelegate: WithDeeplinkHandling,
    private val deeplinkProcessor: DeeplinkProcessor,
    private val walletAlertDialogsFactory: WalletAlertDialogFactory,
    private val preAuthPinMapper: PreAuthPinMapper,
    savedStateHandle: SavedStateHandle
) : ViewModel(),
    WithRetryLastAction by retryLastAction,
    WithDeeplinkHandling by deepLinkDelegate,
    WithErrorHandlingOnFlow by errorHandling {

    private val _loader = MutableStateFlow(Loader.create())
    val loader: Flow<Loader> = _loader

    private val _toolbarData = mutableStateListOf<UIElementData>()
    val toolbarData: SnapshotStateList<UIElementData> = _toolbarData

    private val _bodyData = mutableStateListOf<UIElementData>()
    val bodyData: SnapshotStateList<UIElementData> = _bodyData

    private val _bottomData = mutableStateListOf<UIElementData>()
    val bottomData: SnapshotStateList<UIElementData> = _bottomData

    private val _navigation = mutableSharedFlowOf<NavigationPath>()
    val navigation = _navigation.asSharedFlow()

    private var _processNavigation = MutableLiveData<UiDataEvent<NavDirections>>()
    val processNavigation: LiveData<UiDataEvent<NavDirections>>
        get() = _processNavigation

    private var codeType: Pair<Int?, KeyboardType>? = null
    private var code: String = ""

    val linkToProcess: String? = savedStateHandle[WalletConst.LINK_TO_PROCESS]

    init {
        processLink()
    }

    fun onUIAction(event: UIAction) {
        when (event.actionKey) {
            UIActionKeysCompose.TOOLBAR_NAVIGATION_BACK -> {
                _navigation.tryEmit(BaseNavigation.Back)
            }

            UIActionKeysCompose.TEXT_INPUT_V2 -> {
                onCodeChanged(event)
            }

            WalletConst.GO_HOME -> {
                _navigation.tryEmit(Navigation.Home)
            }
        }
        when (event.action?.type) {
            WalletConst.ACCEPT_OFFER_PIN -> {
                acceptDocumentOffer()
            }

            ActionsConst.ACTION_NAVIGATE_BACK -> {
                _navigation.tryEmit(BaseNavigation.Back)
            }

            UIActionKeysCompose.CLEAR_TEXT_INPUT_V2 -> {
                clearCode()
            }
        }
    }

    suspend fun handleDeepLinks() {
        deeplinkFlow.collectLatest {
            it?.getContentIfNotHandled()?.let { action ->
                deeplinkProcessor.handleDeepLinkAction(action).let { route ->
                    if (route != null) {
                        _processNavigation.value = UiDataEvent(route)
                    }
                }
            }
        }
    }

    sealed interface Navigation : NavigationPath {
        object Home : Navigation
    }

    private fun processLink() {
        executeActionOnFlow(
            loader = _loader.setFullScreen(CircularWithBackNav)
        ) {
            linkToProcess?.let {
                showToolbar()
                codeType = getTxCodeType(it)
                showBody()
                showBottom()
            }
        }

    }

    private fun acceptDocumentOffer() {
        if (!linkToProcess.isNullOrEmpty() && code.isNotEmpty()) {
            executeActionOnFlow {
                documentOfferInteractor.issueDocumentsOffer(
                    offerUri = linkToProcess,
                    txCode = code
                )
                    .withLoader(_loader.setComponent(PreAuthPinMapper.ACCEPT_OFFER_PIN_BTN))
                    .collect { state ->
                        when (state) {
                            is IssueDocumentOfferResult.DeferredSuccess -> {
                                showTemplateDialog(walletAlertDialogsFactory.deferredDocRequest())
                            }

                            is IssueDocumentOfferResult.Failure -> {
                                showTemplateDialog(walletAlertDialogsFactory.docFailToReceive())
                            }

                            is IssueDocumentOfferResult.Success -> {
                                showTemplateDialog(walletAlertDialogsFactory.docIssued())
                            }

                            is IssueDocumentOfferResult.AlreadyExists -> showTemplateDialog(
                                walletAlertDialogsFactory.documentAlreadyExist()
                            )
                        }
                    }
            }
        }
    }

    private fun showBottom() {
        _bottomData.add(preAuthPinMapper.bottom())
    }

    private fun showToolbar() {
        _toolbarData.add(preAuthPinMapper.toolbar())
    }

    private fun onCodeChanged(event: UIAction) {
        val id = event.optionalId
        val value = event.data
        val requiredLength = codeType?.first
        val cleanedValue = requiredLength?.let { value?.take(it) } ?: value

        _bodyData.findAndChangeFirstByInstance<InputBlockOrgData> { block ->
            val updatedBlock = block.onInputChanged(id, cleanedValue)

            code = updatedBlock.items.filterIsInstance<InputTextMlcV2Data>()
                .joinToString("") { it.inputValue.orEmpty() }

            updatedBlock
        }
        updateBottomGroupButton()
    }

    private fun clearCode() {
        _bodyData.findAndChangeFirstByInstance<InputBlockOrgData> { block ->
            block.onInputChanged(PreAuthPinMapper.CODE_INPUT_ID, "")
        }
        updateBottomGroupButton()
    }

    private fun updateBottomGroupButton() {
        val input = _bodyData
            .filterIsInstance<InputBlockOrgData>()
            .flatMap { it.items }
            .filterIsInstance<InputTextMlcV2Data>()
            .firstOrNull()
            ?.inputValue
            .orEmpty()

        val requiredLength = codeType?.first ?: 0

        val isFilled = input.length == requiredLength

        _bottomData.findAndChangeFirstByInstance<BottomGroupOrgData> {
            it.activateButtonsIgnoreCheckbox(isFilled)
        }
    }

    private fun showBody() {
        _bodyData.addAllFromListIfNotNull(
            preAuthPinMapper.body(
                codeType?.second ?: KeyboardType.Text
            )
        )
    }

    private fun getTxCodeType(deeplink: String): Pair<Int?, KeyboardType>? {
        return try {
            val uri = deeplink.toUri()

            val jsonString = uri.getQueryParameter("credential_offer") ?: return null

            val json = Json.parseToJsonElement(jsonString).jsonObject

            val grants = json["grants"]?.jsonObject
            val preAuthGrant =
                grants?.get("urn:ietf:params:oauth:grant-type:pre-authorized_code")?.jsonObject
            val txCode = preAuthGrant?.get("tx_code")?.jsonObject
            val codeLength = txCode?.get("length")?.jsonPrimitive?.int
            val keyboardType = when(txCode?.get("input_mode")?.jsonPrimitive?.content) {
                "numeric" -> KeyboardType.Number
                else -> KeyboardType.Text
            }
            codeLength to keyboardType
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
