package ua.govstack.digitalwallet.app.opensource.data.repository.cleaner

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import ua.govstack.digitalwallet.app.wallet_core.database.dao.TransactionLogDao
import ua.govstack.digitalwallet.app.wallet_core.documents.store.DocumentStore
import javax.inject.Inject

class DataRepositoryMemoryCleaner @Inject constructor(
    private val documentStore: DocumentStore,
    private val transactionLogDao: TransactionLogDao
) : DataRepositoryCleaner {

    override suspend fun clean() {
        coroutineScope {
            val clearEuDocumentsJob = launch {
                documentStore.clear()
            }
            launch {
                clearEuDocumentsJob.join()
                transactionLogDao.deleteAll()
            }
        }
    }
}