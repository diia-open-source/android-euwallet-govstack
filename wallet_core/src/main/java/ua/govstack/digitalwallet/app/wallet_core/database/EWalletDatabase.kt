package ua.govstack.digitalwallet.app.wallet_core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ua.govstack.digitalwallet.app.wallet_core.database.dao.RevokedDocumentDao
import ua.govstack.digitalwallet.app.wallet_core.database.dao.TransactionLogDao
import ua.govstack.digitalwallet.app.wallet_core.database.model.RevokedDocument
import ua.govstack.digitalwallet.app.wallet_core.database.model.TransactionLog

@Database(
    entities = [
        RevokedDocument::class,
        TransactionLog::class
    ],
    version = 1
)
abstract class EWalletDatabase : RoomDatabase() {
    abstract fun revokedDocumentDao(): RevokedDocumentDao

    abstract fun transactionLogDao(): TransactionLogDao
}