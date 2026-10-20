/*
 * Copyright (c) 2024-2025 European Commission
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

package eu.europa.ec.eudi.wallet.issue.openid4vci

import eu.europa.ec.eudi.openid4vci.AccessToken
import eu.europa.ec.eudi.openid4vci.AuthorizedRequest
import eu.europa.ec.eudi.openid4vci.ClientAuthentication
import eu.europa.ec.eudi.openid4vci.Issuer
import eu.europa.ec.eudi.openid4vci.SubmissionOutcome
import eu.europa.ec.eudi.wallet.document.DocumentId
import eu.europa.ec.eudi.wallet.document.DocumentManager
import eu.europa.ec.eudi.wallet.document.UnsignedDocument
import eu.europa.ec.eudi.wallet.internal.d
import eu.europa.ec.eudi.wallet.issue.openid4vci.IssueEvent.Companion.failure
import eu.europa.ec.eudi.wallet.issue.openid4vci.OpenId4VciManager.Companion.TAG
import eu.europa.ec.eudi.wallet.issue.openid4vci.reissue.IssuanceMetadata
import eu.europa.ec.eudi.wallet.logging.Logger
import eu.europa.ec.eudi.wallet.transactionLogging.TransactionLog
import eu.europa.ec.eudi.wallet.transactionLogging.TransactionLogger
import eu.europa.ec.eudi.wallet.transactionLogging.issuance.IssuanceLogFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.io.bytestring.ByteString
import org.multipaz.storage.Storage
import java.util.UUID
import kotlin.coroutines.resume

internal class ProcessResponse(
    val documentManager: DocumentManager,
    val deferredContextFactory: DeferredContextFactory,
    val clientAttestationPopKeyId: String?,
    val listener: OpenId4VciManager.OnResult<IssueEvent>,
    val issuedDocumentIds: MutableList<DocumentId>,
    val deferredDocumentIds: MutableList<DocumentId>,
    val logger: Logger? = null,
    val authorizedRequest: AuthorizedRequest,
    val issuer: Issuer,
    val documentToConfigurationMap: Map<UnsignedDocument, Offer.OfferedDocument>,
    val dpopKeyAlias: String?,
    val issuanceMetadataStorage: Storage,
    val clientAuthentication: ClientAuthentication,
    val replacesDocumentId: DocumentId? = null,
    val logIncompleteDeferredIssuance: Boolean = false,
    val transactionLogger: TransactionLogger? = null,
) {

    suspend fun process(response: SubmitRequest.Response) {
        val batchAggregation = response.takeIf { shouldAggregateIssuanceLog(it) }?.let {
            BatchIssuanceAggregation(
                transactionId = UUID.randomUUID().toString(),
                timestamp = System.currentTimeMillis(),
            )
        }

        response.forEach { (unsignedDocument, result) ->
            process(unsignedDocument, result.keyAliases, result.outcome, batchAggregation)
        }

        batchAggregation?.buildTransactionLog()?.let { transactionLog ->
            logTransactionSafely(
                transactionLog = transactionLog,
                sourceMethod = "process"
            )
        }
    }

    private suspend fun process(
        unsignedDocument: UnsignedDocument,
        keyAliases: List<String>,
        outcomeResult: Result<SubmissionOutcome>,
        batchAggregation: BatchIssuanceAggregation? = null,
    ) {
        try {
            processSubmittedRequest(unsignedDocument, keyAliases, outcomeResult.getOrThrow(), batchAggregation)
        } catch (e: Throwable) {
            when (e) {
                is UserAuthRequiredException -> {

                    val keyUnlockData = suspendCancellableCoroutine { cont ->
                        cont.invokeOnCancellation { listener(IssueEvent.Failure(e)) }
                        listener(
                            IssueEvent.DocumentRequiresUserAuth(
                                document = unsignedDocument,
                                signingAlgorithm = e.signingAlgorithm,
                                keysRequireAuth = e.keysAndSecureAreas,
                                resume = { keyUnlockData ->
                                    runBlocking {
                                        cont.resume(keyUnlockData)
                                    }
                                },
                                cancel = {
                                    runBlocking {
                                        cont.cancel(IllegalStateException("Canceled"))
                                    }
                                }
                            )
                        )
                    }
                    val (keyAliases, outcome) = e.resume(keyUnlockData)
                    processSubmittedRequest(unsignedDocument, keyAliases, outcome, batchAggregation)
                }

                else -> listener(IssueEvent.Failure(e))
            }
        }
    }

    private fun processSubmittedRequest(
        unsignedDocument: UnsignedDocument,
        keyAliases: List<String>,
        outcome: SubmissionOutcome,
        batchAggregation: BatchIssuanceAggregation? = null,
    ) {
        when (outcome) {
            is SubmissionOutcome.Success -> runCatching {
                val credentials = outcome.credentials.map { it.credential }.zip(keyAliases)
                documentManager.storeIssuedDocument(unsignedDocument, credentials) { message ->
                    logger?.d(TAG, message)
                }.getOrThrow()
            }.onSuccess { document ->
                issuedDocumentIds.add(document.id)
                batchAggregation?.recordSuccess(document, documentToConfigurationMap[unsignedDocument]) ?: logTransactionSafely(
                    IssuanceLogFactory.createCompleted(
                        document = document,
                        offeredDocument = documentToConfigurationMap[unsignedDocument]
                    ),
                    sourceMethod = "processSubmittedRequest"
                )

                // Store issuance metadata in background coroutine
                CoroutineScope(Dispatchers.IO).launch {
                    storeIssuanceMetadata(document.id, unsignedDocument, keyAliases)
                }

                listener(IssueEvent.DocumentIssued(document))
            }.onFailure { error ->
                documentManager.deleteDocumentById(unsignedDocument.id)
                batchAggregation?.recordFailure(
                    unsignedDocument = unsignedDocument,
                    offeredDocument = documentToConfigurationMap[unsignedDocument],
                    error = error,
                ) ?: logTransactionSafely(
                    IssuanceLogFactory.createFailed(
                        documentId = unsignedDocument.id,
                        documentFormat = unsignedDocument.format,
                        displayName = unsignedDocument.name,
                        offeredDocument = documentToConfigurationMap[unsignedDocument],
                        error = error,
                    ),
                    sourceMethod = "processSubmittedRequest"
                )
                listener(IssueEvent.DocumentFailed(unsignedDocument, error))
            }

            is SubmissionOutcome.Failed -> {
                documentManager.deleteDocumentById(unsignedDocument.id)
                val error = outcome.error.cause ?: IllegalStateException("CredentialIssuanceError")
                batchAggregation?.recordFailure(
                    unsignedDocument = unsignedDocument,
                    offeredDocument = documentToConfigurationMap[unsignedDocument],
                    error = error,
                ) ?: logTransactionSafely(
                    IssuanceLogFactory.createFailed(
                        documentId = unsignedDocument.id,
                        documentFormat = unsignedDocument.format,
                        displayName = unsignedDocument.name,
                        offeredDocument = documentToConfigurationMap[unsignedDocument],
                        error = error,
                    ),
                    sourceMethod = "processSubmittedRequest"
                )
                listener(
                    failure(
                        error,
                        unsignedDocument
                    )
                )
            }

            is SubmissionOutcome.Deferred -> {
                val deferredLogTimestamp = System.currentTimeMillis()
                val deferredContext = deferredContextFactory(
                    keyAliases,
                    outcome,
                    clientAttestationPopKeyId,
                    dpopKeyAlias
                ).copy(
                    replacesDocumentId = replacesDocumentId,
                    credentialConfigurationIdentifier = documentToConfigurationMap[unsignedDocument]?.configurationIdentifier?.value,
                    credentialEndpoint = issuer.credentialOffer.credentialIssuerMetadata.credentialEndpoint.toString(),
                    transactionLogTimestamp = deferredLogTimestamp,
                )

                runCatching {
                    documentManager.storeDeferredDocument(
                        unsignedDocument = unsignedDocument,
                        relatedData = deferredContext.toBytes()
                    ).getOrThrow()
                }.onSuccess { document ->
                    deferredDocumentIds.add(document.id)
                    if (logIncompleteDeferredIssuance) {
                        logTransactionSafely(
                            IssuanceLogFactory.createDeferred(
                                document = document,
                                offeredDocument = documentToConfigurationMap[unsignedDocument],
                                transactionId = document.id,
                                timestamp = deferredLogTimestamp,
                            ),
                            sourceMethod = "processSubmittedRequest"
                        )
                    }
                    listener(IssueEvent.DocumentDeferred(document))
                }.onFailure { error ->
                    documentManager.deleteDocumentById(unsignedDocument.id)
                    logTransactionSafely(
                        IssuanceLogFactory.createFailed(
                            documentId = unsignedDocument.id,
                            documentFormat = unsignedDocument.format,
                            displayName = unsignedDocument.name,
                            offeredDocument = documentToConfigurationMap[unsignedDocument],
                            error = error,
                            transactionId = unsignedDocument.id,
                            timestamp = deferredLogTimestamp,
                        ),
                        sourceMethod = "processSubmittedRequest"
                    )
                    listener(IssueEvent.DocumentFailed(unsignedDocument, error))
                }
            }
        }
    }

    /**
     * Stores issuance metadata for a successfully issued document.
     *
     * This method captures all necessary metadata to enable credential re-issuance later
     * without requiring the user to go through the full authorization flow again.
     *
     * @param documentId The ID of the successfully issued document
     * @param unsignedDocument The unsigned document that was issued
     * @param keyAliases The PoP key aliases used for this document
     */
    private suspend fun storeIssuanceMetadata(
        documentId: DocumentId,
        unsignedDocument: UnsignedDocument,
        keyAliases: List<String>,
    ) {

        val refreshToken = authorizedRequest.refreshToken?.refreshToken

        runCatching {
            val credentialConfigurationId = requireNotNull(
                documentToConfigurationMap[unsignedDocument]?.configurationIdentifier?.value
            ) { "Credential configuration identifier not found for document" }

            // Get metadata from credential offer
            val credentialOffer = issuer.credentialOffer
            val issuerMetadata = credentialOffer.credentialIssuerMetadata
            val authServerMetadata = credentialOffer.authorizationServerMetadata

            // Extract client ID and WIA JWT directly from ClientAuthentication
            val (clientId, clientAttestationJwt) = when (val auth = clientAuthentication) {
                is ClientAuthentication.None -> auth.id to null
                is ClientAuthentication.AttestationBased -> auth.id to auth.attestationJWT.jwt.serialize()
                else -> authServerMetadata.issuer.toString() to null
            }

            // Create issuance metadata
            val issuanceMetadata = IssuanceMetadata(
                credentialIssuerId = issuerMetadata.credentialIssuerIdentifier.toString(),
                credentialConfigurationIdentifier = credentialConfigurationId,
                credentialEndpoint = issuerMetadata.credentialEndpoint.toString(),
                tokenEndpoint = authServerMetadata.tokenEndpointURI.toString(),
                authorizationServerId = authServerMetadata.issuer.toString(),
                challengeEndpoint = null, // Not exposed in CIAuthorizationServerMetadata
                clientId = clientId,
                clientAttestationJwt = clientAttestationJwt,
                clientAttestationPopKeyId = clientAttestationPopKeyId,
                popKeyAliases = keyAliases,
                dPoPKeyAlias = dpopKeyAlias,
                accessToken = authorizedRequest.accessToken.accessToken,
                accessTokenType = when (authorizedRequest.accessToken) {
                    is AccessToken.DPoP -> "DPoP"
                    is AccessToken.Bearer -> "Bearer"
                },
                refreshToken = refreshToken,
                tokenTimestamp = authorizedRequest.timestamp.epochSecond,
                grantType = if (authorizedRequest.grant::class.simpleName == "PreAuthorizedCodeGrant") {
                    "pre-authorized_code"
                } else {
                    "authorization_code"
                },
            )

            val table = issuanceMetadataStorage.getTable(IssuanceMetadata.STORAGE_TABLE_SPEC)

            val bytes = ByteString(issuanceMetadata.toByteArray())
            table.insert(key = documentId, data = bytes)

            logger?.d(TAG, "Stored issuance metadata for document $documentId")
        }.onFailure { error ->
            // Log but don't fail the issuance if metadata storage fails
            logger?.log(
                Logger.Record(
                    level = Logger.Companion.LEVEL_ERROR,
                    message = "Failed to store issuance metadata for document $documentId: ${error.message}",
                    sourceClassName = "ProcessResponse",
                    sourceMethod = "storeIssuanceMetadata",
                    thrown = error
                )
            )
        }
    }

    private fun logTransactionSafely(
        transactionLog: TransactionLog,
        sourceMethod: String,
    ) {
        runCatching {
            transactionLogger?.log(transactionLog)
        }.onFailure { error ->
            logger?.log(
                Logger.Record(
                    level = Logger.LEVEL_ERROR,
                    message = "Failed to log issuance transaction",
                    thrown = error,
                    sourceClassName = ProcessResponse::class.java.name,
                    sourceMethod = sourceMethod,
                )
            )
        }
    }

    private fun shouldAggregateIssuanceLog(response: SubmitRequest.Response): Boolean {
        if (response.size <= 1) return false

        return response.values.all { result ->
            when (val outcome = result.outcome.getOrNull()) {
                null -> false
                is SubmissionOutcome.Deferred -> false
                else -> true
            }
        }
    }

    private inner class BatchIssuanceAggregation(
        private val transactionId: String,
        private val timestamp: Long,
    ) {
        private val documents = mutableListOf<TransactionLog.IssuedDocument>()
        private var issuerDocument: Offer.OfferedDocument? = null
        private var status: TransactionLog.Status = TransactionLog.Status.Completed
        private var errorMessage: String? = null

        fun recordSuccess(
            document: eu.europa.ec.eudi.wallet.document.Document,
            offeredDocument: Offer.OfferedDocument?,
        ) {
            if (issuerDocument == null) issuerDocument = offeredDocument
            documents += TransactionLog.IssuedDocument(
                documentId = document.id,
                docType = when (val format = document.format) {
                    is eu.europa.ec.eudi.wallet.document.format.MsoMdocFormat -> format.docType
                    is eu.europa.ec.eudi.wallet.document.format.SdJwtVcFormat -> format.vct
                },
                displayName = document.name,
                dataFormat = when (document.format) {
                    is eu.europa.ec.eudi.wallet.document.format.MsoMdocFormat -> TransactionLog.DataFormat.Cbor
                    is eu.europa.ec.eudi.wallet.document.format.SdJwtVcFormat -> TransactionLog.DataFormat.Json
                },
            )
        }

        fun recordFailure(
            unsignedDocument: UnsignedDocument,
            offeredDocument: Offer.OfferedDocument?,
            error: Throwable,
        ) {
            if (issuerDocument == null) issuerDocument = offeredDocument
            status = TransactionLog.Status.Error
            errorMessage = errorMessage ?: error.message
            documents += TransactionLog.IssuedDocument(
                documentId = unsignedDocument.id,
                docType = when (val format = unsignedDocument.format) {
                    is eu.europa.ec.eudi.wallet.document.format.MsoMdocFormat -> format.docType
                    is eu.europa.ec.eudi.wallet.document.format.SdJwtVcFormat -> format.vct
                },
                displayName = unsignedDocument.name,
                dataFormat = when (unsignedDocument.format) {
                    is eu.europa.ec.eudi.wallet.document.format.MsoMdocFormat -> TransactionLog.DataFormat.Cbor
                    is eu.europa.ec.eudi.wallet.document.format.SdJwtVcFormat -> TransactionLog.DataFormat.Json
                },
            )
        }

        fun buildTransactionLog(): TransactionLog? {
            if (documents.isEmpty()) return null
            return IssuanceLogFactory.createBatch(
                status = status,
                documents = documents.toList(),
                offeredDocument = issuerDocument,
                transactionId = transactionId,
                timestamp = timestamp,
                errorMessage = errorMessage,
            )
        }
    }
}
