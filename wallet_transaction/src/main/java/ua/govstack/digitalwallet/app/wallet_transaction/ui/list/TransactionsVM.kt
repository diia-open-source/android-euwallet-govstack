package ua.govstack.digitalwallet.app.wallet_transaction.ui.list

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
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
import ua.govstack.digitalwallet.app.wallet_transaction.domain.interactor.GetTransactionsResult
import ua.govstack.digitalwallet.app.wallet_transaction.domain.interactor.TransactionsInteractor
import ua.govstack.digitalwallet.app.wallet_transaction.domain.model.TransactionLogDataDomain
import ua.govstack.digitalwallet.app.wallet_transaction.util.TransactionsConst
import javax.inject.Inject

@HiltViewModel
class TransactionsVM @Inject constructor(
    private val errorHandling: WithErrorHandlingOnFlow,
    private val retryActionDelegate: WithRetryLastAction,
    private val walletAlertDialogsFactory: WalletAlertDialogFactory,
    private val mapper: TransactionsMapper,
    private val transactionsInteractor: TransactionsInteractor
) : ViewModel(),
    WithErrorHandlingOnFlow by errorHandling,
    WithRetryLastAction by retryActionDelegate {

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
        buildInitialScreenContent()
    }

    private fun buildInitialScreenContent() {
        clearContent()
        _loader.setFullScreen(CircularWithBackNav)
        _loader.start()
        viewModelScope.launch {
            transactionsInteractor.observeTransactions().collect { response ->
                _loader.stop()
                when (response) {
                    is GetTransactionsResult.Success -> {
                        showScreenContent(response.transactions)
                    }

                    is GetTransactionsResult.Empty -> {
                        showEmptyScreen()
                    }

                    is GetTransactionsResult.Failure -> {
                        showTemplateDialog(
                            walletAlertDialogsFactory.somethingGoesWrong()
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

        val action = event.action
        when (action?.type) {
            TransactionsConst.TO_TRANSACTION_DETAILS -> {
                _navigation.tryEmit(TransactionsNavigation.ToDetailsScreen(action.resource))
            }
        }
    }

    fun showScreenContent(transactions: List<TransactionLogDataDomain>) {
        clearContent()
        showToolbar()
        showBody(transactions)
    }

    fun showEmptyScreen() {
        clearContent()
        showToolbar()
        _bodyData.addAllFromListIfNotNull(listOf(mapper.emptyBodyItem()))
    }

    private fun showToolbar() {
        _toolbarData.addAllFromListIfNotNull(listOf(mapper.toolbar()))
    }


    private fun showBody(transactions: List<TransactionLogDataDomain>) {
        _bodyData.addAllFromListIfNotNull(
            transactions.map { transaction ->
                mapper.transactionCard(transaction)
            }
        )
    }

    fun clearContent() {
        _toolbarData.clear()
        _bodyData.clear()
        _bottomData.clear()
    }


    sealed class TransactionsNavigation : NavigationPath {
        data class ToDetailsScreen(val transactionId: String?) : TransactionsNavigation()
    }

}
