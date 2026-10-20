package ua.govstack.digitalwallet.app.wallet_transaction.data.transformer

import android.util.Log
import eu.europa.ec.eudi.wallet.transactionLogging.TransactionLog
import eu.europa.ec.eudi.wallet.transactionLogging.TransactionLogDetails
import eu.europa.ec.eudi.wallet.transactionLogging.TransactionLogParser
import kotlinx.serialization.json.Json
import ua.govstack.digitalwallet.app.wallet_transaction.domain.model.TransactionLogDataDomain
import ua.govstack.digitalwallet.app.wallet_core.database.model.TransactionLog as StorageTransaction

internal fun StorageTransaction.toDomain(): TransactionLogDataDomain? {
    return this.toCoreTransactionLog()?.parseTransactionLog()?.getOrNull()?.toTransactionLogData(
        id = this.identifier
    )
}

internal fun StorageTransaction.toCoreTransactionLog(): TransactionLog? = try {
    val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }
    json.decodeFromString(value)
} catch (e: Exception) {
    Log.e("StorageTransaction.toCoreTransactionLog", e.message.toString())
    null
}

@Throws(IllegalArgumentException::class)
 internal fun TransactionLog.parseTransactionLog(): Result<TransactionLogDetails> {
    return TransactionLogParser.parse(this)
}
