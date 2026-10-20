/*
 * Copyright (c) 2025 European Commission
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Modifications Copyright (c) 2026 GovStack/Diia contributors.
 * This file has been modified from the original upstream version.
 */

package eu.europa.ec.eudi.wallet.transactionLogging

/**
 * A logger for transactions.
 *
 * Implementations of this interface should log transactions to some persistent storage.
 * The storage can be a file, a database, or any other storage medium.
 */
fun interface TransactionLogger {
    /**
     * Logs a transaction.
     * @param transaction The transaction to log.
     */
    fun log(transaction: TransactionLog)
}

/**
 * A transaction logger that can upsert an existing transaction entry.
 *
 * Implementations may use [TransactionLog.transactionId] to replace an existing
 * record instead of appending a new one. This is useful for deferred issuance
 * flows where an incomplete transaction later becomes completed or failed.
 */
interface TransactionLogUpserter : TransactionLogger {
    /**
     * Inserts a new transaction or updates the existing one if it represents
     * the same logical transaction.
     */
    fun upsert(transaction: TransactionLog)

    override fun log(transaction: TransactionLog) = upsert(transaction)
}
