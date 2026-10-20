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

package eu.europa.ec.eudi.wallet.transactionLogging.deleting

import eu.europa.ec.eudi.wallet.document.Document
import eu.europa.ec.eudi.wallet.document.DocumentId
import eu.europa.ec.eudi.wallet.document.DocumentManager
import eu.europa.ec.eudi.wallet.document.Outcome
import eu.europa.ec.eudi.wallet.document.ProofOfDeletion
import eu.europa.ec.eudi.wallet.document.format.MsoMdocFormat
import eu.europa.ec.eudi.wallet.document.format.SdJwtVcFormat
import eu.europa.ec.eudi.wallet.logging.Logger
import eu.europa.ec.eudi.wallet.transactionLogging.TransactionLog
import eu.europa.ec.eudi.wallet.transactionLogging.TransactionLogger
import java.time.Instant

internal class TransactionLoggingDocumentManager(
    private val delegate: DocumentManager,
    private val transactionLogger: TransactionLogger,
    private val logger: Logger? = null,
) : DocumentManager by delegate {

    override fun deleteDocumentById(documentId: DocumentId): Outcome<ProofOfDeletion?> {
        val document = delegate.getDocumentById(documentId)

        return runCatching { delegate.deleteDocumentById(documentId) }
            .onSuccess { outcome ->
                document?.let { documentToDelete ->
                    val transactionLog = runCatching { outcome.getOrThrow() }
                        .fold(
                            onSuccess = {
                                createDeleteLog(
                                    document = documentToDelete,
                                    status = TransactionLog.Status.Completed,
                                )
                            },
                            onFailure = { error ->
                                createDeleteLog(
                                    document = documentToDelete,
                                    status = TransactionLog.Status.Error,
                                    errorMessage = error.message,
                                )
                            }
                        )
                    logTransactionSafely(transactionLog)
                }
            }
            .onFailure { error ->
                document?.let {
                    logTransactionSafely(
                        createDeleteLog(
                            document = it,
                            status = TransactionLog.Status.Error,
                            errorMessage = error.message,
                        )
                    )
                }
                logger?.log(
                    Logger.Record(
                        level = Logger.LEVEL_ERROR,
                        message = "Failed to delete document",
                        thrown = error,
                        sourceClassName = TransactionLoggingDocumentManager::class.java.name,
                        sourceMethod = "deleteDocumentById"
                    )
                )
            }
            .getOrThrow()
    }

    private fun createDeleteLog(
        document: Document,
        status: TransactionLog.Status,
        errorMessage: String? = null,
    ): TransactionLog {
        return TransactionLog(
            timestamp = Instant.now().toEpochMilli(),
            status = status,
            type = TransactionLog.Type.Deleting,
            relyingParty = null,
            rawRequest = null,
            rawResponse = null,
            dataFormat = when (document.format) {
                is MsoMdocFormat -> TransactionLog.DataFormat.Cbor
                is SdJwtVcFormat -> TransactionLog.DataFormat.Json
                else -> null
            },
            sessionTranscript = null,
            metadata = null,
            errorMessage = errorMessage,
            documentId = document.id,
            docType = when (val format = document.format) {
                is MsoMdocFormat -> format.docType
                is SdJwtVcFormat -> format.vct
                else -> null
            },
            displayName = document.name,
            issuingParty = null,
        )
    }

    private fun logTransactionSafely(transactionLog: TransactionLog) {
        runCatching {
            transactionLogger.log(transactionLog)
        }.onFailure { error ->
            logger?.log(
                Logger.Record(
                    level = Logger.LEVEL_ERROR,
                    message = "Failed to log deletion transaction",
                    thrown = error,
                    sourceClassName = TransactionLoggingDocumentManager::class.java.name,
                    sourceMethod = "logTransactionSafely"
                )
            )
        }
    }
}
