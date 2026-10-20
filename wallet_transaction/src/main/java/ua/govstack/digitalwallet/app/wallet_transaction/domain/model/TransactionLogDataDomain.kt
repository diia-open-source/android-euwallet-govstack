package ua.govstack.digitalwallet.app.wallet_transaction.domain.model

import eu.europa.ec.eudi.wallet.document.metadata.IssuerMetadata
import eu.europa.ec.eudi.wallet.transactionLogging.TransactionLog
import ua.govstack.digitalwallet.app.ui_base.provider.ResourceProvider
import ua.govstack.digitalwallet.app.wallet_core.utils.extensions.getLocalizedString
import ua.govstack.digitalwallet.app.wallet_core.documents.model.IssuedDocumentClaimDomain
import ua.govstack.digitalwallet.app.wallet_transaction.R
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Locale

sealed interface TransactionLogDataDomain {

    val id: String
    val name: String
    val status: TransactionLog.Status
    val creationLocalDateTime: LocalDateTime
    val creationLocalDate: LocalDate

    data class PresentationLog(
        override val id: String,
        override val name: String,
        override val status: TransactionLog.Status,
        override val creationLocalDateTime: LocalDateTime,
        override val creationLocalDate: LocalDate,
        val relyingParty: TransactionLog.RelyingParty,
        val documents: List<IssuanceDocument>,
    ) : TransactionLogDataDomain

    data class IssuanceLog(
        override val id: String,
        override val name: String,
        override val status: TransactionLog.Status,
        override val creationLocalDateTime: LocalDateTime,
        override val creationLocalDate: LocalDate,
        val documents: List<IssuanceDocument>,
    ) : TransactionLogDataDomain

    data class IssuanceDocument(
        val id: String,
        val format: String,
        val issuerMetadata: IssuerMetadata?,
        val name: String,
        val fields: List<IssuedDocumentClaimDomain>,
    )

    data class SigningLog(
        override val id: String,
        override val name: String,
        override val status: TransactionLog.Status,
        override val creationLocalDateTime: LocalDateTime,
        override val creationLocalDate: LocalDate,
    ) : TransactionLogDataDomain

    data class DeletingLog(
        override val id: String,
        override val name: String,
        override val status: TransactionLog.Status,
        override val creationLocalDateTime: LocalDateTime,
        override val creationLocalDate: LocalDate,
        val documents: List<IssuanceDocument>,
    ) : TransactionLogDataDomain

    companion object {
        fun TransactionLogDataDomain.getTransactionDocumentNames(userLocale: Locale): List<String> {
            return when (this) {
                is IssuanceLog -> {
                    documents.map { it.name }
                }

                is PresentationLog -> {
                    this.documents.mapNotNull { document ->
                        document.issuerMetadata.getLocalizedDocumentName(
                            userLocale = userLocale,
                            fallback = ""
                        ).takeIf { it.isNotBlank() }
                    }.flatMap { documentName ->
                        listOf(documentName)
                    }
                }
                is DeletingLog -> {
                     documents.map { it.name }
                }
                is SigningLog -> {
                    //TODO change this once Core supports more transaction types
                    emptyList()
                }
            }
        }

        fun TransactionLogDataDomain.getDisplayName(resourceProvider: ResourceProvider): String {
            return when(this) {
                is PresentationLog -> name.ifEmpty { resourceProvider.getString(R.string.transaction_type_issuing) }
                is DeletingLog -> resourceProvider.getString(R.string.transaction_document_deleted)
                is IssuanceLog -> name
                is SigningLog -> name
            }
        }
    }
}

fun IssuerMetadata?.getLocalizedDocumentName(
    userLocale: Locale,
    fallback: String,
): String {
    return this?.display.getLocalizedString(
        userLocale = userLocale,
        localeExtractor = { it.locale },
        stringExtractor = { it.name },
        fallback = fallback,
    )
}
