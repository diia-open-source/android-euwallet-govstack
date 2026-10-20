package ua.govstack.digitalwallet.app.wallet_gallery.domain.transformer

import eu.europa.ec.eudi.wallet.document.UnsignedDocument
import ua.govstack.digitalwallet.app.core.models.common_compose.atm.chip.ChipStatusAtm
import ua.govstack.digitalwallet.app.core.models.common_compose.atm.chip.Type
import ua.govstack.digitalwallet.app.core.models.common_compose.atm.icon.IconAtm
import ua.govstack.digitalwallet.app.core.models.common_compose.general.Action
import ua.govstack.digitalwallet.app.core.models.common_compose.org.doc.DocButtonHeadingOrg
import ua.govstack.digitalwallet.app.core.models.common_compose.org.doc.DocHeadingOrg
import ua.govstack.digitalwallet.app.core.models.common_compose.table.HeadingWithSubtitlesMlc
import ua.govstack.digitalwallet.app.core.models.common_compose.table.tableBlockTwoColumnsPlaneOrg.TableBlockTwoColumnsPlaneOrg
import ua.govstack.digitalwallet.app.core.models.document.LocalizationType
import ua.govstack.digitalwallet.app.core.models.document.docgroups.v2.DataForDisplayingInOrderConfigurations
import ua.govstack.digitalwallet.app.core.models.document.docgroups.v2.EN
import ua.govstack.digitalwallet.app.core.models.document.docgroups.v2.FrontCard
import ua.govstack.digitalwallet.app.ui_base.components.DiiaResourceIcon
import ua.govstack.digitalwallet.app.ui_base.provider.ResourceProvider
import ua.govstack.digitalwallet.app.wallet_core.R
import ua.govstack.digitalwallet.app.wallet_core.documents.extensions.localizedIssuerMetadata
import ua.govstack.digitalwallet.app.wallet_core.utils.formatInstant
import ua.govstack.digitalwallet.app.wallet_core.documents.model.DocumentStatus
import ua.govstack.digitalwallet.app.wallet_doc_mdl.models.Mdl
import ua.govstack.digitalwallet.app.wallet_doc_pid.models.Pid
import ua.govstack.digitalwallet.app.wallet_gallery.domain.transformer.TableItemFactory.createTableItemVertical
import java.util.Locale

class DeferredDocumentTransformer(
    private val resourceProvider: ResourceProvider
) {

    fun transformToPid(document: UnsignedDocument, order: Int): Pid =
        Pid(
            data = listOf(
                Pid.Data(
                    id = document.id,
                    dataForDisplayingInOrderConfigurations = createDisplayConfig(
                        id = "pid",
                        docName = document.name
                    ),
                    docData = Pid.Data.DocData(
                        docName = document.name,
                        documentCbor = null,
                        fullName = null
                    ),
                    docNumber = null,
                    docStatus = DocumentStatus.STATUS_ACTIVE,
                    content = null,
                    frontCard = createFrontCard(
                        document = document,
                        subtype = "pid"
                    ),
                    fullInfo = null,
                    localization = LocalizationType.eng,
                    shareLocalization = LocalizationType.eng,
                    expirationDate = "",
                    order = order,
                    dataForDisplayingAsListItem = null
                )
            )
        )

    fun transformToMdl(document: UnsignedDocument, order: Int): Mdl =
        Mdl(
            data = listOf(
                Mdl.Data(
                    id = document.id,
                    dataForDisplayingInOrderConfigurations = createDisplayConfig(
                        id = "mdl",
                        docName = document.name
                    ),
                    docData = Mdl.Data.DocData(
                        docName = document.name,
                        documentCbor = null
                    ),
                    docNumber = null,
                    docStatus = DocumentStatus.STATUS_ACTIVE,
                    content = null,
                    frontCard = createFrontCard(
                        document = document,
                        subtype = "mdl"
                    ),
                    fullInfo = null,
                    localization = LocalizationType.eng,
                    shareLocalization = LocalizationType.eng,
                    expirationDate = "",
                    order = order,
                    dataForDisplayingAsListItem = null
                )
            )
        )

    private fun createFrontCard(
        document: UnsignedDocument,
        subtype: String
    ): FrontCard {
        val localizedIssuerName = document.localizedIssuerMetadata(Locale.getDefault())?.name
        val pendingLabel = resourceProvider.getString(R.string.dashboard_document_deferred_pending)

        val infoItems = buildList {
            localizedIssuerName?.takeIf { it.isNotBlank() }?.let { issuerName ->
                add(
                    createTableItemVertical(
                        componentId = "issuer_name",
                        displayTitle = resourceProvider.getString(R.string.issuance_document_offer_default_issuer_name),
                        value = document.issuerMetadata?.credentialIssuerIdentifier ?: issuerName
                    )
                )
            }

            add(
                createTableItemVertical(
                    componentId = "requested_at",
                    displayTitle = resourceProvider.getString(R.string.issuance_add_document_deferred_success_title),
                    value = document.createdAt.formatInstant()
                )
            )
            
            //example time to issue
            add(
                createTableItemVertical(
                    componentId = "time_to_issue",
                    displayTitle = resourceProvider.getString(R.string.issuance_add_document_deferred_time_to_issue_title),
                    value = resourceProvider.getString(R.string.issuance_add_document_deferred_time_to_issue_label),
                )
            )
        }

        return FrontCard(
            en = listOf(
                EN(
                    docHeadingOrg = DocHeadingOrg(
                        componentId = "heading_front_card",
                        headingWithSubtitlesMlc = HeadingWithSubtitlesMlc(
                            componentId = "deferred_heading_front_card",
                            value = document.name,
                            subtitles = localizedIssuerName?.let { listOf(it) },
                            maxLines = 2,
                            isAutoSized = false
                        )
                    ),
                    tableBlockTwoColumnsPlaneOrg = TableBlockTwoColumnsPlaneOrg(
                        componentId = "deferred_info_front_card",
                        items = infoItems,
                        photo = null,
                        headingWithSubtitlesMlc = null
                    ),
                    tickerAtm = null,
                    smallEmojiPanelMlc = null,
                    subtitleLabelMlc = null,
                    chipStatusAtm = ChipStatusAtm(
                        componentId = "deferred_status",
                        code = Type.PENDING.id,
                        name = pendingLabel,
                        type = Type.PENDING.id
                    ),
                    docButtonHeadingOrg = DocButtonHeadingOrg(
                        componentId = "button_heading_front_card",
                        headingWithSubtitlesMlc = HeadingWithSubtitlesMlc(
                            componentId = "deferred_button_heading_front_card",
                            value = pendingLabel,
                            subtitles = null,
                            maxLines = 2,
                            isAutoSized = false
                        ),
                        iconAtm = IconAtm(
                            componentId = "ellipse_menu",
                            code = DiiaResourceIcon.ELLIPSE_KEBAB.code,
                            accessibilityDescription = "",
                            action = Action(
                                type = "ellipseMenu",
                                subtype = subtype,
                                resource = null,
                                subresource = null
                            )
                        )
                    )
                )
            ),
            ua = null
        )
    }

    private fun createDisplayConfig(
        id: String,
        docName: String
    ) = DataForDisplayingInOrderConfigurations(
        id = "dataForDisplayingInOrderConfigurations_deferred_$id",
        label = docName,
        description = resourceProvider.getString(R.string.dashboard_document_deferred_pending)
    )
}
