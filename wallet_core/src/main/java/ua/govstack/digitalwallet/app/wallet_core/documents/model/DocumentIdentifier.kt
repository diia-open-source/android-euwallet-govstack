package ua.govstack.digitalwallet.app.wallet_core.documents.model

import eu.europa.ec.eudi.wallet.document.Document
import eu.europa.ec.eudi.wallet.document.format.MsoMdocFormat
import eu.europa.ec.eudi.wallet.document.format.SdJwtVcFormat

typealias FormatType = String

sealed interface DocumentIdentifier {
    val formatType: FormatType

    data object MdocPid : DocumentIdentifier {
        override val formatType: FormatType
            get() = "eu.europa.ec.eudi.pid.1"
    }

    data object SdJwtPid : DocumentIdentifier {
        override val formatType: FormatType
            get() = "urn:eudi:pid:1"
    }

    data object MdocMdl: DocumentIdentifier{
        override val formatType: FormatType
            get() = "org.iso.18013.5.1.mDL"
    }
    
    data class OTHER(
        override val formatType: FormatType,
    ) : DocumentIdentifier
}

fun Document.toDocumentIdentifier(): DocumentIdentifier {
    val formatType = when (val f = format) {
        is MsoMdocFormat -> f.docType
        is SdJwtVcFormat -> f.vct
    }
    return createDocumentIdentifier(formatType)
}

/**
 * @return A [DocumentIdentifier] from a FormatType.
 */
fun FormatType.toDocumentIdentifier(): DocumentIdentifier = when (this.lowercase()) {
    DocumentIdentifier.MdocPid.formatType.lowercase() -> DocumentIdentifier.MdocPid
    DocumentIdentifier.SdJwtPid.formatType.lowercase() -> DocumentIdentifier.SdJwtPid
    DocumentIdentifier.MdocMdl.formatType.lowercase() -> DocumentIdentifier.MdocMdl
    else -> DocumentIdentifier.OTHER(formatType = this)
}

private fun createDocumentIdentifier(
    formatType: FormatType
): DocumentIdentifier {
    return when (formatType) {
        DocumentIdentifier.MdocPid.formatType -> DocumentIdentifier.MdocPid
        DocumentIdentifier.SdJwtPid.formatType -> DocumentIdentifier.SdJwtPid
        DocumentIdentifier.MdocMdl.formatType -> DocumentIdentifier.MdocMdl
        else -> DocumentIdentifier.OTHER(formatType = formatType)
    }
}
