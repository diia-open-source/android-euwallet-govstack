package ua.govstack.digitalwallet.app.wallet_core.documents.extensions

import eu.europa.ec.eudi.iso18013.transfer.response.DocItem
import eu.europa.ec.eudi.iso18013.transfer.response.device.MsoMdocItem
import eu.europa.ec.eudi.wallet.transfer.openId4vp.SdJwtVcItem
import ua.govstack.digitalwallet.app.wallet_core.documents.model.ClaimPathDomain
import ua.govstack.digitalwallet.app.wallet_core.documents.model.ClaimPathDomain.Companion.toClaimPathDomain

fun DocItem.toClaimPath(): ClaimPathDomain {
    return when (this) {
        is MsoMdocItem -> listOf(this.elementIdentifier)
        is SdJwtVcItem -> this.path
        else -> emptyList()
    }.toClaimPathDomain()
}