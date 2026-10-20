package ua.govstack.digitalwallet.app.ui_base.mappers.document

import androidx.compose.runtime.snapshots.SnapshotStateList
import ua.govstack.digitalwallet.app.core.models.document.Document
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.UIElementData

interface BaseFullInfoComposeMapper {

    fun mapDocToBody(
        document: Document,
        bodyData: SnapshotStateList<UIElementData>
    )
}