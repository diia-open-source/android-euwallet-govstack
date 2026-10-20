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

import eu.europa.ec.eudi.wallet.document.Document
import eu.europa.ec.eudi.wallet.document.format.DocumentFormat
import eu.europa.ec.eudi.wallet.document.format.MsoMdocFormat
import eu.europa.ec.eudi.wallet.document.format.SdJwtVcFormat
import eu.europa.ec.eudi.wallet.document.metadata.IssuerMetadata
import eu.europa.ec.eudi.wallet.issue.openid4vci.Offer
import eu.europa.ec.eudi.wallet.transactionLogging.TransactionLog
import java.time.Instant

internal object IssuanceLogFactory {

    fun createCompleted(
        document: Document,
        offeredDocument: Offer.OfferedDocument?,
        transactionId: String? = null,
        timestamp: Long = Instant.now().toEpochMilli(),
    ): TransactionLog {
        return create(
            timestamp = timestamp,
            status = TransactionLog.Status.Completed,
            offeredDocument = offeredDocument,
            transactionId = transactionId,
            documentId = document.id,
            documentFormat = document.format,
            displayName = document.name,
        )
    }

    fun createBatch(
        status: TransactionLog.Status,
        documents: List<TransactionLog.IssuedDocument>,
        offeredDocument: Offer.OfferedDocument?,
        transactionId: String,
        timestamp: Long = Instant.now().toEpochMilli(),
        errorMessage: String? = null,
    ): TransactionLog {
        val firstDocument = documents.firstOrNull()
        return create(
            timestamp = timestamp,
            status = status,
            offeredDocument = offeredDocument,
            transactionId = transactionId,
            documentId = firstDocument?.documentId,
            documentFormat = null,
            displayName = firstDocument?.displayName,
            docType = firstDocument?.docType,
            dataFormat = firstDocument?.dataFormat,
            errorMessage = errorMessage,
            documents = documents,
        )
    }

    fun createCompleted(
        document: Document,
        issuerIdentifier: String?,
        transactionId: String? = null,
        timestamp: Long = Instant.now().toEpochMilli(),
    ): TransactionLog {
        return createCompleted(
            document = document,
            issuerMetadata = document.issuerMetadata,
            issuerIdentifier = issuerIdentifier,
            transactionId = transactionId,
            timestamp = timestamp,
        )
    }

    fun createCompleted(
        document: Document,
        issuerMetadata: IssuerMetadata?,
        issuerIdentifier: String?,
        transactionId: String? = null,
        timestamp: Long = Instant.now().toEpochMilli(),
    ): TransactionLog {
        return create(
            timestamp = timestamp,
            status = TransactionLog.Status.Completed,
            offeredDocument = null,
            transactionId = transactionId,
            documentId = document.id,
            documentFormat = document.format,
            displayName = document.name,
            issuingParty = issuingParty(
                issuerMetadata = issuerMetadata,
                issuerIdentifier = issuerIdentifier,
            )
        )
    }

    fun createDeferred(
        document: Document,
        offeredDocument: Offer.OfferedDocument?,
        transactionId: String,
        timestamp: Long = Instant.now().toEpochMilli(),
    ): TransactionLog {
        return create(
            timestamp = timestamp,
            status = TransactionLog.Status.Incomplete,
            offeredDocument = offeredDocument,
            transactionId = transactionId,
            documentId = document.id,
            documentFormat = document.format,
            displayName = document.name,
        )
    }

    fun createFailed(
        documentId: String?,
        documentFormat: DocumentFormat?,
        displayName: String?,
        offeredDocument: Offer.OfferedDocument?,
        error: Throwable,
        transactionId: String? = null,
        timestamp: Long = Instant.now().toEpochMilli(),
    ): TransactionLog {
        return create(
            timestamp = timestamp,
            status = TransactionLog.Status.Error,
            offeredDocument = offeredDocument,
            transactionId = transactionId,
            documentId = documentId,
            documentFormat = documentFormat,
            displayName = displayName,
            errorMessage = error.message,
        )
    }

