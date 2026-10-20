package ua.govstack.digitalwallet.app.wallet_doc_pid.util

import android.content.res.Resources
import android.os.Parcelable
import ua.govstack.digitalwallet.app.core.models.document.DocAction
import ua.govstack.digitalwallet.app.core.models.document.Document
import ua.govstack.digitalwallet.app.core.models.document.ui.DocumentsContextMenuActions
import ua.govstack.digitalwallet.app.ui_base.components.DiiaResourceIcon
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.UiIcon
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.UiText
import ua.govstack.digitalwallet.app.ui_base.components.molecule.list.ListItemMlcData
import ua.govstack.digitalwallet.app.ui_base.mappers.document.BaseDocumentActionProvider
import ua.govstack.digitalwallet.app.ui_base.mappers.document.toListItemMlcData
import ua.govstack.digitalwallet.app.wallet_doc_pid.R
import ua.govstack.digitalwallet.app.wallet_doc_pid.models.Pid

class PidContextMenuActionProvider :
    BaseDocumentActionProvider {
    override fun isDocumentProvider(document: Parcelable): Boolean {
        return document is Pid.Data
    }

    override fun listOfActions(
        docParcelable: Parcelable,
        resources: Resources
    ): List<ListItemMlcData> {
        val document = docParcelable as? Pid.Data ?: return emptyList()
        if (document.isDeferredDocument()) {
            return emptyList()
        }

        return listOf(
            DocumentsContextMenuActions.FULL_DOC.toListItemMlcData(
                label = UiText.StringResource(R.string.full_info),
                iconLeft = UiIcon.DrawableResource(DiiaResourceIcon.DOC_INFO.code)
            )
        )
    }

    override fun listOfGeneralActions(
        document: Document,
        enableStackActions: Boolean
    ): List<ListItemMlcData> {
        return listOf()
    }

    override fun listOfManualActions(
        docParcelable: Parcelable,
        availableActionsForUser: List<DocAction>?
    ): List<ListItemMlcData> {
        return listOf(
            DocumentsContextMenuActions.REMOVE_DOC.toListItemMlcData(
                label = UiText.StringResource(R.string.remove_doc_action),
                iconLeft = UiIcon.DrawableResource(DiiaResourceIcon.DELETE.code),
            )
        )
    }

    private fun Pid.Data.isDeferredDocument(): Boolean {
        return fullInfo == null
    }
}
