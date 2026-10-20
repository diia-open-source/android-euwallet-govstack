package ua.govstack.digitalwallet.app.wallet_gallery.ui.actions

import ua.govstack.digitalwallet.app.core.models.document.Document
import ua.govstack.digitalwallet.app.core.models.document.ManualDocs
import ua.govstack.digitalwallet.app.core.models.document.ui.DocumentsContextMenuActions
import ua.govstack.digitalwallet.app.ui_base.components.DiiaResourceIcon
import ua.govstack.digitalwallet.app.ui_base.components.atom.button.BtnWhiteLargeAtmData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.state.UIState
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.UiIcon
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.UiText
import ua.govstack.digitalwallet.app.ui_base.components.molecule.list.ListItemMlcData
import ua.govstack.digitalwallet.app.ui_base.components.organism.list.ActivityViewOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.list.ContextIconMenuOrgData
import ua.govstack.digitalwallet.app.ui_base.mappers.document.toListItemMlcData
import ua.govstack.digitalwallet.app.wallet_doc_mdl.R
import ua.govstack.digitalwallet.app.wallet_doc_mdl.models.Mdl
import ua.govstack.digitalwallet.app.wallet_doc_pid.models.Pid
import javax.inject.Inject

class DocActionMapper @Inject constructor() {

    fun body(doc: Document, manualDocs: ManualDocs?): ActivityViewOrgData {
        val contextMenu = provideActions(doc, manualDocs)

        val button = BtnWhiteLargeAtmData(
            title = UiText.StringResource(ua.govstack.digitalwallet.app.wallet_gallery.R.string.close),
            id = "",
            interactionState = UIState.Interaction.Enabled
        )

        return ActivityViewOrgData(
            contextMenuOrg = contextMenu,
            button = button
        )
    }

    private fun provideActions(
        document: Document,
        manualDocs: ManualDocs?,
    ): ContextIconMenuOrgData {
        return ContextIconMenuOrgData(
            docActions = listOfActions(document).toMutableList(),
            manualActions = listOfManualActions(),
            generalActions = emptyList(),
            qr = null,
            ean13 = null,
            showButtons = false,
            showDividerForManualActions = manualDocs != null
        )
    }

    private fun listOfActions(
        document: Document,
    ): List<ListItemMlcData> {

        val isDeferred = when (document) {
            is Mdl.Data -> document.isDeferredDocument()
            is Pid.Data -> document.isDeferredDocument()
            else -> return emptyList()
        }

        if (isDeferred) return emptyList()

        return listOf(
            DocumentsContextMenuActions.FULL_DOC.toListItemMlcData(
                label = UiText.StringResource(R.string.full_info),
                iconLeft = UiIcon.DrawableResource(DiiaResourceIcon.DOC_INFO.code)
            )
        )
    }

    private fun listOfManualActions(): List<ListItemMlcData> {
        return listOf(
            DocumentsContextMenuActions.REMOVE_DOC.toListItemMlcData(
                label = UiText.StringResource(R.string.remove_doc_action),
                iconLeft = UiIcon.DrawableResource(DiiaResourceIcon.DELETE.code),
            )
        )
    }

    private fun Mdl.Data.isDeferredDocument(): Boolean {
        return fullInfo == null
    }

    private fun Pid.Data.isDeferredDocument(): Boolean {
        return fullInfo == null
    }
}