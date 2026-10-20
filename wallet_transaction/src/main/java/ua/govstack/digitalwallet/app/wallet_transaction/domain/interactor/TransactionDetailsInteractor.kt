package ua.govstack.digitalwallet.app.wallet_transaction.domain.interactor

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import ua.govstack.digitalwallet.app.wallet_transaction.data.repository.TransactionsRepository
import ua.govstack.digitalwallet.app.wallet_transaction.domain.model.TransactionLogDataDomain

sealed class GetTransactionDetailsResult {
    data class Success(val transactionLogDataDomain: TransactionLogDataDomain) :
        GetTransactionDetailsResult()

    data object Empty : GetTransactionDetailsResult()
    data class Failure(val error: String) : GetTransactionDetailsResult()
}

interface TransactionDetailsInteractor {
    suspend fun getTransactionDetails(transactionId: String): Flow<GetTransactionDetailsResult>
}

internal class TransactionDetailsInteractorImpl(
    private val transactionsRepository: TransactionsRepository
) : TransactionDetailsInteractor {

    override suspend fun getTransactionDetails(transactionId: String): Flow<GetTransactionDetailsResult> =
        transactionsRepository.observeTransactionLog(transactionId).map {
            it?.let { GetTransactionDetailsResult.Success(it) } ?: GetTransactionDetailsResult.Empty
        }.catch { e ->
            emit(GetTransactionDetailsResult.Failure(e.message.orEmpty()))
        }
}
