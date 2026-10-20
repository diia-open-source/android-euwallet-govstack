package ua.govstack.digitalwallet.app.opensource.ui.compose.mappers

import androidx.compose.runtime.toMutableStateList
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
import ua.govstack.digitalwallet.app.core.network.Http
import ua.govstack.digitalwallet.app.opensource.R
import ua.govstack.digitalwallet.app.ui_base.components.atom.button.ButtonStrokeAdditionalAtomData
import ua.govstack.digitalwallet.app.ui_base.components.atom.icon.IconAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.icon.SmallIconAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.status.toUiModel
import ua.govstack.digitalwallet.app.ui_base.components.atom.text.TickerAtomData
import ua.govstack.digitalwallet.app.ui_base.components.atom.text.TickerType
import ua.govstack.digitalwallet.app.ui_base.components.atom.text.TickerUsage
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIActionKeysCompose
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.state.UIState
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.UiIcon
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.UiText
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.toDynamicString
import ua.govstack.digitalwallet.app.ui_base.components.molecule.card.WhiteMenuCardMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.doc.DocCoverMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.doc.DocNumberCopyWhiteMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.doc.StackMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.doc.toUiModel
import ua.govstack.digitalwallet.app.ui_base.components.molecule.header.NavigationPanelMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.text.HeadingWithSubtitlesWhiteMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.text.SubtitleLabelMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.text.toUiModel
import ua.govstack.digitalwallet.app.ui_base.components.molecule.tile.SmallEmojiPanelMlcData
import ua.govstack.digitalwallet.app.ui_base.components.organism.document.AddDocOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.document.ContentTableOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.document.DocButtonHeadingOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.document.DocHeadingOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.document.DocOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.document.DocPhotoOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.pager.DocCardFlipData
import ua.govstack.digitalwallet.app.ui_base.components.organism.pager.DocCarouselOrgData
import ua.govstack.digitalwallet.app.ui_base.components.theme.White
import ua.govstack.digitalwallet.app.ui_base.mappers.document.DocumentComposeMapper
import ua.govstack.digitalwallet.app.ui_base.util.toDataActionWrapper
import ua.govstack.digitalwallet.app.ui_base.util.toUiModel
import ua.govstack.digitalwallet.app.wallet_core.documents.model.DocumentStatus
import ua.govstack.digitalwallet.app.wallet_doc_mdl.models.Mdl
import ua.govstack.digitalwallet.app.wallet_doc_pid.models.Pid
import ua.govstack.digitalwallet.app.wallet_gallery.domain.model.DocManualOptions
import ua.govstack.digitalwallet.app.wallet_gallery.ui.actions.DocumentActions
import javax.inject.Inject

class DocumentComposeMapperImpl @Inject constructor() : DocumentComposeMapper {

    override fun SmallEmojiPanelMlc?.toComposeEmojiPanelMlc(): SmallEmojiPanelMlcData? {
        if (this == null) return null
        val text = label ?: return null
        val code = icon?.code ?: return null
        return SmallEmojiPanelMlcData(
            text = UiText.DynamicString(text),
            icon = UiIcon.DrawableResource(code = code)
        )
    }

    override fun SubtitleLabelMlc?.toComposeSubtitleLabelMlc(): SubtitleLabelMlcData? {
        val text = this?.label ?: return null
        return SubtitleLabelMlcData(
            label = UiText.DynamicString(text),
            componentId = componentId?.let { UiText.DynamicString(it) }
        )
    }

    override fun toComposeNavigationPanel(): NavigationPanelMlcData {
        return NavigationPanelMlcData(
            title = UiText.DynamicString("QR code scanning"),
            tintColor = White,
            isContextMenuExist = false,
        )
    }

    override fun toComposeDocScanSubtitleLabel(status: QrCheckStatus): SubtitleLabelMlcData {
        return when (status) {
            else -> {
                SubtitleLabelMlcData(
                    label = UiText.StringResource(R.string.qr_check_subtitle_checking_docs)
                )
            }
        }
    }

