package ua.govstack.digitalwallet.app.wallet_doc_mdl.util

import androidx.compose.runtime.snapshots.SnapshotStateList
import ua.govstack.digitalwallet.app.core.models.document.Document
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.UIElementData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.addAllIfNotNull
import ua.govstack.digitalwallet.app.ui_base.components.organism.document.Localization
import ua.govstack.digitalwallet.app.ui_base.components.organism.document.toUIModel
import ua.govstack.digitalwallet.app.ui_base.components.organism.list.toUIModel
import ua.govstack.digitalwallet.app.ui_base.mappers.document.BaseFullInfoComposeMapper
import ua.govstack.digitalwallet.app.ui_base.mappers.document.DocumentComposeMapper
import ua.govstack.digitalwallet.app.wallet_doc_mdl.models.Mdl

class MdlFullInfoComposeMapper(
    docComposeMapper: DocumentComposeMapper
) : BaseFullInfoComposeMapper, DocumentComposeMapper by docComposeMapper {
    override fun mapDocToBody(document: Document, bodyData: SnapshotStateList<UIElementData>) {
        if (document is Mdl.Data) {
            bodyData.addAllIfNotNull(
                document.fullInfo?.find { it.docHeadingOrg != null }?.docHeadingOrg.toComposeDocHeadingOrg(),
                document.fullInfo?.find { it.tickerAtm != null }?.tickerAtm.toComposeTickerAtm(),
                toComposeContentTableOrg(
                    document.fullInfo?.mapNotNull { it.tableBlockTwoColumnsOrg },
                    document.fullInfo?.mapNotNull { it.tableBlockOrg },
                    tableBlockAccordionOrg = null,
                    photo = document.photo,
                    valueImage = document.signature?.image,
                ),
                document.fullInfo?.find { it.verificationCodesOrg != null }?.verificationCodesOrg?.toUIModel(
                    localization = Localization.eng
                ),
                document.fullInfo?.find { it.listItemGroupOrg != null }?.listItemGroupOrg?.toUIModel()
            )
        }
    }
}