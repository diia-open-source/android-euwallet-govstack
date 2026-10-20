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

import eu.europa.ec.eudi.wallet.transactionLogging.deleting.DeletingTransactionLog
import eu.europa.ec.eudi.wallet.transactionLogging.issuance.IssuanceTransactionLog
import eu.europa.ec.eudi.wallet.transactionLogging.presentation.PresentationTransactionLog

object TransactionLogParser {
    fun parse(log: TransactionLog): Result<TransactionLogDetails> = runCatching {
        when (log.type) {
            TransactionLog.Type.Presentation ->
                TransactionLogDetails.Presentation(
                    PresentationTransactionLog.fromTransactionLog(log).getOrThrow()
                )

            TransactionLog.Type.Issuance ->
                TransactionLogDetails.Issuance(
                    IssuanceTransactionLog.fromTransactionLog(log).getOrThrow()
                )

            TransactionLog.Type.Deleting ->
                TransactionLogDetails.Deleting(
                    DeletingTransactionLog.fromTransactionLog(log).getOrThrow()
                )

            TransactionLog.Type.Signing ->
                TransactionLogDetails.Signing(
                    metadata = log.metadata.orEmpty()
                )
        }
    }
}
