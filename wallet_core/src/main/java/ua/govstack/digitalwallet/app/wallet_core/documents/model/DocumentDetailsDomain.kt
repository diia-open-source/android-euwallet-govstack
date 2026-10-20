package ua.govstack.digitalwallet.app.wallet_core.documents.model

import eu.europa.ec.eudi.wallet.document.DocumentId

data class DocumentPayloadDomain(
    val docName: String,
    val docId: DocumentId,
    val domainDocFormat: DomainDocumentFormat,
    val docClaimsDomain: List<ClaimDomain>,
)

data class RequestDocumentItemUi(
    val domainPayload: DocumentPayloadDomain,
    val headerUi: ExpandableListItemUi.NestedListItem,
)

data class DocumentDetailsDomain(
    val docName: String,
    val docId: DocumentId,
    val documentIdentifier: DocumentIdentifier,
    val issuerUrl: String?,
    val documentExpirationDateFormatted: String?,
    val documentHasExpired: Boolean,
    val documentClaims: List<ClaimDomain>
)
