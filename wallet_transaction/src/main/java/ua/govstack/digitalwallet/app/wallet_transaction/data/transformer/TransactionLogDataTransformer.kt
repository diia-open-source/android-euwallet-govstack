package ua.govstack.digitalwallet.app.wallet_transaction.data.transformer

import eu.europa.ec.eudi.wallet.transactionLogging.TransactionLogDetails
import eu.europa.ec.eudi.wallet.transactionLogging.presentation.PresentedDocument
import ua.govstack.digitalwallet.app.wallet_core.documents.extensions.toTransactionClaimDomains
import ua.govstack.digitalwallet.app.wallet_transaction.domain.model.TransactionLogDataDomain
import ua.govstack.digitalwallet.app.wallet_transaction.domain.model.TransactionLogDataDomain.IssuanceDocument
import ua.govstack.digitalwallet.app.wallet_transaction.domain.model.TransactionLogDataDomain.PresentationLog
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

@Throws(IllegalArgumentException::class)
internal fun TransactionLogDetails.toTransactionLogData(
    id: String
): TransactionLogDataDomain = when (this) {
    is TransactionLogDetails.Presentation -> PresentationLog(
        id = id,
        name = data.relyingParty.name,
        status = data.status,
        creationLocalDateTime = data.timestamp.toLocalDateTime(),
        creationLocalDate = data.timestamp.toLocalDate(),
        relyingParty = data.relyingParty,
        documents = data.documents.map { it.toIssuanceDocument() },
    )
    is TransactionLogDetails.Issuance -> {
        TransactionLogDataDomain.IssuanceLog(
            id = id,
            name = getIssuerName(),
            status = data.status,
            creationLocalDateTime = data.timestamp.toLocalDateTime(),
            creationLocalDate = data.timestamp.toLocalDate(),
            documents = getIssuanceDocumentsFields(),
        )
    }

    is TransactionLogDetails.Deleting -> TransactionLogDataDomain.DeletingLog(
        id = id,
        name = "Deleted document",
        status = data.status,
        creationLocalDateTime = data.timestamp.toLocalDateTime(),
        creationLocalDate = data.timestamp.toLocalDate(),
        documents = getIssuanceDocumentsFields()
    )

    else -> throw IllegalArgumentException()
}

private fun TransactionLogDetails.Issuance.getIssuerName(): String {
    return data.issuingParty.name.ifBlank {
        data.issuingParty.identifier
    }
}

private fun TransactionLogDetails.Deleting.getIssuanceDocumentsFields(): List<IssuanceDocument> {
    return listOf(
        IssuanceDocument(
            format = data.docType.orEmpty(),
            issuerMetadata = null,
            fields = emptyList(),
            name = data.displayName.orEmpty(),
            id = data.documentId.orEmpty()
        )
    )
}

private fun TransactionLogDetails.Issuance.getIssuanceDocumentsFields(): List<IssuanceDocument> {
    return if (data.documents.isNotEmpty()) {
        data.documents.map { document ->
            IssuanceDocument(
                format = document.docType.orEmpty(),
                issuerMetadata = null,
                fields = emptyList(),
                name = document.displayName.orEmpty(),
                id = document.documentId.orEmpty()
            )
        }
    } else {
        listOf(
            IssuanceDocument(
                format = data.docType.orEmpty(),
                issuerMetadata = null,
                fields = emptyList(),
                name = data.displayName.orEmpty(),
                id = data.documentId.orEmpty()
            )
        )
    }
}

private fun PresentedDocument.toIssuanceDocument(): IssuanceDocument {
    return IssuanceDocument(
        format = format.toString(),
        issuerMetadata = metadata,
        name = "",
        id = metadata?.documentConfigurationIdentifier.orEmpty(),
        fields = this.toTransactionClaimDomains()
    )
}

fun Instant.toLocalDateTime(zoneId: ZoneId = ZoneId.systemDefault()): LocalDateTime {
    return LocalDateTime.ofInstant(this, zoneId)
}

fun Instant.toLocalDate(zoneId: ZoneId = ZoneId.systemDefault()): LocalDate {
    return this.atZone(zoneId).toLocalDate()
}
