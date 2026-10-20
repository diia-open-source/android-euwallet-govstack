package ua.govstack.digitalwallet.app.wallet_core.documents.model

import eu.europa.ec.eudi.wallet.document.ElementIdentifier

sealed class ClaimDomain {
    abstract val key: ElementIdentifier
    abstract val displayTitle: String
    abstract val path: ClaimPathDomain

    data class Group(
        override val key: ElementIdentifier,
        override val displayTitle: String,
        override val path: ClaimPathDomain,
        val items: List<ClaimDomain>,
    ) : ClaimDomain()

    data class Primitive(
        override val key: ElementIdentifier,
        override val displayTitle: String,
        override val path: ClaimPathDomain,
        val value: String,
        val isRequired: Boolean,
    ) : ClaimDomain()

}