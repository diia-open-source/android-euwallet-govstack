package ua.govstack.digitalwallet.app.wallet_transaction.ui.details

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ua.govstack.digitalwallet.app.core.util.delegation.WithErrorHandlingOnFlow
import ua.govstack.digitalwallet.app.core.util.delegation.WithRetryLastAction
import ua.govstack.digitalwallet.app.core.util.extensions.mutableSharedFlowOf
import ua.govstack.digitalwallet.app.core.util.state.CircularWithBackNav
import ua.govstack.digitalwallet.app.core.util.state.Loader
import ua.govstack.digitalwallet.app.core.util.state.setFullScreen
import ua.govstack.digitalwallet.app.core.util.state.start
import ua.govstack.digitalwallet.app.core.util.state.stop
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.UIElementData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.addAllFromListIfNotNull
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIAction
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIActionKeysCompose
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.navigation.NavigationPath
import ua.govstack.digitalwallet.app.ui_base.navigation.BaseNavigation
import ua.govstack.digitalwallet.app.wallet_core.ui.alertDialogsFactory.WalletAlertDialogFactory
import ua.govstack.digitalwallet.app.wallet_transaction.domain.interactor.GetTransactionDetailsResult
import ua.govstack.digitalwallet.app.wallet_transaction.domain.interactor.TransactionDetailsInteractor
import ua.govstack.digitalwallet.app.wallet_transaction.domain.model.TransactionLogDataDomain
import javax.inject.Inject

@HiltViewModel
class TransactionDetailsVM @Inject constructor(
    private val errorHandling: WithErrorHandlingOnFlow,
    private val retryActionDelegate: WithRetryLastAction,
    private val walletAlertDialogsFactory: WalletAlertDialogFactory,
    private val mapper: TransactionDetailsMapper,
    private val transactionsDetailsInteractor: TransactionDetailsInteractor,
    savedStateHandle: SavedStateHandle,
) : ViewModel(),
    WithErrorHandlingOnFlow by errorHandling,
    WithRetryLastAction by retryActionDelegate {

    private val transactionId = checkNotNull(savedStateHandle.get<String>("transactionId"))

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

    init {
        buildInitialScreenContent(transactionId)
    }

    private fun buildInitialScreenContent(transactionId: String) {
        clearContent()
        _loader.setFullScreen(CircularWithBackNav)
        _loader.start()
        viewModelScope.launch {
            transactionsDetailsInteractor.getTransactionDetails(transactionId).collect { response ->
                _loader.stop()
                when (response) {
                    is GetTransactionDetailsResult.Success -> {
                        showScreenContent(response.transactionLogDataDomain)
                    }

                    is GetTransactionDetailsResult.Empty -> {
                        showTemplateDialog(
                            walletAlertDialogsFactory.somethingGoesWrong()
                        )
                    }

                    is GetTransactionDetailsResult.Failure -> {
                        showTemplateDialog(
                            walletAlertDialogsFactory.failToHandleSharingRequest()
                        )
                    }
                }
            }
        }
    }

    fun onUIAction(event: UIAction) {
        when (event.actionKey) {
            UIActionKeysCompose.TOOLBAR_NAVIGATION_BACK -> {
                _navigation.tryEmit(BaseNavigation.Back)
            }
        }
    }

    fun showScreenContent(transactions: TransactionLogDataDomain) {
        clearContent()
        showToolbar()
        showBody(transactions)
    }

    fun clearContent() {
        _toolbarData.clear()
        _bodyData.clear()
        _bottomData.clear()
    }

    private fun showToolbar() {
        _toolbarData.addAllFromListIfNotNull(listOf(mapper.toolbar()))
    }

    private fun showBody(transaction: TransactionLogDataDomain) {
        val bodyContent = mapper.mapBodyContent(transaction)

        _bodyData.clear()
        _bodyData.addAllFromListIfNotNull(bodyContent)
    }

}
