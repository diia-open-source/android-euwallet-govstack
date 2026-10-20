package ua.govstack.digitalwallet.app.wallet_core.issuance.model

import eu.europa.ec.eudi.wallet.issue.openid4vci.Offer

sealed class ResolveDocumentOfferResult {
    data class Success(val offer: Offer) : ResolveDocumentOfferResult()
    data class UntrustedIssuer(val issuerId: String?) : ResolveDocumentOfferResult()
    data class Failure(val errorMessage: String) : ResolveDocumentOfferResult()
}
