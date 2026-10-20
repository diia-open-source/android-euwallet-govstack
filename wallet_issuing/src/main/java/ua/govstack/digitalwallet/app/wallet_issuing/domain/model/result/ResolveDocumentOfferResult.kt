package ua.govstack.digitalwallet.app.wallet_issuing.domain.model.result

import ua.govstack.digitalwallet.app.wallet_issuing.domain.model.DocumentOffer
import java.net.URI

sealed class ResolveDocumentOfferResult {
    data class Success(
        val documents: List<DocumentOffer>,
        val issuerName: String,
        val issuerLogo: URI?,
        val txCodeLength: Int?
    ) : ResolveDocumentOfferResult()

    data class UntrustedIssuer(
        val issuerId: String?,
    ) : ResolveDocumentOfferResult()

    data class NoDocument(
        val issuerName: String,
        val issuerLogo: URI?,
    ) : ResolveDocumentOfferResult()

    data class Failure(val errorMessage: String) : ResolveDocumentOfferResult()

    data object PIDRequired : ResolveDocumentOfferResult()
}
