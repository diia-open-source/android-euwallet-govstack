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

package eu.europa.ec.eudi.wallet.issue.openid4vci

import eu.europa.ec.eudi.openid4vci.AuthorizedRequest
import eu.europa.ec.eudi.openid4vci.ClientAuthentication
import eu.europa.ec.eudi.openid4vci.CredentialIssuerId
import eu.europa.ec.eudi.openid4vci.IssuedCredential
import eu.europa.ec.eudi.openid4vci.Issuer
import eu.europa.ec.eudi.openid4vci.SubmissionOutcome
import eu.europa.ec.eudi.wallet.document.DocumentManager
import eu.europa.ec.eudi.wallet.document.IssuedDocument
import eu.europa.ec.eudi.wallet.document.Outcome
import eu.europa.ec.eudi.wallet.document.UnsignedDocument
import eu.europa.ec.eudi.wallet.document.credential.IssuerProvidedCredential
import eu.europa.ec.eudi.wallet.document.format.MsoMdocFormat
import eu.europa.ec.eudi.wallet.transactionLogging.TransactionLog
import eu.europa.ec.eudi.wallet.transactionLogging.TransactionLogger
import eu.europa.ec.eudi.wallet.logging.Logger
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.multipaz.storage.ephemeral.EphemeralStorage

class ProcessResponseTest {

    @Test
    fun `process logs completed issuance when document is issued successfully`() = runTest {
        val unsignedDocument = mockUnsignedDocument()
        val issuedDocument = mockIssuedDocument()
        val issuedOutcome = Outcome.success(issuedDocument)
        val documentManager = mockk<DocumentManager>(relaxed = true) {
            every { storeIssuedDocument(unsignedDocument, any<List<IssuerProvidedCredential>>()) } returns issuedOutcome
        }
        val transactionLogSlot = slot<TransactionLog>()
        val transactionLogger = mockk<TransactionLogger> {
            every { log(capture(transactionLogSlot)) } just Runs
        }

        val processor = createProcessResponse(
            documentManager = documentManager,
            transactionLogger = transactionLogger,
            unsignedDocument = unsignedDocument,
            offeredDocument = mockOfferedDocument(),
            listener = { _: IssueEvent -> }
        )

        processor.process(
            SubmitRequest.Response(
                mapOf(
                    unsignedDocument to SubmitRequest.ResponseResult(
                        keyAliases = listOf("key-1"),
                        outcome = Result.success(mockSuccessfulSubmissionOutcome()),
                    )
                )
            )
        )

        verify(exactly = 1) { transactionLogger.log(any()) }
        assertEquals(TransactionLog.Type.Issuance, transactionLogSlot.captured.type)
        assertEquals(TransactionLog.Status.Completed, transactionLogSlot.captured.status)
        assertEquals("issued-doc", transactionLogSlot.captured.documentId)
        assertEquals("Issued PID", transactionLogSlot.captured.displayName)
        assertEquals("Test Issuer", transactionLogSlot.captured.issuingParty?.name)
    }

    @Test
    fun `process logs failed issuance when submission outcome is failed`() = runTest {
        val unsignedDocument = mockUnsignedDocument()
        val error = IllegalStateException("issuance failed")
        val documentManager = mockk<DocumentManager>(relaxed = true)
        val transactionLogSlot = slot<TransactionLog>()
        val transactionLogger = mockk<TransactionLogger> {
            every { log(capture(transactionLogSlot)) } just Runs
        }

        val processor = createProcessResponse(
            documentManager = documentManager,
            transactionLogger = transactionLogger,
            unsignedDocument = unsignedDocument,
            offeredDocument = mockOfferedDocument(),
            listener = { _: IssueEvent -> }
        )

        val failedOutcome = mockk<SubmissionOutcome.Failed> {
            every { this@mockk.error } returns mockk {
                every { cause } returns error
            }
        }

        processor.process(
            SubmitRequest.Response(
                mapOf(
                    unsignedDocument to SubmitRequest.ResponseResult(
                        keyAliases = emptyList(),
                        outcome = Result.success(failedOutcome),
                    )
                )
            )
        )

        verify(exactly = 1) { documentManager.deleteDocumentById("unsigned-doc") }
        verify(exactly = 1) { transactionLogger.log(any()) }
        assertEquals(TransactionLog.Type.Issuance, transactionLogSlot.captured.type)
        assertEquals(TransactionLog.Status.Error, transactionLogSlot.captured.status)
        assertEquals("issuance failed", transactionLogSlot.captured.errorMessage)
        assertEquals("unsigned-doc", transactionLogSlot.captured.documentId)
    }