    fun createDeferredFailed(
        documentId: String?,
        documentFormat: DocumentFormat?,
        displayName: String?,
        issuerIdentifier: String?,
        error: Throwable,
        transactionId: String,
        timestamp: Long = Instant.now().toEpochMilli(),
    ): TransactionLog {
        return createDeferredFailed(
            documentId = documentId,
            documentFormat = documentFormat,
            displayName = displayName,
            issuerMetadata = null,
            issuerIdentifier = issuerIdentifier,
            error = error,
            transactionId = transactionId,
            timestamp = timestamp,
        )
    }

    fun createDeferredFailed(
        documentId: String?,
        documentFormat: DocumentFormat?,
        displayName: String?,
        issuerMetadata: IssuerMetadata?,
        issuerIdentifier: String?,
        error: Throwable,
        transactionId: String,
        timestamp: Long = Instant.now().toEpochMilli(),
    ): TransactionLog {
        return create(
            timestamp = timestamp,
            status = TransactionLog.Status.Error,
            offeredDocument = null,
            transactionId = transactionId,
            documentId = documentId,
            documentFormat = documentFormat,
            displayName = displayName,
            errorMessage = error.message,
            issuingParty = issuingParty(
                issuerMetadata = issuerMetadata,
                issuerIdentifier = issuerIdentifier,
            )
        )
    }

    private fun create(
        timestamp: Long,
        status: TransactionLog.Status,
        offeredDocument: Offer.OfferedDocument?,
        transactionId: String?,
        documentId: String?,
        documentFormat: DocumentFormat?,
        displayName: String?,
        docType: String? = docType(documentFormat),
        dataFormat: TransactionLog.DataFormat? = dataFormat(documentFormat),
        errorMessage: String? = null,
        documents: List<TransactionLog.IssuedDocument>? = null,
        issuingParty: TransactionLog.IssuingParty? = issuingParty(offeredDocument),
    ): TransactionLog {
        return TransactionLog(
            timestamp = timestamp,
            status = status,
            type = TransactionLog.Type.Issuance,
            relyingParty = null,
            rawRequest = null,
            rawResponse = null,
            dataFormat = dataFormat,
            sessionTranscript = null,
            metadata = null,
            transactionId = transactionId,
            errorMessage = errorMessage,
            documentId = documentId,
            docType = docType,
            displayName = displayName,
            documents = documents,
            issuingParty = issuingParty,
        )
    }

    private fun issuingParty(
        offeredDocument: Offer.OfferedDocument?,
    ): TransactionLog.IssuingParty? {
        val offer = offeredDocument?.offer ?: return null
        val issuerDisplay = offer.issuerMetadata.display.firstOrNull()
        val identifier = offer.credentialOffer.credentialIssuerIdentifier.value.toString()

        return TransactionLog.IssuingParty(
            name = issuerDisplay?.name ?: identifier,
            identifier = identifier,
            logoUrl = issuerDisplay?.logo?.uri?.toString(),
        )
    }

    private fun issuingParty(
        issuerMetadata: IssuerMetadata?,
        issuerIdentifier: String?,
    ): TransactionLog.IssuingParty {
        val identifier = issuerIdentifier
            ?.takeIf { it.isNotBlank() }
            ?: issuerMetadata?.credentialIssuerIdentifier?.takeIf { it.isNotBlank() }
            .orEmpty()
        val issuerDisplay = issuerMetadata?.issuerDisplay?.firstOrNull()

        return TransactionLog.IssuingParty(
            name = issuerDisplay?.name ?: identifier.ifBlank { "Unknown Issuer" },
            identifier = identifier,
            logoUrl = issuerDisplay?.logo?.uri?.toString(),
        )
    }

    private fun dataFormat(documentFormat: DocumentFormat?): TransactionLog.DataFormat? =
        when (documentFormat) {
            is MsoMdocFormat -> TransactionLog.DataFormat.Cbor
            is SdJwtVcFormat -> TransactionLog.DataFormat.Json
            null -> null
        }

    private fun docType(documentFormat: DocumentFormat?): String? =
        when (documentFormat) {
            is MsoMdocFormat -> documentFormat.docType
            is SdJwtVcFormat -> documentFormat.vct
            null -> null
        }
}
