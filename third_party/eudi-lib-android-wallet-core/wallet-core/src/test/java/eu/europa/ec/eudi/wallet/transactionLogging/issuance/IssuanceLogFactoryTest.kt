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

import eu.europa.ec.eudi.openid4vci.CredentialIssuerId
import eu.europa.ec.eudi.wallet.document.DeferredDocument
import eu.europa.ec.eudi.wallet.document.format.MsoMdocFormat
import eu.europa.ec.eudi.wallet.document.metadata.IssuerMetadata
import eu.europa.ec.eudi.wallet.issue.openid4vci.Offer
import eu.europa.ec.eudi.wallet.transactionLogging.TransactionLog
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

class IssuanceLogFactoryTest {

    @Test
    fun `createDeferred creates incomplete issuance log`() {
        val document = mockDeferredDocument()

        val log = IssuanceLogFactory.createDeferred(
            document = document,
            offeredDocument = mockOfferedDocument(),
            transactionId = "tx-deferred-doc",
        )

        assertEquals(TransactionLog.Type.Issuance, log.type)
        assertEquals(TransactionLog.Status.Incomplete, log.status)
        assertEquals("tx-deferred-doc", log.transactionId)
        assertEquals("deferred-doc", log.documentId)
        assertEquals("Deferred PID", log.displayName)
        assertEquals("Friendly Issuer", log.issuingParty?.name)
    }

    @Test
    fun `createCompleted prefers issuer display name over identifier`() {
        val document = mockDeferredDocument()

        val log = IssuanceLogFactory.createCompleted(
            document = document,
            issuerMetadata = mockIssuerMetadata(),
            issuerIdentifier = "https://issuer.example.com",
        )

        assertEquals("Friendly Issuer", log.issuingParty?.name)
        assertEquals("https://issuer.example.com", log.issuingParty?.identifier)
    }

    private fun mockDeferredDocument(): DeferredDocument = mockk(relaxed = true) {
        every { id } returns "deferred-doc"
        every { name } returns "Deferred PID"
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

    private fun mockOfferedDocument(): Offer.OfferedDocument = mockk(relaxed = true) {
        every { offer } returns mockk(relaxed = true) {
            every { issuerMetadata } returns mockk(relaxed = true) {
                every { display } returns listOf(
                    mockk(relaxed = true) {
                        every { name } returns "Friendly Issuer"
                        every { logo } returns null
                    }
                )
            }
            every { credentialOffer } returns mockk(relaxed = true) {
                every { credentialIssuerIdentifier } returns CredentialIssuerId("https://issuer.example.com").getOrThrow()
            }
        }
    }
}
