package ua.govstack.digitalwallet.app.wallet_core.documents.model

data class ScopedDocumentDomain(
    val name: String,
    val configurationId: String,
    val credentialIssuerId: String,
    val formatType: FormatType?,
    val isPid: Boolean,
    val credentialIssuerOrder: Int
)