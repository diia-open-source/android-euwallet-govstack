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

import eu.europa.ec.eudi.openid4vci.DeferredCredentialQueryOutcome
import eu.europa.ec.eudi.openid4vci.IssuedCredential
import eu.europa.ec.eudi.wallet.document.DeferredDocument
import eu.europa.ec.eudi.wallet.document.DocumentManager
import eu.europa.ec.eudi.wallet.document.IssuedDocument
import eu.europa.ec.eudi.wallet.document.Outcome
import eu.europa.ec.eudi.wallet.document.credential.IssuerProvidedCredential
import eu.europa.ec.eudi.wallet.document.format.MsoMdocFormat
import eu.europa.ec.eudi.wallet.document.metadata.IssuerMetadata
import eu.europa.ec.eudi.wallet.transactionLogging.TransactionLog
import eu.europa.ec.eudi.wallet.transactionLogging.TransactionLogger
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Test

class ProcessDeferredOutcomeTest {

    @Test
    fun `process logs deferred issuance with issuer display name`() {
        val deferredDocument = mockDeferredDocument()
        val issuedDocument = mockIssuedDocument()
        val documentManager = mockk<DocumentManager>(relaxed = true) {
            every {
                storeIssuedDocument(deferredDocument, any<List<IssuerProvidedCredential>>())
            } returns Outcome.success(issuedDocument)
        }
        val transactionLogSlot = slot<TransactionLog>()
        val transactionLogger = mockk<TransactionLogger> {
            every { log(capture(transactionLogSlot)) } just Runs
        }

        val processor = ProcessDeferredOutcome(
            documentManager = documentManager,
            callback = OpenId4VciManager.OnDeferredIssueResult { },
            deferredContext = null,
            transactionLogger = transactionLogger,
        )

        processor.process(
            deferredDocument = deferredDocument,
            keyAliases = listOf("key-1"),
            outcome = DeferredCredentialQueryOutcome.Issued(
                credentials = listOf(IssuedCredential.string("AQI")),
                notificationId = null,
            )
        )

        verify(exactly = 1) { transactionLogger.log(any()) }
        assertEquals(TransactionLog.Status.Completed, transactionLogSlot.captured.status)
        assertEquals("deferred-doc", transactionLogSlot.captured.transactionId)
        assertEquals("Friendly Issuer", transactionLogSlot.captured.issuingParty?.name)
        assertEquals("https://issuer.example.com", transactionLogSlot.captured.issuingParty?.identifier)
    }

    private fun mockDeferredDocument(): DeferredDocument = mockk(relaxed = true) {
        every { id } returns "deferred-doc"
        every { name } returns "Deferred PID"
        every { format } returns MsoMdocFormat("eu.europa.ec.eudi.pid.1")
        every { issuerMetadata } returns mockIssuerMetadata()
    }

    private fun mockIssuedDocument(): IssuedDocument = mockk(relaxed = true) {
        every { id } returns "issued-doc"
        every { name } returns "Issued PID"
        every { format } returns MsoMdocFormat("eu.europa.ec.eudi.pid.1")
        every { issuerMetadata } returns mockIssuerMetadata()
    }

    private fun mockIssuerMetadata(): IssuerMetadata = mockk(relaxed = true) {
        every { credentialIssuerIdentifier } returns "https://issuer.example.com"
        every { issuerDisplay } returns listOf(
            mockk(relaxed = true) {
                every { name } returns "Friendly Issuer"
                every { logo } returns null
            }
        )
    }
}
