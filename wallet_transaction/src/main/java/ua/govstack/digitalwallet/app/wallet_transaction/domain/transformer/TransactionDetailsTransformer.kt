package ua.govstack.digitalwallet.app.wallet_transaction.domain.transformer

import eu.europa.ec.eudi.wallet.document.IssuedDocument
import ua.govstack.digitalwallet.app.wallet_core.documents.extensions.toTransactionClaimDomains
import ua.govstack.digitalwallet.app.wallet_transaction.domain.model.TransactionLogDataDomain
import ua.govstack.digitalwallet.app.wallet_transaction.domain.model.TransactionLogDataDomain.IssuanceDocument

internal fun TransactionLogDataDomain.IssuanceLog.withDocuments(
    documents: List<IssuedDocument>
): TransactionLogDataDomain.IssuanceLog {
    return this.copy(
        documents = this.documents.map { issuanceDocument ->
            val document = documents.firstOrNull { it.id == issuanceDocument.id }
            IssuanceDocument(
                format = (document?.format ?: issuanceDocument.format).toString(),
                issuerMetadata = document?.issuerMetadata ?: issuanceDocument.issuerMetadata,
                name = document?.name ?: issuanceDocument.name,
                id = issuanceDocument.id,
                fields = document?.toTransactionClaimDomains().orEmpty()
            )
        }
    )
}
