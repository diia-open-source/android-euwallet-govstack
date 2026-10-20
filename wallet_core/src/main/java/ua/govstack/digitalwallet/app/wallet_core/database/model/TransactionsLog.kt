package ua.govstack.digitalwallet.app.wallet_core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactionLogs")
data class TransactionLog(
    @PrimaryKey
    val identifier: String,
    val value: String,
    val timestamp: Long = System.currentTimeMillis()
)