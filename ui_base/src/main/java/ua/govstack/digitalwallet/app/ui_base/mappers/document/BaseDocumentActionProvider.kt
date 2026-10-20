package ua.govstack.digitalwallet.app.ui_base.mappers.document

import android.content.res.Resources
import android.os.Parcelable
import ua.govstack.digitalwallet.app.core.models.document.DocAction
import ua.govstack.digitalwallet.app.core.models.document.Document
import ua.govstack.digitalwallet.app.ui_base.components.molecule.list.ListItemMlcData

interface BaseDocumentActionProvider {
    fun isDocumentProvider(document: Parcelable): Boolean
    fun listOfActions(
        docParcelable: Parcelable,
        resources: Resources
    ): List<ListItemMlcData>

    fun listOfGeneralActions(
        document: Document,
        enableStackActions: Boolean,
    ): List<ListItemMlcData>

    fun listOfManualActions(
        docParcelable: Parcelable,
        availableActionsForUser: List<DocAction>?
    ): List<ListItemMlcData>
}