    override fun toComposeDocOrgLoading(): DocPhotoOrgData {
        return DocPhotoOrgData(
            docHeading = null,
            docButtonHeading = null,
            tickerAtomData = null,
            tableBlockOrgData = null
        )
    }

    override fun toComposeAddDocOrg(
        docType: String,
        position: Int
    ): AddDocOrgData {
        return AddDocOrgData(
            addDoc = WhiteMenuCardMlcData(
                title = "Add a document",
                icon = UiText.StringResource(R.drawable.ic_add)
            ),
            changePosition = WhiteMenuCardMlcData(
                actionKey = UIActionKeysCompose.CHANGE_DOC_ORDER,
                title = "Change the order of\ndocuments",
                icon = UiText.StringResource(R.drawable.ic_doc_reorder)
            ),
            docType = docType,
            position = position
        )
    }

    private fun generateDocCoverData(
        docStatus: Int,
        verificationCodesCount: Int
    ): DocCover? {
        return when (docStatus) {

            Http.HTTP_1010 -> {
                DocCover(
                    title = R.string.dl_no_photo_title,
                    description = R.string.dl_to_e_queue,
                    buttonTitle = "Register in queue",
                    actionKey = DocumentActions.DOC_ACTION_OPEN_WEB_VIEW,
                    verificationCodesCount = verificationCodesCount,
                    data = null
                )
            }

            Http.HTTP_1011 -> {
                DocCover(
                    title = R.string.dl_outdated_title,
                    description = R.string.dl_outdated_description,
                    buttonTitle = "Register in queue",
                    actionKey = DocumentActions.DOC_ACTION_OPEN_WEB_VIEW,
                    verificationCodesCount = verificationCodesCount,
                    data = null
                )
            }

            Http.HTTP_1012 -> {
                DocCover(
                    title = R.string.dl_need_verify_title,
                    description = R.string.dl_need_verify_queue,
                    buttonTitle = "Find a service center",
                    actionKey = DocumentActions.DOC_ACTION_OPEN_WEB_VIEW,
                    verificationCodesCount = verificationCodesCount,
                    data = null
                )
            }


            Http.HTTP_1016 -> {
                DocCover(
                    title = R.string.dl_expiration_title,
                    description = R.string.dl_expiration_description,
                    buttonTitle = "Delete document",
                    actionKey = DocumentActions.DOC_COVER_ACTION_DELETE,
                    verificationCodesCount = verificationCodesCount
                )
            }

            DocumentStatus.STATUS_REVOKED -> {
                DocCover(
                    title = R.string.dl_revoked_title,
                    description = R.string.dl_revoked_description,
                    buttonTitle = "Delete",
                    actionKey = DocumentActions.DOC_COVER_ACTION_DELETE,
                    verificationCodesCount = verificationCodesCount
                )
            }


            else -> null
        }
    }

    private fun DocCover.toComposeDocCoverMlc(isStack: Boolean, stackSize: Int): DocCoverMlcData {
        return DocCoverMlcData(
            title = UiText.StringResource(this.title),
            text = UiText.StringResource(this.description),
            button = if (this.buttonTitle != null) ButtonStrokeAdditionalAtomData(
                actionKey = this.actionKey,
                title = UiText.DynamicString(this.buttonTitle ?: ""),
                id = this.data.orEmpty(),
                interactionState = UIState.Interaction.Enabled
            ) else null,
            stackMlcData = if (isStack) StackMlcData(
                amount = stackSize,
                smallIconAtmData = SmallIconAtmData(code = "stack")
            ) else null,
            isStack = isStack,
            size = stackSize
        )
    }


