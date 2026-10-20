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

import eu.europa.ec.eudi.wallet.document.DocumentManager
import eu.europa.ec.eudi.wallet.document.IssuedDocument
import eu.europa.ec.eudi.wallet.document.Outcome
import eu.europa.ec.eudi.wallet.document.ProofOfDeletion
import eu.europa.ec.eudi.wallet.document.format.MsoMdocFormat
import eu.europa.ec.eudi.wallet.logging.Logger
import eu.europa.ec.eudi.wallet.transactionLogging.TransactionLog
import eu.europa.ec.eudi.wallet.transactionLogging.TransactionLogger
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class TransactionLoggingDocumentManagerTest {

    @Test
    fun `deleteDocumentById logs completed deletion when delegate succeeds`() {
        val outcome = mockk<Outcome<ProofOfDeletion?>>(relaxed = true) {
            every { getOrThrow() } returns null
        }
        val document = mockk<IssuedDocument>(relaxed = true) {
            every { id } returns "doc-123"
            every { name } returns "PID"
            every { format } returns MsoMdocFormat("eu.europa.ec.eudi.pid.1")
        }
        val delegate = mockk<DocumentManager>(relaxed = true) {
            every { getDocumentById("doc-123") } returns document
            every { deleteDocumentById("doc-123") } returns outcome
        }
        val transactionLogSlot = slot<TransactionLog>()
        val transactionLogger = mockk<TransactionLogger> {
            every { log(capture(transactionLogSlot)) } just Runs
        }

        val wrapper = TransactionLoggingDocumentManager(delegate, transactionLogger)
        val result = wrapper.deleteDocumentById("doc-123")

        assertSame(outcome, result)
        verify(exactly = 1) { delegate.deleteDocumentById("doc-123") }
        verify(exactly = 1) { transactionLogger.log(any()) }
        assertEquals(TransactionLog.Type.Deleting, transactionLogSlot.captured.type)
        assertEquals(TransactionLog.Status.Completed, transactionLogSlot.captured.status)
        assertEquals("doc-123", transactionLogSlot.captured.documentId)
        assertEquals("PID", transactionLogSlot.captured.displayName)
    }

    @Test
    fun `deleteDocumentById logs failed deletion when outcome is erroneous`() {
        val error = IllegalStateException("delete failed")
        val outcome = mockk<Outcome<ProofOfDeletion?>>(relaxed = true) {
            every { getOrThrow() } throws error
        }
        val document = mockk<IssuedDocument>(relaxed = true) {
            every { id } returns "doc-123"
            every { name } returns "PID"
            every { format } returns MsoMdocFormat("eu.europa.ec.eudi.pid.1")
        }
        val delegate = mockk<DocumentManager>(relaxed = true) {
            every { getDocumentById("doc-123") } returns document
            every { deleteDocumentById("doc-123") } returns outcome
        }
        val transactionLogSlot = slot<TransactionLog>()
        val transactionLogger = mockk<TransactionLogger> {
            every { log(capture(transactionLogSlot)) } just Runs
        }

        val wrapper = TransactionLoggingDocumentManager(delegate, transactionLogger)
        val result = wrapper.deleteDocumentById("doc-123")

        assertSame(outcome, result)
        verify(exactly = 1) { transactionLogger.log(any()) }
        assertEquals(TransactionLog.Status.Error, transactionLogSlot.captured.status)
        assertEquals("delete failed", transactionLogSlot.captured.errorMessage)
    }

    @Test
    fun `deleteDocumentById returns delegate outcome when transaction logger throws`() {
        val outcome = mockk<Outcome<ProofOfDeletion?>>(relaxed = true) {
            every { getOrThrow() } returns null
        }
        val document = mockk<IssuedDocument>(relaxed = true) {
            every { id } returns "doc-123"
            every { name } returns "PID"
            every { format } returns MsoMdocFormat("eu.europa.ec.eudi.pid.1")
        }
        val delegate = mockk<DocumentManager>(relaxed = true) {
            every { getDocumentById("doc-123") } returns document
            every { deleteDocumentById("doc-123") } returns outcome
        }
        val logger = mockk<Logger>(relaxed = true)
        val transactionLogger = mockk<TransactionLogger> {
            every { log(any()) } throws IllegalStateException("logger failed")
        }

        val wrapper = TransactionLoggingDocumentManager(
            delegate = delegate,
            transactionLogger = transactionLogger,
            logger = logger,
        )
        val result = wrapper.deleteDocumentById("doc-123")

        assertSame(outcome, result)
        verify(exactly = 1) { logger.log(any()) }
    }
}
