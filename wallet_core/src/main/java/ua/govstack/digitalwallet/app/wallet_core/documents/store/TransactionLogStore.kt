package ua.govstack.digitalwallet.app.wallet_core.documents.store

import eu.europa.ec.eudi.wallet.transactionLogging.TransactionLog
import eu.europa.ec.eudi.wallet.transactionLogging.TransactionLogUpserter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import ua.govstack.digitalwallet.app.core.util.UuidProvider
import ua.govstack.digitalwallet.app.wallet_core.database.dao.TransactionLogDao
import kotlin.uuid.ExperimentalUuidApi
import ua.govstack.digitalwallet.app.wallet_core.database.model.TransactionLog as TransactionStorage

interface TransactionLogStore : TransactionLogUpserter

internal class TransactionLogStoreImpl(
    private val transactionLogDao: TransactionLogDao,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
    private val uuidProvider: UuidProvider
) : TransactionLogStore {

    @OptIn(ExperimentalUuidApi::class)
    override fun upsert(transaction: TransactionLog) {
        scope.launch {
            val json = Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            }
            val identifier = transaction.transactionId ?: uuidProvider.provideUuid()
            val value = TransactionStorage(
                identifier = identifier,
                value = json.encodeToString(transaction),
                timestamp = transaction.timestamp,
            )
            val existing = transaction.transactionId?.let { transactionLogDao.retrieve(identifier) }

            if (existing == null) {
                transactionLogDao.store(value)
            } else {
                transactionLogDao.update(
                    existing.copy(
                        value = value.value,
                        timestamp = value.timestamp,
                    )
                )
            }
        }
    }
}