    override fun TickerAtm?.toComposeTickerAtm(
        isTickerClickable: Boolean
    ): TickerAtomData? {
        return this?.let {
            TickerAtomData(
                componentId = it.componentId.orEmpty(),
                title = it.value,
                usage = TickerUsage.GRAND,
                type = when (this.type) {
                    TickerAtm.TickerType.warning -> TickerType.WARNING
                    TickerAtm.TickerType.positive -> TickerType.POSITIVE
                    TickerAtm.TickerType.neutral -> TickerType.NEUTRAL
                    TickerAtm.TickerType.informative -> TickerType.INFORMATIVE
                    TickerAtm.TickerType.negative -> TickerType.NEGATIVE
                    TickerAtm.TickerType.pink -> TickerType.PINK
                    TickerAtm.TickerType.rainbow -> TickerType.RAINBOW
                    TickerAtm.TickerType.blue -> TickerType.BLUE
                    TickerAtm.TickerType.cyan -> TickerType.CYAN
                },
                clickable = isTickerClickable
            )
        }
    }

    private fun DocButtonHeadingOrg?.toComposeDocButtonHeadingOrg(
        isStack: Boolean,
        stackSize: Int
    ): DocButtonHeadingOrgData? {
        return this?.let {
            DocButtonHeadingOrgData(
                componentId = this.componentId.orEmpty(),
                heading = if (this.headingWithSubtitlesMlc != null) this.headingWithSubtitlesMlc!!.toUiModel() else null,
                headingWhite = if (this.headingWithSubtitleWhiteMlc != null) HeadingWithSubtitlesWhiteMlcData(
                    value = this.headingWithSubtitleWhiteMlc?.value,
                    subtitles = if (!this.headingWithSubtitleWhiteMlc?.subtitles.isNullOrEmpty()) this.headingWithSubtitleWhiteMlc?.subtitles else null
                ) else null,
                docNumberCopy = this.docNumberCopyMlc?.toUiModel(),
                docNumberCopyWhite = this.docNumberCopyWhiteMlc?.let {
                    DocNumberCopyWhiteMlcData(
                        value = this.docNumberCopyWhiteMlc?.value.toDynamicString(),
                        icon = this.docNumberCopyWhiteMlc?.icon?.let {
                            IconAtmData(
                                code = it.code,
                                action = it.action?.toDataActionWrapper()
                            )
                        })
                },
                iconAtmData = if (this.iconAtm != null) iconAtm?.toUiModel() else null,
                stackMlcData =
                if (this.docNumberCopyWhiteMlc != null || this.headingWithSubtitleWhiteMlc != null)
                    StackMlcData(
                        amount = stackSize,
                        smallIconAtmData = SmallIconAtmData(code = "stackWhite"),
                        isWhite = true
                    ) else
                    StackMlcData(
                        amount = stackSize,
                        smallIconAtmData = SmallIconAtmData(code = "stack")
                    ),
                isStack = isStack,
                size = stackSize
            )
        }
    }

    override fun DocHeadingOrg?.toComposeDocHeadingOrg(): DocHeadingOrgData? {
        return this?.let {
            DocHeadingOrgData(
                componentId = this.componentId.orEmpty(),
                heading = if (this.headingWithSubtitlesMlc != null) this.headingWithSubtitlesMlc!!.toUiModel() else null,
                headingWhite = if (this.headingWithSubtitleWhiteMlc != null) HeadingWithSubtitlesWhiteMlcData(
                    value = this.headingWithSubtitleWhiteMlc?.value,
                    subtitles = if (!this.headingWithSubtitleWhiteMlc?.subtitles.isNullOrEmpty()) this.headingWithSubtitleWhiteMlc?.subtitles else null
                ) else null,
                docNumber = this.docNumberCopyMlc?.toUiModel(),
                docNumberCopyWhite = this.docNumberCopyWhiteMlc?.let {
                    DocNumberCopyWhiteMlcData(
                        value = this.docNumberCopyWhiteMlc?.value.toDynamicString(),
                        icon = this.docNumberCopyWhiteMlc?.icon?.let {
                            IconAtmData(
                                code = it.code,
                                action = it.action?.toDataActionWrapper()
                            )
                        })
                }
            )
        }
    }

