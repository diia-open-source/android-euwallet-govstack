package ua.govstack.digitalwallet.app.wallet_issuing.domain.model

data class DocumentOffer(
    val title: String,
    val issuer: String? = null
)