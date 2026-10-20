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

package eu.europa.ec.eudi.wallet.transactionLogging

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TransactionLogParserTest {

    @Test
    fun `parse returns issuance details for issuance log`() {
        val log = TransactionLog(
            timestamp = 1L,
            status = TransactionLog.Status.Completed,
            type = TransactionLog.Type.Issuance,
            relyingParty = null,
            rawRequest = null,
            rawResponse = null,
            dataFormat = TransactionLog.DataFormat.Json,
            sessionTranscript = null,
            metadata = null,
            documentId = "doc-1",
            docType = "eu.europa.pid",
            displayName = "PID",
            issuingParty = TransactionLog.IssuingParty(
                name = "Test Issuer",
                identifier = "https://issuer.example.com"
            )
        )

        val parsed = TransactionLogParser.parse(log).getOrThrow()

        assertTrue(parsed is TransactionLogDetails.Issuance)
        val issuance = (parsed as TransactionLogDetails.Issuance).data
        assertEquals("doc-1", issuance.documentId)
        assertEquals("PID", issuance.displayName)
        assertEquals("Test Issuer", issuance.issuingParty.name)
    }

    @Test
    fun `parse returns deleting details for deleting log`() {
        val log = TransactionLog(
            timestamp = 1L,
            status = TransactionLog.Status.Error,
            type = TransactionLog.Type.Deleting,
            relyingParty = null,
            rawRequest = null,
            rawResponse = null,
            dataFormat = TransactionLog.DataFormat.Cbor,
            sessionTranscript = null,
            metadata = null,
            errorMessage = "boom",
            documentId = "doc-2",
            docType = "org.iso.18013.5.1.mDL",
            displayName = "Driving Licence",
        )

        val parsed = TransactionLogParser.parse(log).getOrThrow()

        assertTrue(parsed is TransactionLogDetails.Deleting)
        val deleting = (parsed as TransactionLogDetails.Deleting).data
        assertEquals("doc-2", deleting.documentId)
        assertEquals("Driving Licence", deleting.displayName)
        assertEquals("boom", deleting.errorMessage)
    }

    @Test
    fun `parse returns signing details for signing log`() {
        val log = TransactionLog(
            timestamp = 1L,
            status = TransactionLog.Status.Completed,
            type = TransactionLog.Type.Signing,
            relyingParty = null,
            rawRequest = null,
            rawResponse = null,
            dataFormat = null,
            sessionTranscript = null,
            metadata = listOf("meta-1")
        )

        val parsed = TransactionLogParser.parse(log).getOrThrow()

        assertTrue(parsed is TransactionLogDetails.Signing)
        assertEquals(listOf("meta-1"), (parsed as TransactionLogDetails.Signing).metadata)
    }

    @Test
    fun `transaction log with new fields remains serializable`() {
        val json = Json { ignoreUnknownKeys = true }
        val log = TransactionLog(
            timestamp = 1L,
            status = TransactionLog.Status.Completed,
            type = TransactionLog.Type.Issuance,
            relyingParty = null,
            rawRequest = null,
            rawResponse = null,
            dataFormat = TransactionLog.DataFormat.Json,
            sessionTranscript = null,
            metadata = null,
            errorMessage = "boom",
            documentId = "doc-3",
            docType = "vct:test",
            displayName = "Test VC",
            issuingParty = TransactionLog.IssuingParty(
                name = "Issuer",
                identifier = "https://issuer.example.com",
                logoUrl = "https://issuer.example.com/logo.png"
            )
        )

        val encoded = json.encodeToString(TransactionLog.serializer(), log)
        val decoded = json.decodeFromString(TransactionLog.serializer(), encoded)

        assertEquals(log.type, decoded.type)
        assertEquals(log.documentId, decoded.documentId)
        assertEquals(log.issuingParty, decoded.issuingParty)
    }
}
