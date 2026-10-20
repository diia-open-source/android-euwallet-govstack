package ua.govstack.digitalwallet.app.wallet_gallery.ui.details.mapper

import androidx.compose.runtime.snapshots.SnapshotStateList
import ua.govstack.digitalwallet.app.core.models.document.Document
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.UIElementData
import ua.govstack.digitalwallet.app.ui_base.mappers.document.BaseFullInfoComposeMapper
import javax.inject.Inject

class DocFullInfoComposeMapperImpl @Inject constructor(val mappers: List<@JvmSuppressWildcards BaseFullInfoComposeMapper> ) :
    DocFullInfoComposeMapper {
    override fun mapDocToBody(
        document: Document,
        bodyData: SnapshotStateList<UIElementData>
    ) {
        mappers.forEach {
            it.mapDocToBody(document, bodyData)
        }
    }
}