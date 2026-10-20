/*
 * Copyright (c) 2026 GovStack/Diia contributors
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

package eu.europa.ec.eudi.wallet.transactionLogging.issuance

import eu.europa.ec.eudi.wallet.transactionLogging.TransactionLog
import java.time.Instant

data class IssuanceTransactionLog(
    val timestamp: Instant,
    val status: TransactionLog.Status,
    val issuingParty: TransactionLog.IssuingParty,
    val documentId: String?,
    val docType: String?,
    val displayName: String?,
    val documents: List<TransactionLog.IssuedDocument>,
    val dataFormat: TransactionLog.DataFormat?,
    val errorMessage: String?,
) {
    companion object {
        fun fromTransactionLog(transactionLog: TransactionLog): Result<IssuanceTransactionLog> {
            return runCatching {
                require(transactionLog.type == TransactionLog.Type.Issuance) {
                    "Transaction log is not an issuance transaction log"
                }

                IssuanceTransactionLog(
                    timestamp = Instant.ofEpochMilli(transactionLog.timestamp),
                    status = transactionLog.status,
                    issuingParty = transactionLog.issuingParty
                        ?: TransactionLog.IssuingParty("Unknown Issuer", ""),
                    documentId = transactionLog.documentId,
                    docType = transactionLog.docType,
                    displayName = transactionLog.displayName,
                    documents = transactionLog.documents
                        ?: listOfNotNull(
                            transactionLog.documentId?.let {
                                TransactionLog.IssuedDocument(
                                    documentId = it,
                                    docType = transactionLog.docType,
                                    displayName = transactionLog.displayName,
                                    dataFormat = transactionLog.dataFormat,
                                )
                            }
                        ),
                    dataFormat = transactionLog.dataFormat,
                    errorMessage = transactionLog.errorMessage,
                )
            }
        }
    }
}
