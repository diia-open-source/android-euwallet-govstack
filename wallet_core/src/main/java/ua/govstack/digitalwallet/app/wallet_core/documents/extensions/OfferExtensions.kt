package ua.govstack.digitalwallet.app.wallet_core.documents.extensions

import eu.europa.ec.eudi.wallet.document.format.MsoMdocFormat
import eu.europa.ec.eudi.wallet.document.format.SdJwtVcFormat
import eu.europa.ec.eudi.wallet.issue.openid4vci.Offer
import ua.govstack.digitalwallet.app.wallet_core.documents.model.DocumentIdentifier
import ua.govstack.digitalwallet.app.wallet_core.documents.model.toDocumentIdentifier

val Offer.OfferedDocument.documentIdentifier: DocumentIdentifier?
    get() = when (val format = documentFormat) {
        is MsoMdocFormat -> format.docType.toDocumentIdentifier()
        is SdJwtVcFormat -> format.vct.toDocumentIdentifier()
        null -> null
    }
