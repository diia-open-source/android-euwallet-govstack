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

import eu.europa.ec.eudi.wallet.transactionLogging.TransactionLog.DataFormat.Cbor
import eu.europa.ec.eudi.wallet.transactionLogging.TransactionLog.DataFormat.Json
import eu.europa.ec.eudi.wallet.transactionLogging.TransactionLog.Status.Completed
import eu.europa.ec.eudi.wallet.transactionLogging.TransactionLog.Status.Error
import eu.europa.ec.eudi.wallet.transactionLogging.TransactionLog.Status.Incomplete
import eu.europa.ec.eudi.wallet.transactionLogging.TransactionLog.Type.Deleting
import eu.europa.ec.eudi.wallet.transactionLogging.TransactionLog.Type.Issuance
import eu.europa.ec.eudi.wallet.transactionLogging.TransactionLog.Type.Presentation
import eu.europa.ec.eudi.wallet.transactionLogging.TransactionLog.Type.Signing
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


/**
 * Represents a transaction log entry.
 *
 * @property timestamp The timestamp of the transaction.
 * @property status The status of the transaction.
 * @property type The type of the transaction.
 *
 * The following properties are optional and should be present in case of type [Type.Presentation]
 * @property relyingParty The relying party involved in the transaction.
 * @property rawRequest The raw request data.
 * @property rawResponse The raw response data.
 * @property dataFormat The format of the data (e.g., CBOR, JSON).
 * @property sessionTranscript The session transcript data.
 * @property metadata Additional metadata related to the transaction each item is a json-encoded [Metadata] object
 * @property errorMessage Optional error message for issuance or deletion failures.
 * @property transactionId Optional stable transaction identifier used to update an existing transaction log.
 * @property documentId Optional document identifier associated with issuance or deletion.
 * @property docType Optional document type associated with issuance or deletion.
 * @property displayName Optional user-facing document name associated with issuance or deletion.
 * @property documents Optional list of documents that belong to a multi-document issuance transaction.
 * @property issuingParty Optional issuer information associated with issuance logs.
 */
@Serializable
data class TransactionLog(
    val timestamp: Long,
    val status: Status,
    val type: Type,
    val relyingParty: RelyingParty?,
    val rawRequest: ByteArray?,
    val rawResponse: ByteArray?,
    val dataFormat: DataFormat?,
    val sessionTranscript: ByteArray?,
    val metadata: List<String>?,
    val transactionId: String? = null,
    val errorMessage: String? = null,
    val documentId: String? = null,
    val docType: String? = null,
    val displayName: String? = null,
    val documents: List<IssuedDocument>? = null,
    val issuingParty: IssuingParty? = null,
) {
    /**
     * Represents the status of the transaction.
     *
     * - [Incomplete] indicates that the transaction is incomplete.
     * - [Completed] indicates that the transaction is completed successfully.
     * - [Error] indicates that there was an error during the transaction.
     */
    @Serializable
    enum class Status {
        Incomplete,
        Completed,
        Error;
    }

    /**
     * Represents the type of the transaction.
     *
     * - [Presentation] indicates that the transaction is related to a presentation of documents.
     * - [Issuance] indicates that the transaction is related to the issuance of documents.
     * - [Signing] indicates that the transaction is related to the signing of documents.
     * - [Deleting] indicates that the transaction is related to the deletion of documents.
     */
    @Serializable
    enum class Type {
        Presentation,
        Issuance,
        Signing,
        Deleting;
    }

    /**
     * Represents the relying party involved in the transaction.
     *
     * Appears in the context of [Type.Presentation] and contains information about the relying party.
     *
     * @property name The name of the relying party.
     * @property isVerified Indicates whether the relying party is verified.
     * @property certificateChain The certificate chain of the relying party.
     * @property readerAuth The reader authentication data.
     */
    @Serializable
    data class RelyingParty(
        val name: String,
        val isVerified: Boolean,
        val certificateChain: List<String>,
        val readerAuth: String?,
    )

    /**
     * Represents the issuer involved in an issuance transaction.
     *
     * @property name The display name of the issuer.
     * @property identifier The issuer identifier.
     * @property logoUrl Optional issuer logo URL.
     */
    @Serializable
    data class IssuingParty(
        val name: String,
        val identifier: String,
        val logoUrl: String? = null,
    )

    /**
     * Represents a document captured by an issuance transaction.
     *
     * This is primarily used for multi-document issuance so integrators can
     * render a single issuance transaction with all documents involved.
     */
    @Serializable
    data class IssuedDocument(
        val documentId: String?,
        val docType: String?,
        val displayName: String?,
        val dataFormat: DataFormat? = null,
    )

    /**
     * Represents the format of the data in the [eu.europa.ec.eudi.wallet.transactionLogging.TransactionLog.rawResponse]
     *
     * - [Cbor] indicates that the data is in CBOR format.
     * - [Json] indicates that the data is in JSON format.
     */
    @Serializable
    enum class DataFormat {
        Cbor,
        Json,
    }

    @Serializable
    data class Metadata(
        val issuerMetadata: String?,
        val format: String,
        val index: Int,
        val queryId: String? = null
    ) {
        override fun toString(): String {
            return issuerMetadata ?: ""
        }

        companion object {
            val Json = Json {
                ignoreUnknownKeys = true
                allowStructuredMapKeys = true
            }

            fun fromJson(jsonStr: String): Metadata {
                return Json.decodeFromString(jsonStr)
            }
        }

        fun toJson(): String {
            return Json.encodeToString(this)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TransactionLog

        if (timestamp != other.timestamp) return false
        if (status != other.status) return false
        if (type != other.type) return false
        if (relyingParty != other.relyingParty) return false
        if (!rawRequest.contentEquals(other.rawRequest)) return false
        if (!rawResponse.contentEquals(other.rawResponse)) return false
        if (dataFormat != other.dataFormat) return false
        if (!sessionTranscript.contentEquals(other.sessionTranscript)) return false
        if (metadata != other.metadata) return false
        if (transactionId != other.transactionId) return false
        if (errorMessage != other.errorMessage) return false
        if (documentId != other.documentId) return false
        if (docType != other.docType) return false
        if (displayName != other.displayName) return false
        if (documents != other.documents) return false
        if (issuingParty != other.issuingParty) return false

        return true
    }

    override fun hashCode(): Int {
        var result = timestamp.hashCode()
        result = 31 * result + status.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + (relyingParty?.hashCode() ?: 0)
        result = 31 * result + (rawRequest?.contentHashCode() ?: 0)
        result = 31 * result + (rawResponse?.contentHashCode() ?: 0)
        result = 31 * result + (dataFormat?.hashCode() ?: 0)
        result = 31 * result + (sessionTranscript?.contentHashCode() ?: 0)
        result = 31 * result + (metadata?.hashCode() ?: 0)
        result = 31 * result + (transactionId?.hashCode() ?: 0)
        result = 31 * result + (errorMessage?.hashCode() ?: 0)
        result = 31 * result + (documentId?.hashCode() ?: 0)
        result = 31 * result + (docType?.hashCode() ?: 0)
        result = 31 * result + (displayName?.hashCode() ?: 0)
        result = 31 * result + (documents?.hashCode() ?: 0)
        result = 31 * result + (issuingParty?.hashCode() ?: 0)
        return result
    }
}
