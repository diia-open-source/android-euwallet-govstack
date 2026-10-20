package ua.govstack.digitalwallet.app.core.models.deeplink

sealed class DeepLinkAction

class DeepLinkActionViewMessage(
    val needAuth: Boolean,
    val resourceId: String,
    val notificationId: String
) : DeepLinkAction()

class DeepLinkActionStartFlow(
    val flowId: String,
    val resId: String,
    val resType: String? = null,
) : DeepLinkAction()

class DeepLinkActionOpenNotify(
    val flowType: String,
    val flowSubType: String,
    val resId: String
) : DeepLinkAction()

class DeepLinkActionViewDocument(
    val notificationId: String?,
    val resourceId: String?,
    val documentType: String
) : DeepLinkAction()

object DeepLinkActionOpenMenu : DeepLinkAction()
