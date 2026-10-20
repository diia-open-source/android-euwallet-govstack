package ua.govstack.digitalwallet.app.wallet_issuing.data.transformer

import eu.europa.ec.eudi.wallet.document.format.MsoMdocFormat
import eu.europa.ec.eudi.wallet.document.format.SdJwtVcFormat
import eu.europa.ec.eudi.wallet.issue.openid4vci.Offer
import ua.govstack.digitalwallet.app.wallet_core.utils.extensions.getLocalizedString
import ua.govstack.digitalwallet.app.wallet_core.utils.extensions.getLocalizedValue
import ua.govstack.digitalwallet.app.wallet_core.documents.model.DocumentIdentifier
import ua.govstack.digitalwallet.app.wallet_core.documents.model.toDocumentIdentifier
import java.net.URI
import java.util.Locale

fun Offer.getIssuerName(locale: Locale): String {
    return issuerMetadata.display.getLocalizedString(
        userLocale = locale,
        localeExtractor = { it.locale },
        stringExtractor = { it.name },
        fallback = issuerMetadata.credentialIssuerIdentifier.value.value.host
    )
}

fun Offer.getIssuerLogo(locale: Locale): URI? {
    return issuerMetadata.display.getLocalizedValue(
        userLocale = locale,
        localeExtractor = { it.locale },
        valueExtractor = { it.logo?.uri },
        fallback = null
    )
}

val Offer.OfferedDocument.documentIdentifier: DocumentIdentifier?
    get() = when (val format = documentFormat) {
        is MsoMdocFormat -> format.docType.toDocumentIdentifier()
        is SdJwtVcFormat -> format.vct.toDocumentIdentifier()
        null -> null
    }

fun Offer.OfferedDocument.getName(locale: Locale): String? {
    return configuration.credentialMetadata?.display.getLocalizedValue(
        userLocale = locale,
        localeExtractor = { it.locale },
        valueExtractor = { it.name },
        fallback = when (val format = documentFormat) {
            is MsoMdocFormat -> format.docType
            is SdJwtVcFormat -> format.vct
            null -> null
        }
    )
}

fun Offer.getIssuerUrl(): String {
    return issuerMetadata
        .credentialIssuerIdentifier
        .value
        .value
        .host
}
