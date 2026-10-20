package ua.govstack.digitalwallet.app.wallet_core.documents.extensions

import eu.europa.ec.eudi.wallet.document.Document
import eu.europa.ec.eudi.wallet.document.metadata.IssuerMetadata
import ua.govstack.digitalwallet.app.wallet_core.utils.extensions.getLocalizedValue
import java.net.URI
import java.util.Locale

fun Document.localizedIssuerMetadata(locale: Locale): IssuerMetadata.IssuerDisplay? {
    return issuerMetadata?.issuerDisplay.getLocalizedValue(
        userLocale = locale,
        fallback = null,
        localeExtractor = { it.locale },
        valueExtractor = { it }
    )
}

fun Document.getIssuerUrl(): String? {
    val issuer = issuerMetadata?.credentialIssuerIdentifier ?: return null
    val host = runCatching { URI(issuer).host }.getOrNull() ?: issuer
    return "Issuer: $host"
}