    override fun toComposeDocPhoto(
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
        isTickerClickable: Boolean,
        smallEmojiPanelMlc: SmallEmojiPanelMlc?
    ): DocPhotoOrgData {
        val doc = if (!showCover) {

            DocPhotoOrgData(
                docHeading = docHeadingOrg.toComposeDocHeadingOrg(),
                tickerAtomData = tickerAtm.toComposeTickerAtm(isTickerClickable),
                docButtonHeading = docButtonHeadingOrg.toComposeDocButtonHeadingOrg(
                    isStack,
                    stackSize
                ),
                tableBlockTwoColumns = tableBlockTwoColumnsPlaneOrg?.mapNotNull {
                    it.toComposeTableBlockTwoColumnsPlaneOrg(photo, valueImage)
                },
                tableBlockOrgData = tableBlockOrg?.mapNotNull {
                    it.toComposeTableBlockPlaneOrgData(valueImage)
                },
                subtitleLabelMlc = subtitleLabelMlc.toComposeSubtitleLabelMlc(),
                smallEmojiPanelMlcData = smallEmojiPanelMlc.toComposeEmojiPanelMlc()
            )
        } else {
            DocPhotoOrgData(
                docHeading = docHeadingOrg.toComposeDocHeadingOrg(),
                tickerAtomData = tickerAtm.toComposeTickerAtm(),
                docButtonHeading = docButtonHeadingOrg.toComposeDocButtonHeadingOrg(
                    isStack,
                    stackSize
                ),
                tableBlockTwoColumns = tableBlockTwoColumnsPlaneOrg?.mapNotNull {
                    //NO photo for doc with cover
                    it.toComposeTableBlockTwoColumnsPlaneOrg(null, null)
                },
                tableBlockOrgData = tableBlockOrg?.mapNotNull {
                    it.toComposeTableBlockPlaneOrgData(valueImage)
                },
                docCover = cover?.toComposeDocCoverMlc(isStack, stackSize),
                subtitleLabelMlc = subtitleLabelMlc.toComposeSubtitleLabelMlc(),
                smallEmojiPanelMlcData = smallEmojiPanelMlc.toComposeEmojiPanelMlc()
            )
        }
        return doc
    }

    override fun toDocCardFlip(
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
        smallEmojiPanelMlc: SmallEmojiPanelMlc?
    ): DocCardFlipData {
        return DocCardFlipData(
            id = id,
            docType = docType,
            front = toComposeDocPhoto(
                localizationType,
                photo,
                valueImage,
                isStack,
                stackSize,
                cover != null,
                cover,
                tableBlockOrg,
                docHeadingOrg,
                docButtonHeadingOrg,
                subtitleLabelMlc,
                tableBlockTwoColumnsPlaneOrg,
                tickerAtm,
                isTickerClickable,
                smallEmojiPanelMlc
            ),
            back = null,
            position = position,
            enableFlip = cover == null
        )
    }

    override fun toDocOrg(
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
    ): DocOrgData {

        val docData = if (!showCover) {
            DocOrgData(
                id = docHeadingOrg?.docNumberCopyWhiteMlc?.value,
                imageUrl = url,
                position = position,
                docType = docType,
                docHeading = docHeadingOrg.toComposeDocHeadingOrg(),
                docButtonHeading = docButtonHeadingOrg.toComposeDocButtonHeadingOrg(
                    isStack,
                    stackSize
                ),
                chipStatusAtmData = chipStatusAtm?.toUiModel(),
                placeHolder = placeHolder,
                landscapeBg = landscapeBg
            )
        } else DocOrgData(
            id = docHeadingOrg?.docNumberCopyWhiteMlc?.value,
            imageUrl = url,
            position = position,
            docType = docType,
            docHeading = docHeadingOrg.toComposeDocHeadingOrg(),
            docButtonHeading = docButtonHeadingOrg.toComposeDocButtonHeadingOrg(
                isStack,
                stackSize
            ),
            chipStatusAtmData = chipStatusAtm?.toUiModel(),
            docCover = cover?.toComposeDocCoverMlc(true, stackSize),
            placeHolder = placeHolder,
            landscapeBg = landscapeBg
        )
        return docData
    }

