package ua.govstack.digitalwallet.app.wallet_transaction.domain.interactor

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import ua.govstack.digitalwallet.app.wallet_transaction.data.repository.TransactionsRepository
import ua.govstack.digitalwallet.app.wallet_transaction.domain.model.TransactionLogDataDomain

sealed class GetTransactionsResult {
    data class Success(val transactions: List<TransactionLogDataDomain>) : GetTransactionsResult()
    data class Failure(val error: String) : GetTransactionsResult()
    data object Empty : GetTransactionsResult()
}

interface TransactionsInteractor {
    suspend fun observeTransactions(): Flow<GetTransactionsResult>
}

internal class TransactionsInteractorImpl(
    private val transactionsRepository: TransactionsRepository
) : TransactionsInteractor {

    override suspend fun observeTransactions(): Flow<GetTransactionsResult> =
        transactionsRepository.observeTransactionLogs().map { transactions ->
            if (transactions.isEmpty()) {
                GetTransactionsResult.Empty
            } else {
                GetTransactionsResult.Success(transactions)
            }
        }.catch { e ->
            emit(GetTransactionsResult.Failure(e.message.orEmpty()))
        }

}
