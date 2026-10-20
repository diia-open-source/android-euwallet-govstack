package ua.govstack.digitalwallet.app.wallet_gallery.domain.transformer

import eu.europa.ec.eudi.wallet.document.IssuedDocument
import ua.govstack.digitalwallet.app.core.models.common_compose.atm.icon.IconAtm
import ua.govstack.digitalwallet.app.core.models.common_compose.atm.text.TickerAtm
import ua.govstack.digitalwallet.app.core.models.common_compose.general.Action
import ua.govstack.digitalwallet.app.core.models.common_compose.general.ElementPaddingMode
import ua.govstack.digitalwallet.app.core.models.common_compose.general.PaddingMode
import ua.govstack.digitalwallet.app.core.models.common_compose.mlc.doc.DocNumberCopyMlc
import ua.govstack.digitalwallet.app.core.models.common_compose.org.doc.DocButtonHeadingOrg
import ua.govstack.digitalwallet.app.core.models.common_compose.org.doc.DocHeadingOrg
import ua.govstack.digitalwallet.app.core.models.common_compose.table.HeadingWithSubtitlesMlc
import ua.govstack.digitalwallet.app.core.models.common_compose.table.Item
import ua.govstack.digitalwallet.app.core.models.common_compose.table.TableItemVerticalMlc
import ua.govstack.digitalwallet.app.core.models.common_compose.table.tableBlockOrg.TableBlockOrg
import ua.govstack.digitalwallet.app.core.models.common_compose.table.tableBlockTwoColumnsOrg.TableBlockTwoColumnsOrg
import ua.govstack.digitalwallet.app.core.models.common_compose.table.tableBlockTwoColumnsPlaneOrg.TableBlockTwoColumnsPlaneOrg
import ua.govstack.digitalwallet.app.core.models.document.LocalizationType
import ua.govstack.digitalwallet.app.core.models.document.docgroups.v2.DataForDisplayingInOrderConfigurations
import ua.govstack.digitalwallet.app.core.models.document.docgroups.v2.EN
import ua.govstack.digitalwallet.app.core.models.document.docgroups.v2.FrontCard
import ua.govstack.digitalwallet.app.core.models.document.docgroups.v2.FullInfo
import ua.govstack.digitalwallet.app.core.util.UuidProvider
import ua.govstack.digitalwallet.app.ui_base.components.DiiaResourceIcon
import ua.govstack.digitalwallet.app.ui_base.provider.ResourceProvider
import ua.govstack.digitalwallet.app.wallet_core.documents.extensions.asDisplayValue
import ua.govstack.digitalwallet.app.wallet_core.documents.extensions.displayTitleLabel
import ua.govstack.digitalwallet.app.wallet_core.documents.extensions.findClaim
import ua.govstack.digitalwallet.app.wallet_core.documents.extensions.findPrimitive
import ua.govstack.digitalwallet.app.wallet_core.documents.extensions.joinPrimitiveValues
import ua.govstack.digitalwallet.app.wallet_core.documents.extensions.normalizeBase64
import ua.govstack.digitalwallet.app.wallet_core.documents.model.ClaimDomain
import ua.govstack.digitalwallet.app.wallet_core.documents.model.DocumentDetailsDomain
import ua.govstack.digitalwallet.app.wallet_core.documents.model.DocumentStatus
import ua.govstack.digitalwallet.app.wallet_core.documents.util.PidJsonKeys
import ua.govstack.digitalwallet.app.wallet_doc_pid.models.Pid
import ua.govstack.digitalwallet.app.wallet_gallery.R
import ua.govstack.digitalwallet.app.wallet_gallery.domain.transformer.TableItemFactory.createTableItemVertical