    @Test
    fun `process keeps successful issuance flow when transaction logger throws`() = runTest {
        val unsignedDocument = mockUnsignedDocument()
        val issuedDocument = mockIssuedDocument()
        val issuedOutcome = Outcome.success(issuedDocument)
        val documentManager = mockk<DocumentManager>(relaxed = true) {
            every { storeIssuedDocument(unsignedDocument, any<List<IssuerProvidedCredential>>()) } returns issuedOutcome
        }
        val logger = mockk<Logger>(relaxed = true)
        val transactionLogger = mockk<TransactionLogger> {
            every { log(any()) } throws IllegalStateException("logger failed")
        }
        val events = mutableListOf<IssueEvent>()

        val processor = createProcessResponse(
            documentManager = documentManager,
            transactionLogger = transactionLogger,
            unsignedDocument = unsignedDocument,
            offeredDocument = mockOfferedDocument(),
            logger = logger,
            listener = { events += it }
        )

        processor.process(
            SubmitRequest.Response(
                mapOf(
                    unsignedDocument to SubmitRequest.ResponseResult(
                        keyAliases = listOf("key-1"),
                        outcome = Result.success(mockSuccessfulSubmissionOutcome()),
                    )
                )
            )
        )

        assertTrue(events.single() is IssueEvent.DocumentIssued)
        verify(atLeast = 1) { logger.log(any()) }
    }

    @Test
    fun `process aggregates successful multi-document issuance into a single log`() = runTest {
        val unsignedDocumentOne = mockUnsignedDocument(
            id = "unsigned-doc-1",
            name = "Unsigned PID 1",
            docType = "eu.europa.ec.eudi.pid.1",
        )
        val unsignedDocumentTwo = mockUnsignedDocument(
            id = "unsigned-doc-2",
            name = "Unsigned PID 2",
            docType = "eu.europa.ec.eudi.pid.2",
        )
        val issuedDocumentOne = mockIssuedDocument(
            id = "issued-doc-1",
            name = "Issued PID 1",
            docType = "eu.europa.ec.eudi.pid.1",
        )
        val issuedDocumentTwo = mockIssuedDocument(
            id = "issued-doc-2",
            name = "Issued PID 2",
            docType = "eu.europa.ec.eudi.pid.2",
        )
        val documentManager = mockk<DocumentManager>(relaxed = true) {
            every {
                storeIssuedDocument(
                    unsignedDocumentOne,
                    any<List<IssuerProvidedCredential>>()
                )
            } returns Outcome.success(issuedDocumentOne)
            every {
                storeIssuedDocument(
                    unsignedDocumentTwo,
                    any<List<IssuerProvidedCredential>>()
                )
            } returns Outcome.success(issuedDocumentTwo)
        }
        val transactionLogSlot = slot<TransactionLog>()
        val transactionLogger = mockk<TransactionLogger> {
            every { log(capture(transactionLogSlot)) } just Runs
        }

        val processor = ProcessResponse(
            documentManager = documentManager,
            deferredContextFactory = { _, _, _, _ ->
                throw UnsupportedOperationException("Not used in this test")
            },
            clientAttestationPopKeyId = null,
            listener = { _: IssueEvent -> },
            issuedDocumentIds = mutableListOf(),
            deferredDocumentIds = mutableListOf(),
            logger = null,
            authorizedRequest = mockk<AuthorizedRequest>(relaxed = true),
            issuer = mockk<Issuer>(relaxed = true),
            documentToConfigurationMap = mapOf(
                unsignedDocumentOne to mockOfferedDocument(),
                unsignedDocumentTwo to mockOfferedDocument(),
            ),
            dpopKeyAlias = null,
            issuanceMetadataStorage = EphemeralStorage(),
            clientAuthentication = mockk<ClientAuthentication.None> {
                every { id } returns "client-id"
            },
            transactionLogger = transactionLogger,
        )

        val response = SubmitRequest.Response(
            mapOf(
                unsignedDocumentOne to SubmitRequest.ResponseResult(
                    keyAliases = listOf("key-1"),
                    outcome = Result.success(mockSuccessfulSubmissionOutcome()),
                ),
                unsignedDocumentTwo to SubmitRequest.ResponseResult(
                    keyAliases = listOf("key-2"),
                    outcome = Result.success(mockSuccessfulSubmissionOutcome()),
                ),
            )
        )

        processor.process(response)

        verify(exactly = 1) { transactionLogger.log(any()) }
        assertEquals(TransactionLog.Type.Issuance, transactionLogSlot.captured.type)
        assertEquals(TransactionLog.Status.Completed, transactionLogSlot.captured.status)
        assertTrue(transactionLogSlot.captured.transactionId?.isNotBlank() == true)
        assertEquals(2, transactionLogSlot.captured.documents?.size)
        assertEquals("issued-doc-1", transactionLogSlot.captured.documents?.get(0)?.documentId)
        assertEquals("issued-doc-2", transactionLogSlot.captured.documents?.get(1)?.documentId)
        assertEquals("Test Issuer", transactionLogSlot.captured.issuingParty?.name)
    }

