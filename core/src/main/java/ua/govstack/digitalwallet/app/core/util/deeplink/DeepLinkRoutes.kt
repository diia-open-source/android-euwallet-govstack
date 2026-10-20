package ua.govstack.digitalwallet.app.core.util.deeplink

object DeepLinkRoutes {
    const val HOME = "/home/"
    const val MESSAGE = "/message/"
    const val OPEN_PULL_NOTIFICATION = "/open-pull-notification"
    const val AUTH_PATH_PREFIX = "/auth"

    const val CREDENTIAL_OFFER = "openid-credential-offer"
    const val CREDENTIAL_OFFER_HAIP_VCI = "haip-vci"
    const val CREDENTIAL_OFFER_RESUME = "eu.europa.ec.euidi"

    const val SHARING_EUDI_OPENID4VP = "eudi-openid4vp"
    const val SHARING_MDOC_OPENID4VP = "mdoc-openid4vp"
    const val SHARING_OPENID4VP = "openid4vp"
    const val SHARING_HAIP_VP = "haip-vp"
    const val SHARING_HAIP = "haip"

    const val FLOW_PROXIMITY = "e_wallet_proximity"
    const val FLOW_INFO = "e_wallet_info"

    val credentialOfferSchemes = listOf(
        CREDENTIAL_OFFER,
        CREDENTIAL_OFFER_HAIP_VCI
    )

    val manifestWalletSchemes = listOf(
        SHARING_EUDI_OPENID4VP,
        SHARING_MDOC_OPENID4VP,
        SHARING_OPENID4VP,
        CREDENTIAL_OFFER,
        CREDENTIAL_OFFER_HAIP_VCI,
        CREDENTIAL_OFFER_RESUME,
        SHARING_HAIP_VP
    )

    val externalPresentationSchemes = listOf(
        SHARING_EUDI_OPENID4VP,
        SHARING_MDOC_OPENID4VP,
        SHARING_OPENID4VP,
        SHARING_HAIP_VP
    )

    val presentationFlowIds = externalPresentationSchemes + listOf(
        SHARING_HAIP,
        FLOW_PROXIMITY
    )

    val qrMarkers = credentialOfferSchemes +
        listOf(CREDENTIAL_OFFER_RESUME) +
        externalPresentationSchemes +
        listOf(SHARING_HAIP)

    val openId4VpSchemes = listOf(
        SHARING_HAIP,
        SHARING_OPENID4VP,
        SHARING_HAIP_VP,
        SHARING_EUDI_OPENID4VP,
        SHARING_MDOC_OPENID4VP
    )

    fun isWalletIntent(raw: String): Boolean = manifestWalletSchemes.any(raw::contains)

    fun isRegularAppLink(path: String?): Boolean =
        !path.isNullOrEmpty() && !path.startsWith(AUTH_PATH_PREFIX)

    fun isCredentialOfferLink(raw: String): Boolean = credentialOfferSchemes.any(raw::startsWith)
}