class PidTransformer(
    private val resourceProvider: ResourceProvider, private val uuidProvider: UuidProvider
) {
    suspend fun transformToPid(
        document: IssuedDocument,
        order: Int,
        isRevoked: Boolean,
    ): Pid {
        val docDomain: DocumentDetailsDomain =
            DocumentDetailsTransformer.transformToDocumentDetailsDomain(
                document = document,
                resourceProvider = resourceProvider,
                uuidProvider = uuidProvider
            ).getOrThrow()

        val status = if (isRevoked || docDomain.documentHasExpired) {
            DocumentStatus.STATUS_REVOKED
        } else {
            DocumentStatus.STATUS_ACTIVE
        }

        return createPid(
            document = docDomain,
            attrs = docDomain.documentClaims,
            order = order,
            status = status
        )
    }

    private fun createPid(
        document: DocumentDetailsDomain,
        attrs: List<ClaimDomain>,
        order: Int,
        status: Int
    ): Pid {
        return Pid(
            data = listOf(
                Pid.Data(
                    id = document.docId,
                    dataForDisplayingInOrderConfigurations = createDisplayingInOrderConfigurations(
                        document
                    ),
                    docData = Pid.Data.DocData(
                        docName = document.docName, documentCbor = null, fullName = null
                    ),
                    docNumber = null,
                    docStatus = status,
                    content = createPidContent(attrs),
                    frontCard = createFrontCard(document, attrs),
                    fullInfo = createFullInfo(attrs, document),
                    localization = LocalizationType.eng,
                    shareLocalization = LocalizationType.eng,
                    expirationDate = document.documentExpirationDateFormatted ?: "",
                    order = order,
                    dataForDisplayingAsListItem = null,
                )
            )
        )
    }

    private fun createPidContent(attrs: List<ClaimDomain>): List<Pid.Data.Content> =
        mutableListOf<Pid.Data.Content>().apply {
            attrs.findPrimitive(PidJsonKeys.PICTURE, PidJsonKeys.PORTRAIT)?.let { claim ->
                add(
                    Pid.Data.Content(
                        image = claim.value.normalizeBase64(), code = "photo"
                    )
                )
            }
            attrs.findPrimitive(PidJsonKeys.SIGNATURE)?.let { claim ->
                add(
                    Pid.Data.Content(
                        image = claim.value.normalizeBase64(), code = "signature"
                    )
                )
            }
        }.toList()

    private fun createFrontCard(
        document: DocumentDetailsDomain, attrs: List<ClaimDomain>
    ): FrontCard {
        return FrontCard(
            en = listOf(
                EN(
                    docHeadingOrg = DocHeadingOrg(
                        componentId = "heading_front_card",
                        headingWithSubtitlesMlc = HeadingWithSubtitlesMlc(
                            componentId = "pid_heading_front_card",
                            value = document.docName,
                            subtitles = null,
                            maxLines = 2,
                            isAutoSized = false
                        )
                    ),
                    tableBlockTwoColumnsPlaneOrg = TableBlockTwoColumnsPlaneOrg(
                        componentId = "user_info_front_card",
                        items = mutableListOf<Item>().apply {

                            attrs.findPrimitive(PidJsonKeys.BIRTHDATE, PidJsonKeys.BIRTH_DATE)
                                ?.let { claim ->
                                    add(
                                        Item(
                                            tableItemVerticalMlc = TableItemVerticalMlc(
                                                componentId = "birth_date",
                                                label = claim.displayTitleLabel(),
                                                value = claim.value
                                            )
                                        )
                                    )
                                }

                            attrs.findClaim(PidJsonKeys.NATIONALITIES)
                                ?.let { claim ->
                                    add(
                                        Item(
                                            tableItemVerticalMlc = TableItemVerticalMlc(
                                                componentId = "nationalities",
                                                label = claim.displayTitleLabel(),
                                                value = claim.asDisplayValue()
                                            )
                                        )
                                    )
                                }

                            attrs.findClaim(PidJsonKeys.NATIONALITY)
                                ?.let { claim ->
                                    add(
                                        Item(
                                            tableItemVerticalMlc = TableItemVerticalMlc(
                                                componentId = "nationality",
                                                label = claim.displayTitleLabel(),
                                                value = claim.asDisplayValue()
                                            )
                                        )
                                    )
                                }

                            attrs.findPrimitive(PidJsonKeys.SIGNATURE)?.let { claim ->
                                add(
                                    createTableItemVertical(
                                        componentId = claim.key,
                                        value = claim.value.normalizeBase64()
                                    )
                                )
                            }
                        }),
                    tickerAtm = createTicker(attrs),
                    smallEmojiPanelMlc = null,
                    subtitleLabelMlc = null,
                    chipStatusAtm = null,
                    docButtonHeadingOrg = DocButtonHeadingOrg(
                        componentId = "button_heading_front_card",
                        headingWithSubtitlesMlc = HeadingWithSubtitlesMlc(
                            componentId = "pid_button_heading_front_card",
                            value = attrs.joinPrimitiveValues(
                                PidJsonKeys.GIVEN_NAME, PidJsonKeys.FAMILY_NAME
                            ) ?: "",
                            paddingMode = PaddingMode(
                                top = ElementPaddingMode.LARGE,
                                side = ElementPaddingMode.NONE
                            ),
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
                                subtype = "pid",
                                resource = null,
                                subresource = null
                            )
                        )
                    )
                )
            ), ua = null
        )
    }

    private fun createFullInfo(
        attrs: List<ClaimDomain>, document: DocumentDetailsDomain,
    ): List<FullInfo> {
        val consumedKeys = mutableSetOf<String>()

        return mutableListOf<FullInfo>().apply {
            add(createFullInfoHeading(attrs, document, consumedKeys))

            add(FullInfo(tickerAtm = createTicker(attrs)))

            add(createPhotoCardBlock(attrs, consumedKeys))

            add(createPersonalDataBlock(attrs, consumedKeys))

            add(createIssuingInfoBlock(attrs, consumedKeys))

            createLocationInfoBlock(
                attrs,
                consumedKeys
            ).takeIf { it.tableBlockOrg?.items?.isNotEmpty() == true }?.let { add(it) }

            createUserContactBlock(attrs, consumedKeys)?.let { add(it) }

            createOthersBlocks(attrs, consumedKeys).forEach { add(it) }
        }
    }

    private fun createFullInfoHeading(
        attrs: List<ClaimDomain>, document: DocumentDetailsDomain, consumedKeys: MutableSet<String>
    ) = FullInfo(
        docHeadingOrg = DocHeadingOrg(
            componentId = "heading_full_info",
            headingWithSubtitlesMlc = HeadingWithSubtitlesMlc(
                componentId = "pid_heading_full_info", value = document.docName, subtitles = null,
                maxLines = Int.MAX_VALUE,
                isAutoSized = false
            ),
            docNumberCopyMlc = attrs.findPrimitive(PidJsonKeys.DOCUMENT_NUMBER)?.let { claim ->
                DocNumberCopyMlc(
                    componentId = "document_number",
                    value = claim.value,
                    icon = null,
                    label = null,
                    maxLines = Int.MAX_VALUE,
                    isAutoSized = false
                )
            }?.also { consumedKeys.add(PidJsonKeys.DOCUMENT_NUMBER) })
    )

    private fun createPhotoCardBlock(attrs: List<ClaimDomain>, consumed: MutableSet<String>) =
        FullInfo(
            tableBlockTwoColumnsOrg = TableBlockTwoColumnsOrg(
                componentId = "user_photo_info",
                headingWithSubtitlesMlc = HeadingWithSubtitlesMlc(
                    componentId = "user_name",
                    value = attrs.joinPrimitiveValues(
                        PidJsonKeys.GIVEN_NAME, PidJsonKeys.FAMILY_NAME
                    )?.also {
                        consumed.addAll(
                            listOf(
                                PidJsonKeys.GIVEN_NAME, PidJsonKeys.FAMILY_NAME
                            )
                        )
                    } ?: "",
                    subtitles = null,
                    maxLines = Int.MAX_VALUE,
                    isAutoSized = false),
                photo = attrs.findPrimitive(PidJsonKeys.PICTURE, PidJsonKeys.PORTRAIT)
                    ?.also { consumed.add(it.key) }?.value?.normalizeBase64(),
                items = mutableListOf<Item>().apply {
                    attrs.findPrimitive(PidJsonKeys.BIRTHDATE, PidJsonKeys.BIRTH_DATE)
                        ?.let { claim ->
                            consumed.add(claim.key)
                            add(
                                createTableItemVertical(
                                    claim.key,
                                    claim.displayTitleLabel(),
                                    claim.value
                                )
                            )
                        }

                    attrs.findPrimitive(PidJsonKeys.SIGNATURE)?.let { claim ->
                        consumed.add(claim.key)
                        add(
                            createTableItemVertical(
                                claim.key, claim.displayTitleLabel(), value = claim.value
                            )
                        )
                    }
                })
        )

    private fun createPersonalDataBlock(attrs: List<ClaimDomain>, consumed: MutableSet<String>) =
        FullInfo(
            tableBlockOrg = TableBlockOrg(
                componentId = "personal_data", items = mutableListOf<Item>().apply {
                    attrs.findPrimitive(PidJsonKeys.SEX)?.let { claim ->
                        consumed.add(claim.key)
                        add(
                            createTableItemVertical(
                                claim.key,
                                claim.displayTitleLabel(),
                                claim.value
                            )
                        )
                    }

                    attrs.findClaim(PidJsonKeys.NATIONALITIES)?.let { claim ->
                        consumed.add(claim.key)
                        add(
                            createTableItemVertical(
                                claim.key, claim.displayTitleLabel(), claim.asDisplayValue()
                            )
                        )
                    }

                    attrs.findClaim(PidJsonKeys.NATIONALITY)?.let { claim ->
                        consumed.add(claim.key)
                        add(
                            createTableItemVertical(
                                claim.key, claim.displayTitleLabel(), claim.asDisplayValue("\n")
                            )
                        )
                    }

                    attrs.findClaim(PidJsonKeys.PLACE_OF_BIRTH)?.let { claim ->
                        consumed.add(claim.key)
                        add(
                            createTableItemVertical(
                                claim.key, claim.displayTitleLabel(), claim.asDisplayValue()
                            )
                        )
                    }

                    attrs.findClaim(PidJsonKeys.BIRTH_PLACE)?.let { claim ->
                        consumed.add(claim.key)
                        add(
                            createTableItemVertical(
                                claim.key, claim.displayTitleLabel(), claim.asDisplayValue()
                            )
                        )
                    }
                })
        )

    private fun createIssuingInfoBlock(attrs: List<ClaimDomain>, consumed: MutableSet<String>) =
        FullInfo(
            tableBlockOrg = TableBlockOrg(
                componentId = "issuing_info", items = listOf(
                    PidJsonKeys.PERSON_ADMINISTRATIVE_NUMBER,
                    PidJsonKeys.ISSUANCE_DATE,
                    PidJsonKeys.DATE_OF_ISSUANCE,
                    PidJsonKeys.DATE_OF_EXPIRY,
                    PidJsonKeys.EXPIRY_DATE,
                    PidJsonKeys.ISSUING_AUTHORITY,
                    PidJsonKeys.ISSUING_COUNTRY,
                    PidJsonKeys.ISSUING_JURISDICTION
                ).mapNotNull { key ->
                    attrs.findPrimitive(key)?.let { claim ->
                        consumed.add(claim.key)
                        createTableItemVertical(claim.key, claim.displayTitleLabel(), claim.value)
                    }
                })
        )

    private fun createLocationInfoBlock(attrs: List<ClaimDomain>, consumed: MutableSet<String>) =
        FullInfo(
            tableBlockOrg = TableBlockOrg(
                componentId = "location_info", items = listOf(
                    PidJsonKeys.RESIDENT_COUNTRY,
                    PidJsonKeys.RESIDENT_STATE,
                    PidJsonKeys.RESIDENT_CITY,
                    PidJsonKeys.RESIDENT_STREET,
                    PidJsonKeys.RESIDENT_HOUSE_NUMBER,
                    PidJsonKeys.RESIDENT_POSTAL_CODE,
                    PidJsonKeys.RESIDENT_ADDRESS,
                    PidJsonKeys.ADDRESS
                ).mapNotNull { key ->
                    attrs.findClaim(key)?.let { claim ->
                        consumed.add(claim.key)
                        createTableItemVertical(
                            claim.key, claim.displayTitleLabel(), claim.asDisplayValue()
                        )
                    }
                })
        )

    private fun createDisplayingInOrderConfigurations(doc: DocumentDetailsDomain) =
        DataForDisplayingInOrderConfigurations(
            id = "dataForDisplayingInOrderConfigurations_pid",
            label = doc.docName,
            description = doc.issuerUrl
        )

    private fun createUserContactBlock(
        attrs: List<ClaimDomain>,
        consumed: MutableSet<String>
    ): FullInfo? {

        val items = mutableListOf<Item>().apply {

            attrs.findPrimitive(PidJsonKeys.EMAIL_ADDRESS)?.let { claim ->
                consumed.add(claim.key)
                add(createTableItemVertical(claim.key, claim.displayTitleLabel(), claim.value))
            }

            attrs.findPrimitive(PidJsonKeys.MOBILE_PHONE_NUMBER)?.let { claim ->
                consumed.add(claim.key)
                add(createTableItemVertical(claim.key, claim.displayTitleLabel(), claim.value))
            }
        }

        return items.takeIf { it.isNotEmpty() }?.let {
            FullInfo(
                tableBlockOrg = TableBlockOrg(
                    componentId = "personal_data",
                    items = it
                )
            )
        }
    }

    private fun createOthersBlocks(
        attrs: List<ClaimDomain>, consumed: MutableSet<String>
    ): List<FullInfo> {
        val blocks = mutableListOf<FullInfo>()
        val leftoverPrimitives = mutableListOf<ClaimDomain.Primitive>()
        consumed.addAll(listOf(PidJsonKeys.EXP, PidJsonKeys.IAT, PidJsonKeys.TRUST_ANCHOR))
        attrs.forEach { claim ->
            if (claim.key in consumed) return@forEach

            when (claim) {
                is ClaimDomain.Group -> {
                    blocks.add(
                        FullInfo(
                            tableBlockOrg = TableBlockOrg(
                                componentId = "group_${claim.key}",
                                items = claim.items.filterIsInstance<ClaimDomain.Primitive>().map {
                                    createTableItemVertical(
                                        it.key,
                                        it.displayTitleLabel(),
                                        it.value
                                    )
                                })
                        )
                    )
                }

                is ClaimDomain.Primitive -> {
                    leftoverPrimitives.add(claim)
                }
            }
        }

        if (leftoverPrimitives.isNotEmpty()) {
            blocks.add(
                FullInfo(
                    tableBlockOrg = TableBlockOrg(
                        componentId = "other_data", items = leftoverPrimitives.map {
                            createTableItemVertical(it.key, it.displayTitleLabel(), it.value)
                        })
                )
            )
        }
        return blocks
    }

    private fun createTicker(
        attrs: List<ClaimDomain>,
    ): TickerAtm {
        return TickerAtm(
            componentId = "ticker",
            usage = TickerAtm.UsageType.document,
            type = TickerAtm.TickerType.cyan,
            value = resourceProvider.getString(
                R.string.ticker_label_s,
                attrs.findPrimitive(
                    PidJsonKeys.DATE_OF_EXPIRY, PidJsonKeys.EXPIRY_DATE
                )?.value.toString()
            )
        )
    }
}
