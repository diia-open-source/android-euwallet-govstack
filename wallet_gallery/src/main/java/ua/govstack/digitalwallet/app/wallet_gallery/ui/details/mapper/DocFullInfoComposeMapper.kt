package ua.govstack.digitalwallet.app.wallet_gallery.ui.details.mapper

import androidx.compose.runtime.snapshots.SnapshotStateList
import ua.govstack.digitalwallet.app.core.models.document.Document
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.UIElementData

interface DocFullInfoComposeMapper {
    /**
     * document transformation to UIElementData and add to the bodyData list
     */
    fun mapDocToBody(document: Document, bodyData: SnapshotStateList<UIElementData>)
}