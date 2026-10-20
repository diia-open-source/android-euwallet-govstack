package ua.govstack.digitalwallet.app.ui_base.mappers.document

import ua.govstack.digitalwallet.app.core.models.common_compose.atm.chip.ChipStatusAtm
import ua.govstack.digitalwallet.app.core.models.common_compose.atm.text.TickerAtm
import ua.govstack.digitalwallet.app.core.models.common_compose.mlc.text.SmallEmojiPanelMlc
import ua.govstack.digitalwallet.app.core.models.common_compose.mlc.text.SubtitleLabelMlc
import ua.govstack.digitalwallet.app.core.models.common_compose.org.doc.DocButtonHeadingOrg
import ua.govstack.digitalwallet.app.core.models.common_compose.org.doc.DocHeadingOrg
import ua.govstack.digitalwallet.app.core.models.common_compose.table.tableBlockAccordionOrg.TableBlockAccordionOrg
import ua.govstack.digitalwallet.app.core.models.common_compose.table.tableBlockOrg.TableBlockOrg
import ua.govstack.digitalwallet.app.core.models.common_compose.table.tableBlockPlaneOrg.TableBlockPlaneOrg
import ua.govstack.digitalwallet.app.core.models.common_compose.table.tableBlockTwoColumnsOrg.TableBlockTwoColumnsOrg
import ua.govstack.digitalwallet.app.core.models.common_compose.table.tableBlockTwoColumnsPlaneOrg.TableBlockTwoColumnsPlaneOrg
import ua.govstack.digitalwallet.app.core.models.document.DocumentCard
import ua.govstack.digitalwallet.app.core.models.document.LocalizationType
import ua.govstack.digitalwallet.app.core.models.document.barcode.DocumentBarcodeResult
import ua.govstack.digitalwallet.app.core.models.document.docgroups.v2.DocCover
import ua.govstack.digitalwallet.app.core.models.document.docgroups.v2.QrCheckStatus
import ua.govstack.digitalwallet.app.ui_base.components.atom.text.TickerAtomData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.header.NavigationPanelMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.text.SubtitleLabelMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.tile.SmallEmojiPanelMlcData
import ua.govstack.digitalwallet.app.ui_base.components.organism.document.AddDocOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.document.ContentTableOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.document.DocHeadingOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.document.DocOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.document.DocPhotoOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.pager.DocCardFlipData
import ua.govstack.digitalwallet.app.ui_base.components.organism.pager.DocCarouselOrgData

interface DocumentComposeMapper {

    fun DocHeadingOrg?.toComposeDocHeadingOrg(): DocHeadingOrgData?

    fun TickerAtm?.toComposeTickerAtm(
        isTickerClickable: Boolean = false
    ): TickerAtomData?

    fun SubtitleLabelMlc?.toComposeSubtitleLabelMlc(): SubtitleLabelMlcData?

    fun SmallEmojiPanelMlc?.toComposeEmojiPanelMlc(): SmallEmojiPanelMlcData?

    fun toComposeNavigationPanel(): NavigationPanelMlcData?

    fun toComposeDocScanSubtitleLabel(status: QrCheckStatus): SubtitleLabelMlcData?

    fun toComposeDocOrgLoading(): DocPhotoOrgData

    fun toComposeAddDocOrg(docType: String, position: Int): AddDocOrgData

    fun toComposeDocPhoto(
        localisation: LocalizationType,
        photo: String?,
        valueImage: String?,
        isStack: Boolean,
        stackSize: Int,
        showCover: Boolean,
        cover: DocCover?,
        tableBlockOrg: List<TableBlockPlaneOrg>?,
        docHeadingOrg: DocHeadingOrg?,
        docButtonHeadingOrg: DocButtonHeadingOrg?,
        subtitleLabelMlc: SubtitleLabelMlc?,
        tableBlockTwoColumnsPlaneOrg: List<TableBlockTwoColumnsPlaneOrg>?,
        tickerAtm: TickerAtm?,
        isTickerClickable: Boolean = false,
        smallEmojiPanelMlc: SmallEmojiPanelMlc? = null
    ): DocPhotoOrgData

    fun toDocCardFlip(
        photo: String?,
        id: String?,
        position: Int,
        docType: String,
        barcodeResult: DocumentBarcodeResult?,
        localizationType: LocalizationType,
        valueImage: String?,
        isStack: Boolean,
        stackSize: Int,
        cover: DocCover?,
        tableBlockOrg: List<TableBlockPlaneOrg>?,
        docHeadingOrg: DocHeadingOrg?,
        docButtonHeadingOrg: DocButtonHeadingOrg?,
        subtitleLabelMlc: SubtitleLabelMlc?,
        tableBlockTwoColumnsPlaneOrg: List<TableBlockTwoColumnsPlaneOrg>?,
        tickerAtm: TickerAtm?,
        isTickerClickable: Boolean,
        smallEmojiPanelMlc: SmallEmojiPanelMlc? = null
    ): DocCardFlipData

    fun toDocOrg(
        id: String?,
        position: Int,
        docType: String,
        isStack: Boolean,
        stackSize: Int,
        cover: DocCover?,
        url: String,
        showCover: Boolean,
        docHeadingOrg: DocHeadingOrg?,
        docButtonHeadingOrg: DocButtonHeadingOrg?,
        chipStatusAtm: ChipStatusAtm?,
        placeHolder: Int,
        landscapeBg: Int
    ): DocOrgData

    fun toDocCarousel(
        cards: List<DocumentCard>,
        barcodeResult: DocumentBarcodeResult?
    ): DocCarouselOrgData

    fun toComposeContentTableOrg(
        tableBlockTwoColumnsOrg: List<TableBlockTwoColumnsOrg>?,
        tableBlockOrg: List<TableBlockOrg>?,
        tableBlockAccordionOrg: List<TableBlockAccordionOrg>?,
        photo: String?,
        valueImage: String?
    ): ContentTableOrgData
}
