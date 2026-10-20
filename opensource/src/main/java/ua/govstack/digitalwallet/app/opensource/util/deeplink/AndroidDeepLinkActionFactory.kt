package ua.govstack.digitalwallet.app.opensource.util.deeplink

import android.net.Uri
import androidx.core.net.toUri
import ua.govstack.digitalwallet.app.core.models.deeplink.DeepLinkAction
import ua.govstack.digitalwallet.app.core.models.deeplink.DeepLinkActionOpenNotify
import ua.govstack.digitalwallet.app.core.models.deeplink.DeepLinkActionStartFlow
import ua.govstack.digitalwallet.app.core.models.deeplink.DeepLinkActionViewMessage
import ua.govstack.digitalwallet.app.core.util.deeplink.DeepLinkActionFactory
import ua.govstack.digitalwallet.app.core.util.deeplink.DeepLinkRoutes
import ua.govstack.digitalwallet.app.diia_storage.store.Preferences
import javax.inject.Inject

class AndroidDeepLinkActionFactory @Inject constructor() :
    DeepLinkActionFactory {

    override fun buildDeepLinkAction(path: String): DeepLinkAction {
        val uri = path.toUri()
        val scheme = uri.scheme
        val normalizedPath = path.normalizedPath(uri)

        if (scheme in DeepLinkRoutes.credentialOfferSchemes) {
            return DeepLinkActionStartFlow(
                flowId = scheme.orEmpty(),
                resId = path,
            )
        }

        if (scheme == DeepLinkRoutes.CREDENTIAL_OFFER_RESUME || path.contains(DeepLinkRoutes.CREDENTIAL_OFFER_RESUME)) {
            return DeepLinkActionOpenNotify(
                flowType = DeepLinkRoutes.CREDENTIAL_OFFER_RESUME,
                resId = path,
                flowSubType = "",
            )
        }

        if (scheme in DeepLinkRoutes.presentationFlowIds) {
            return DeepLinkActionStartFlow(
                flowId = scheme.orEmpty(),
                resId = path
            )
        }

        val matchingOfferScheme = DeepLinkRoutes.credentialOfferSchemes.firstOrNull(path::contains)
        if (matchingOfferScheme != null) {
            return DeepLinkActionStartFlow(
                flowId = matchingOfferScheme,
                resId = path,
            )
        }

        val matchingPresentationFlow = DeepLinkRoutes.presentationFlowIds.firstOrNull(path::contains)
        if (matchingPresentationFlow != null) {
            return DeepLinkActionStartFlow(
                flowId = matchingPresentationFlow,
                resId = path
            )
        }

        if (normalizedPath.startsWith(DeepLinkRoutes.MESSAGE)) {
            val checkDocParams = normalizedPath.split(SPLIT)
            if (checkDocParams.size > 4) {
                return DeepLinkActionViewMessage(
                    needAuth = checkDocParams[2].toBoolean(),
                    notificationId = checkDocParams[3],
                    resourceId = checkDocParams[4],
                )
            }
        }

        if (normalizedPath.startsWith(DeepLinkRoutes.OPEN_PULL_NOTIFICATION)) {
            val params = normalizedPath.split(SPLIT)
            if (params.size > 2) {
                val flowType = params[SECOND_DEEP_LINK_COLUMN]

                val flowSubType = if (params.size > 3) {
                    params[THIRD_DEEP_LINK_COLUMN]
                } else {
                    ""
                }

                val resId = if (params.size > 4) {
                    params[FOURTH_DEEP_LINK_COLUMN]
                } else {
                    Preferences.DEF
                }
                return DeepLinkActionOpenNotify(
                    flowType = flowType,
                    flowSubType = flowSubType,
                    resId = resId
                )
            }
        }

        val params = normalizedPath.split(SPLIT)
        var flowId = DeepLinkRoutes.HOME
        var resId = Preferences.DEF
        if (params.size > 1) {
            flowId = params[FIRST_DEEP_LINK_COLUMN]
            resId = if (params.size > 3) {
                params[THIRD_DEEP_LINK_COLUMN]
            } else if (params.size > 2) {
                params[SECOND_DEEP_LINK_COLUMN]
            } else Preferences.DEF
        }
        return DeepLinkActionStartFlow(
            flowId,
            resId
        )
    }

    companion object {
        private const val SPLIT = "/"
        private const val FIRST_DEEP_LINK_COLUMN = 1
        private const val SECOND_DEEP_LINK_COLUMN = 2
        private const val THIRD_DEEP_LINK_COLUMN = 3
        private const val FOURTH_DEEP_LINK_COLUMN = 4
    }
}

private fun String.normalizedPath(uri: Uri): String {
    return when {
        startsWith("/") -> this
        uri.scheme == "http" || uri.scheme == "https" -> uri.path.orEmpty()
        else -> this
    }
}
