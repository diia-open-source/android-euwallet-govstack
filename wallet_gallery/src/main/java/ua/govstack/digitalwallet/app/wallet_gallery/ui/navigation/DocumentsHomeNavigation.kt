package ua.govstack.digitalwallet.app.wallet_gallery.ui.navigation

import ua.govstack.digitalwallet.app.core.models.deeplink.DeepLinkActionStartFlow
import ua.govstack.digitalwallet.app.core.models.dialogs.TemplateDialogModel
import ua.govstack.digitalwallet.app.core.models.document.Document
import ua.govstack.digitalwallet.app.core.models.document.ManualDocs
import ua.govstack.digitalwallet.app.core.util.navigation.HomeNavigation

sealed class DocumentsHomeNavigation : HomeNavigation {

    data class ToTemplateDialog(
        val template: TemplateDialogModel,
        override var isConsumed: Boolean = false
    ) : DocumentsHomeNavigation()

    data class ToDocActions(
        val doc: Document,
        val position: Int,
        val manualDocs: ManualDocs?,
        override var isConsumed: Boolean = false
    ) : DocumentsHomeNavigation()

    data class ToCredentialList(
        override var isConsumed: Boolean = false
    ) : DocumentsHomeNavigation()

    data class ToStartNewFlow(
        val deeplink: DeepLinkActionStartFlow,
        override var isConsumed: Boolean = false
    ) : DocumentsHomeNavigation()

    data class ToDocsOrder(
        override var isConsumed: Boolean = false
    ) : DocumentsHomeNavigation()
}