    private fun createProcessResponse(
        documentManager: DocumentManager,
        transactionLogger: TransactionLogger,
        unsignedDocument: UnsignedDocument,
        offeredDocument: Offer.OfferedDocument,
        logger: Logger? = null,
        listener: OpenId4VciManager.OnResult<IssueEvent>,
    ): ProcessResponse {
        return ProcessResponse(
            documentManager = documentManager,
            deferredContextFactory = { _, _, _, _ ->
                throw UnsupportedOperationException("Not used in this test")
            },
            clientAttestationPopKeyId = null,
            listener = listener,
            issuedDocumentIds = mutableListOf(),
            deferredDocumentIds = mutableListOf(),
            logger = logger,
            authorizedRequest = mockk<AuthorizedRequest>(relaxed = true),
            issuer = mockk<Issuer>(relaxed = true),
            documentToConfigurationMap = mapOf(unsignedDocument to offeredDocument),
            dpopKeyAlias = null,
            issuanceMetadataStorage = EphemeralStorage(),
            clientAuthentication = mockk<ClientAuthentication.None> {
                every { id } returns "client-id"
            },
            transactionLogger = transactionLogger,
        )
    }

    private fun mockUnsignedDocument(
        id: String = "unsigned-doc",
        name: String = "Unsigned PID",
        docType: String = "eu.europa.ec.eudi.pid.1",
    ): UnsignedDocument = mockk(relaxed = true) {
        every { this@mockk.id } returns id
        every { this@mockk.name } returns name
        every { format } returns MsoMdocFormat(docType)
    }

    private fun mockIssuedDocument(
        id: String = "issued-doc",
        name: String = "Issued PID",
        docType: String = "eu.europa.ec.eudi.pid.1",
    ): IssuedDocument = mockk(relaxed = true) {
        every { this@mockk.id } returns id
        every { this@mockk.name } returns name
        every { format } returns MsoMdocFormat(docType)
    }

    private fun mockOfferedDocument(): Offer.OfferedDocument = mockk(relaxed = true) {
        every { offer } returns mockk(relaxed = true) {
            every { issuerMetadata } returns mockk(relaxed = true) {
                every { display } returns listOf(
                    mockk(relaxed = true) {
                        every { name } returns "Test Issuer"
                        every { logo } returns null
                    }
                )
            }
            every { credentialOffer } returns mockk(relaxed = true) {
                every { credentialIssuerIdentifier } returns CredentialIssuerId("https://issuer.example.com").getOrThrow()
            }
        }
    }

    private fun mockSuccessfulSubmissionOutcome(): SubmissionOutcome.Success {
        return SubmissionOutcome.Success(
            credentials = listOf(IssuedCredential.string("AQI")),
            notificationId = null,
        )
    }
}
