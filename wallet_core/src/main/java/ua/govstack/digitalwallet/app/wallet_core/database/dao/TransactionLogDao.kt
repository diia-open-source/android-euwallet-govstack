package ua.govstack.digitalwallet.app.wallet_core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ua.govstack.digitalwallet.app.wallet_core.database.model.TransactionLog

@Dao
interface TransactionLogDao : StorageDao<TransactionLog> {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    override suspend fun store(value: TransactionLog)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    override suspend fun storeAll(values: List<TransactionLog>)

    @Query("SELECT * FROM transactionLogs WHERE identifier = :identifier")
    override suspend fun retrieve(identifier: String): TransactionLog?

    @Query("SELECT * FROM transactionLogs ORDER BY timestamp DESC")
    override suspend fun retrieveAll(): List<TransactionLog>

    @Query("SELECT * FROM transactionLogs ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<TransactionLog>>

    @Query("SELECT * FROM transactionLogs WHERE identifier = :identifier")
    fun observe(identifier: String): Flow<TransactionLog?>

    @Update
    override suspend fun update(value: TransactionLog)

    @Query("DELETE FROM transactionLogs WHERE identifier = :identifier")
    override suspend fun delete(identifier: String)

    @Query("DELETE FROM transactionLogs")
    override suspend fun deleteAll()
}