    override fun toDocCarousel(
        cards: List<DocumentCard>,
        barcodeResult: DocumentBarcodeResult?
    ): DocCarouselOrgData {
        with(cards) {
            val listDocCards = this.mapNotNull { documentCard ->
                when (val doc = documentCard.doc.document) {
                    is Pid.Data -> toDocCardFlip(
                        tickerAtm = doc.getTicker(),
                        tableBlockOrg = doc.getTableBlockPlaneOrg(),
                        docHeadingOrg = doc.getDocHeading(),
                        docButtonHeadingOrg = doc.getDocButtonHeading(),
                        subtitleLabelMlc = doc.getSubtitleLabel(),
                        tableBlockTwoColumnsPlaneOrg = doc.getTableBlockTwoColumnsPlane(),
                        barcodeResult = null,
                        localizationType = LocalizationType.eng,
                        photo = doc.photo,
                        id = doc.id,
                        docType = doc.getItemType(),
                        position = indexOf(documentCard),
                        valueImage = doc.signature?.image,
                        isStack = documentCard.docCount > 1,
                        stackSize = documentCard.docCount,
                        cover = generateDocCoverData(
                            documentCard.doc.getStatus(),
                            doc.verificationCodesCount()
                        ),
                        isTickerClickable = false
                    )

                    is Mdl.Data -> toDocCardFlip(
                        tickerAtm = doc.getTicker(),
                        tableBlockOrg = doc.getTableBlockPlaneOrg(),
                        docHeadingOrg = doc.getDocHeading(),
                        docButtonHeadingOrg = doc.getDocButtonHeading(),
                        subtitleLabelMlc = doc.getSubtitleLabel(),
                        tableBlockTwoColumnsPlaneOrg = doc.getTableBlockTwoColumnsPlane(),
                        barcodeResult = null,
                        localizationType = LocalizationType.eng,
                        photo = doc.photo,
                        id = doc.id,
                        docType = doc.getItemType(),
                        position = indexOf(documentCard),
                        valueImage = doc.signature?.image,
                        isStack = documentCard.docCount > 1,
                        stackSize = documentCard.docCount,
                        cover = generateDocCoverData(
                            documentCard.doc.getStatus(),
                            doc.verificationCodesCount()
                        ),
                        isTickerClickable = false
                    )

                    is DocManualOptions -> toComposeAddDocOrg(
                        docType = doc.getItemType(),
                        indexOf(documentCard)
                    )

                    else -> null
                }
            }

            return DocCarouselOrgData(data = listDocCards.toMutableStateList())
        }
    }

    override fun toComposeContentTableOrg(
        tableBlockTwoColumnsOrg: List<TableBlockTwoColumnsOrg>?,
        tableBlockOrg: List<TableBlockOrg>?,
        tableBlockAccordionOrg: List<TableBlockAccordionOrg>?,
        photo: String?,
        valueImage: String?
    ): ContentTableOrgData {
        return ContentTableOrgData(
            tableBlockTwoColumnsOrgData = tableBlockTwoColumnsOrg?.mapNotNull {
                it.toComposeTableBlockTwoColumnsOrg(photo, valueImage)
            },
            tableBlockOrgData = tableBlockOrg?.mapNotNull {
                it.toComposeTableBlockOrg(valueImage)
            },
            tableBlockAccordionOrgData = tableBlockAccordionOrg?.mapNotNull {
                it.toComposeTableBlockAccordionOrgData(valueImage)
            }
        )
    }


}
