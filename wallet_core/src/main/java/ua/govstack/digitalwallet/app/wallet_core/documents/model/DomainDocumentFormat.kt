package ua.govstack.digitalwallet.app.wallet_core.documents.model

import eu.europa.ec.eudi.wallet.document.format.DocumentFormat
import eu.europa.ec.eudi.wallet.document.format.MsoMdocFormat
import eu.europa.ec.eudi.wallet.document.format.SdJwtVcFormat

sealed class DomainDocumentFormat {
    data object SdJwtVc : DomainDocumentFormat()
    data class MsoMdoc(val namespace: String) :
        DomainDocumentFormat()

    companion object {
        fun getFormat(format: DocumentFormat, namespace: String?): DomainDocumentFormat {
            return when (format) {
                is SdJwtVcFormat -> SdJwtVc
                is MsoMdocFormat -> MsoMdoc(
                    namespace = namespace.toString()
                )
            }
        }
    }
}