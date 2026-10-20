package ua.govstack.digitalwallet.app.wallet_transaction.data.repository

import eu.europa.ec.eudi.wallet.document.IssuedDocument
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ua.govstack.digitalwallet.app.core.util.DispatcherProvider
import ua.govstack.digitalwallet.app.wallet_core.database.dao.TransactionLogDao
import ua.govstack.digitalwallet.app.wallet_core.documents.store.DocumentStore
import ua.govstack.digitalwallet.app.wallet_transaction.data.transformer.toDomain
import ua.govstack.digitalwallet.app.wallet_transaction.domain.model.TransactionLogDataDomain
import ua.govstack.digitalwallet.app.wallet_transaction.domain.transformer.withDocuments

interface TransactionsRepository {
    suspend fun getTransactionLogs(): List<TransactionLogDataDomain>
    suspend fun getTransactionLog(id: String): TransactionLogDataDomain?
    suspend fun getTransactionLogDetails(id: String): TransactionLogDataDomain?
    suspend fun observeTransactionLogs(): Flow<List<TransactionLogDataDomain>>
    suspend fun observeTransactionLog(id: String): Flow<TransactionLogDataDomain?>
}

internal class TransactionsRepositoryImpl(
    private val transactionsDao: TransactionLogDao,
    private val documentStore: DocumentStore,
    private val dispatcherProvider: DispatcherProvider
) : TransactionsRepository {

    override suspend fun getTransactionLogs(): List<TransactionLogDataDomain> =
        withContext(dispatcherProvider.ioDispatcher()) {
            transactionsDao.retrieveAll().mapNotNull { it.toDomain() }
        }

    override suspend fun getTransactionLog(id: String): TransactionLogDataDomain? =
        withContext(dispatcherProvider.ioDispatcher()) {
            transactionsDao.retrieve(id)?.toDomain()
        }

    override suspend fun getTransactionLogDetails(id: String): TransactionLogDataDomain? =
        withContext(dispatcherProvider.ioDispatcher()) {
            var transaction = getTransactionLog(id) ?: return@withContext null

            if (transaction is TransactionLogDataDomain.IssuanceLog) {
                val documents = transaction.documents
                    .map { documentStore.getDocumentById(it.id) }
                    .filterIsInstance<IssuedDocument>()

                transaction = transaction.withDocuments(documents)
            }
            transaction
        }

    override suspend fun observeTransactionLogs(): Flow<List<TransactionLogDataDomain>> =
        transactionsDao.observeAll().map { list -> list.mapNotNull { it.toDomain() } }
            .flowOn(dispatcherProvider.ioDispatcher())

    override suspend fun observeTransactionLog(id: String): Flow<TransactionLogDataDomain?> =
        transactionsDao.observe(id).map { item ->
            var transaction = item?.toDomain()
            if (transaction is TransactionLogDataDomain.IssuanceLog) {
                val documents = transaction.documents
                    .map { documentStore.getDocumentById(it.id) }
                    .filterIsInstance<IssuedDocument>()

                transaction = transaction.withDocuments(documents)
            }
            transaction
        }.flowOn(dispatcherProvider.ioDispatcher())
}
