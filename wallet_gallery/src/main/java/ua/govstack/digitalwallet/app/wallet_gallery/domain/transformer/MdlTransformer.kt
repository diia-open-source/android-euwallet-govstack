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
import ua.govstack.digitalwallet.app.wallet_core.documents.extensions.displayTitleLabel
import ua.govstack.digitalwallet.app.wallet_core.documents.extensions.findGroup
import ua.govstack.digitalwallet.app.wallet_core.documents.extensions.findPrimitive
import ua.govstack.digitalwallet.app.wallet_core.documents.extensions.findPrimitiveClaims
import ua.govstack.digitalwallet.app.wallet_core.documents.extensions.joinPrimitiveValues
import ua.govstack.digitalwallet.app.wallet_core.documents.extensions.normalizeBase64
import ua.govstack.digitalwallet.app.wallet_core.documents.model.ClaimDomain
import ua.govstack.digitalwallet.app.wallet_core.documents.model.DocumentDetailsDomain
import ua.govstack.digitalwallet.app.wallet_core.documents.model.DocumentStatus
import ua.govstack.digitalwallet.app.wallet_core.documents.util.MdlJsonKeys
import ua.govstack.digitalwallet.app.wallet_core.documents.util.PidJsonKeys
import ua.govstack.digitalwallet.app.wallet_doc_mdl.models.Mdl
import ua.govstack.digitalwallet.app.wallet_gallery.R
import ua.govstack.digitalwallet.app.wallet_gallery.domain.transformer.TableItemFactory.createImageItem
import ua.govstack.digitalwallet.app.wallet_gallery.domain.transformer.TableItemFactory.createTableItemHorizontal
import ua.govstack.digitalwallet.app.wallet_gallery.domain.transformer.TableItemFactory.createTableItemVertical

class MdlTransformer(
    private val resourceProvider: ResourceProvider, private val uuidProvider: UuidProvider
) {
    suspend fun transformToMdl(document: IssuedDocument, order: Int, isRevoked: Boolean): Mdl {
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

        return createMdl(docDomain, docDomain.documentClaims, order, status)
    }

    private fun createMdl(
        document: DocumentDetailsDomain,
        attrs: List<ClaimDomain>,
        order: Int,
        status: Int
    ): Mdl {
        return Mdl(
            data = listOf(
                Mdl.Data(
                    dataForDisplayingInOrderConfigurations = createDisplayingInOrderConfigurations(
                        document
                    ),
                    docData = Mdl.Data.DocData(
                        docName = document.docName, documentCbor = null
                    ),
                    docNumber = null,
                    docStatus = status,
                    content = createContent(attrs),
                    frontCard = createFrontCard(document, attrs),
                    fullInfo = createFullInfo(attrs, document),
                    id = document.docId,
                    localization = LocalizationType.eng,
                    shareLocalization = LocalizationType.eng,
                    expirationDate = document.documentExpirationDateFormatted ?: "",
                    order = order,
                    dataForDisplayingAsListItem = null,
                )
            )
        )
    }

    private fun createContent(attrs: List<ClaimDomain>): List<Mdl.Data.Content> =
        mutableListOf<Mdl.Data.Content>().apply {

            attrs.findPrimitive(MdlJsonKeys.PORTRAIT)?.let { claim ->
                add(
                    Mdl.Data.Content(
                        image = claim.value.normalizeBase64(), code = "photo"
                    )
                )
            }
            attrs.findPrimitive(MdlJsonKeys.SIGNATURE)?.let { claim ->
                add(
                    Mdl.Data.Content(
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
                            componentId = "mdl_heading_front_card",
                            value = document.docName,
                            subtitles = null,
                            maxLines = Int.MAX_VALUE,
                            isAutoSized = false
                        )
                    ),
                    tableBlockTwoColumnsPlaneOrg = TableBlockTwoColumnsPlaneOrg(
                        componentId = "user_info_front_card",
                        photo = attrs.findPrimitive(MdlJsonKeys.PORTRAIT)?.value?.normalizeBase64(),
                        items = mutableListOf<Item>().apply {

                            attrs.findPrimitive(MdlJsonKeys.BIRTH_DATE)?.let { claim ->
                                add(
                                    createTableItemVertical(
                                        componentId = "birth_date",
                                        displayTitle = claim.displayTitleLabel(),
                                        value = claim.value
                                    )
                                )
                            }

                            attrs.getDrivingPrivilegeCategoriesText()?.let { categories ->
                                add(
                                    createTableItemVertical(
                                        componentId = "category",
                                        displayTitle = resourceProvider.getString(R.string.claim_category_title),
                                        value = categories
                                    )
                                )
                            }

                            attrs.findPrimitive(MdlJsonKeys.SIGNATURE)?.value?.let {
                                add(createImageItem("signature_full_info", it))
                            }
                        }),
                    tickerAtm = createTicker(attrs),
                    docButtonHeadingOrg = createFrontButton(attrs)
                )
            ), ua = null
        )
    }

    private fun createFullInfo(
        attrs: List<ClaimDomain>, document: DocumentDetailsDomain
    ): List<FullInfo> {
        val consumedKeys = mutableSetOf<String>()

        return mutableListOf<FullInfo>().apply {
            add(createFullInfoHeading(attrs, document, consumedKeys))
            add(FullInfo(tickerAtm = createTicker(attrs)))
            add(createUserBlock(attrs, consumedKeys))
            createDrivingPrivilegesBlock(attrs, consumedKeys)?.let { add(it) }
            createPersonalBlock(attrs, consumedKeys)?.let { add(it) }
            add(createIssuingBlock(attrs, consumedKeys))
            createResidenceBlock(attrs, consumedKeys)?.let { add(it) }
            createCharacteristicsBlock(attrs, consumedKeys)?.let { add(it) }
            createContactBlock(attrs, consumedKeys)?.let { add(it) }
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
            docNumberCopyMlc = attrs.findPrimitive(MdlJsonKeys.DOCUMENT_NUMBER)?.let { claim ->
                DocNumberCopyMlc(
                    componentId = "document_number",
                    value = claim.value,
                    icon = null,
                    label = null,
                    maxLines = Int.MAX_VALUE,
                    isAutoSized = false
                )
            }?.also { consumedKeys.add(MdlJsonKeys.DOCUMENT_NUMBER) })
    )

    private fun createUserBlock(attrs: List<ClaimDomain>, consumedKeys: MutableSet<String>) =
        FullInfo(
            tableBlockTwoColumnsOrg = TableBlockTwoColumnsOrg(
                componentId = "user_photo_info",
                headingWithSubtitlesMlc = HeadingWithSubtitlesMlc(
                    componentId = "user_name",
                    value = attrs.joinPrimitiveValues(
                        MdlJsonKeys.GIVEN_NAME, MdlJsonKeys.FAMILY_NAME
                    )?.also {
                        consumedKeys.addAll(
                            listOf(
                                MdlJsonKeys.GIVEN_NAME,
                                MdlJsonKeys.FAMILY_NAME
                            )
                        )
                    },
                    subtitles = attrs.joinPrimitiveValues(
                        MdlJsonKeys.FAMILY_NAME_NATIONAL_CHARACTER,
                        MdlJsonKeys.GIVEN_NAME_NATIONAL_CHARACTER
                    )?.also {
                        consumedKeys.addAll(
                            listOf(
                                MdlJsonKeys.FAMILY_NAME_NATIONAL_CHARACTER,
                                MdlJsonKeys.GIVEN_NAME_NATIONAL_CHARACTER
                            )
                        )
                    }?.let { listOf(it) },
                    maxLines = Int.MAX_VALUE,
                    isAutoSized = false
                ),
                photo = attrs.findPrimitive(MdlJsonKeys.PORTRAIT)?.value?.normalizeBase64()
                    .also { consumedKeys.add(MdlJsonKeys.PORTRAIT) },
                items = mutableListOf<Item>().apply {

                    attrs.findPrimitive(MdlJsonKeys.BIRTH_DATE)?.let { claim ->
                        consumedKeys.add(claim.key)
                        add(
                            createTableItemVertical(
                                "birth_date",
                                claim.displayTitleLabel(),
                                claim.value
                            )
                        )
                    }

                    attrs.getDrivingPrivilegeCategoriesText()?.let { categories ->
                        add(
                            createTableItemVertical(
                                "category",
                                resourceProvider.getString(R.string.claim_category_title),
                                categories
                            )
                        )
                    }.also { consumedKeys.add(MdlJsonKeys.DRIVING_PRIVILEGES) }

                    attrs.findPrimitive(MdlJsonKeys.SIGNATURE)?.value?.let {
                        add(createImageItem("signature_full_info", it))
                    }.also { consumedKeys.add(MdlJsonKeys.SIGNATURE) }
                })
        )

    private fun createDrivingPrivilegesBlock(
        attrs: List<ClaimDomain>,
        consumed: MutableSet<String>
    ): FullInfo? {
        val items = attrs.findGroup(MdlJsonKeys.DRIVING_PRIVILEGES)
            ?.toDrivingPrivilegeEntries()
            .orEmpty()
            .flatMapIndexed { index, entry ->
                buildList {
                    entry.category?.takeIf { it.isNotBlank() }?.let { category ->
                        add(
                            createTableItemHorizontal(
                                componentId = "driving_privilege_category_$index",
                                displayTitle = resourceProvider.getString(R.string.claim_category_title),
                                value = category
                            )
                        )
                    }
                    entry.issueDate?.takeIf { it.isNotBlank() }?.let { issueDate ->
                        add(
                            createTableItemHorizontal(
                                componentId = "driving_privilege_issue_date_$index",
                                displayTitle = resourceProvider.getString(R.string.claim_issue_date_title),
                                value = issueDate
                            )
                        )
                    }
                    entry.expiryDate?.takeIf { it.isNotBlank() }?.let { expiryDate ->
                        add(
                            createTableItemHorizontal(
                                componentId = "driving_privilege_expiry_date_$index",
                                displayTitle = resourceProvider.getString(R.string.claim_expiry_date_title),
                                value = expiryDate
                            )
                        )
                    }
                }
            }

        if (items.isEmpty()) return null

        consumed.add(MdlJsonKeys.DRIVING_PRIVILEGES)

        return FullInfo(
            tableBlockOrg = TableBlockOrg(
                componentId = "driving_privileges_info",
                items = items
            )
        )
    }

    private fun createIssuingBlock(
        attrs: List<ClaimDomain>, consumed: MutableSet<String>
    ) = FullInfo(
        tableBlockOrg = TableBlockOrg(
            componentId = "issuing_info", items = listOf(
                MdlJsonKeys.ADMINISTRATIVE_NUMBER,
                MdlJsonKeys.ISSUE_DATE,
                MdlJsonKeys.EXPIRY_DATE,
                MdlJsonKeys.ISSUING_AUTHORITY,
                MdlJsonKeys.ISSUING_COUNTRY
            ).mapNotNull { key ->
                attrs.findPrimitive(key, recursive = false)?.let { claim ->
                    consumed.add(claim.key)
                    createTableItemVertical(
                        componentId = claim.key,
                        displayTitle = claim.displayTitleLabel(),
                        value = claim.value
                    )
                }.also { consumed.addAll(listOf(MdlJsonKeys.EXPIRY_DATE, MdlJsonKeys.ISSUE_DATE)) }
            })
    )

    private fun createPersonalBlock(
        attrs: List<ClaimDomain>, consumed: MutableSet<String>
    ): FullInfo? {

        val items = mutableListOf<Item>().apply {

            attrs.findPrimitive(MdlJsonKeys.NATIONALITY)?.let { claim ->
                consumed.add(claim.key)
                add(createTableItemHorizontal(claim.key, claim.displayTitleLabel(), claim.value))
            }

            attrs.findPrimitive(MdlJsonKeys.SEX)?.let { claim ->
                consumed.add(claim.key)
                add(createTableItemHorizontal(claim.key, claim.displayTitleLabel(), claim.value))
            }

            attrs.findPrimitive(MdlJsonKeys.BIRTH_PLACE)?.let { claim ->
                consumed.add(claim.key)
                add(createTableItemHorizontal(claim.key, claim.displayTitleLabel(), claim.value))
            }
        }

        if (items.isEmpty()) return null

        return FullInfo(
            tableBlockOrg = TableBlockOrg(
                componentId = "user_info_full_info", items = items
            )
        )
    }

    private fun createResidenceBlock(
        attrs: List<ClaimDomain>,
        consumed: MutableSet<String>
    ): FullInfo? {
        val items = listOf(
            MdlJsonKeys.RESIDENT_COUNTRY,
            MdlJsonKeys.RESIDENT_STATE,
            MdlJsonKeys.RESIDENT_CITY,
            MdlJsonKeys.RESIDENT_ADDRESS,
            MdlJsonKeys.RESIDENT_POSTAL_CODE
        ).mapNotNull { key ->
            attrs.findPrimitive(key, recursive = false)?.also { claim ->
                consumed.add(claim.key)
            }
        }.joinToString(", ") { it.value }

        return items.takeIf { it.isNotEmpty() }?.let {
            FullInfo(
                tableBlockOrg = TableBlockOrg(
                    componentId = "residence_address",
                    items = listOf(
                        createTableItemVertical(
                            componentId = "residence_address_data",
                            displayTitle = resourceProvider.getString(R.string.claim_residence_address),
                            value = items
                        )
                    )
                )
            )
        }
    }

    private fun createCharacteristicsBlock(
        attrs: List<ClaimDomain>, consumed: MutableSet<String>
    ): FullInfo? {

        val items = mutableListOf<Item>().apply {

            attrs.findPrimitive(MdlJsonKeys.HEIGHT)?.let { claim ->
                consumed.add(claim.key)
                add(createTableItemHorizontal(claim.key, claim.displayTitleLabel(), claim.value))
            }
            attrs.findPrimitive(MdlJsonKeys.WEIGHT)?.let { claim ->
                consumed.add(claim.key)
                add(createTableItemHorizontal(claim.key, claim.displayTitleLabel(), claim.value))
            }

            attrs.findPrimitive(MdlJsonKeys.EYE_COLOUR)?.let { claim ->
                consumed.add(claim.key)
                add(createTableItemHorizontal(claim.key, claim.displayTitleLabel(), claim.value))
            }

            attrs.findPrimitive(MdlJsonKeys.HAIR_COLOUR)?.let { claim ->
                consumed.add(claim.key)
                add(createTableItemHorizontal(claim.key, claim.displayTitleLabel(), claim.value))
            }
        }

        if (items.isEmpty()) return null

        return FullInfo(
            tableBlockOrg = TableBlockOrg(
                componentId = "user_info_full_info", items = items
            )
        )
    }

    private fun createContactBlock(
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

    private fun createDisplayingInOrderConfigurations(doc: DocumentDetailsDomain) =
        DataForDisplayingInOrderConfigurations(
            id = "dataForDisplayingInOrderConfigurations_mdl",
            label = doc.docName,
            description = doc.issuerUrl
        )

    private fun createFrontButton(attrs: List<ClaimDomain>) = DocButtonHeadingOrg(
        componentId = "button_heading_front_card",
        headingWithSubtitlesMlc = HeadingWithSubtitlesMlc(
            componentId = "mdl_button_heading_front_card",
            value = attrs.joinPrimitiveValues(
                MdlJsonKeys.GIVEN_NAME, MdlJsonKeys.FAMILY_NAME
            ) ?: "",
            paddingMode = PaddingMode(
                top = ElementPaddingMode.LARGE,
                side = ElementPaddingMode.NONE
            ),
            subtitles = null,
            maxLines = 2,
            isAutoSized = false,
        ),
        iconAtm = IconAtm(
            componentId = "ellipse_menu",
            code = DiiaResourceIcon.ELLIPSE_KEBAB.code,
            accessibilityDescription = "",
            action = Action(
                type = "ellipseMenu", subtype = "mdl", resource = null, subresource = null
            )
        )
    )

    private fun createTicker(attrs: List<ClaimDomain>): TickerAtm {
        return TickerAtm(
            componentId = "ticker",
            usage = TickerAtm.UsageType.document,
            type = TickerAtm.TickerType.cyan,
            value = resourceProvider.getString(
                R.string.ticker_label_s,
                attrs.findPrimitive(
                    MdlJsonKeys.DATE_OF_EXPIRY, MdlJsonKeys.EXPIRY_DATE, recursive = false
                )?.value.toString()
            )
        )
    }
}

private fun List<ClaimDomain>.getDrivingPrivilegeCategoriesText(): String? {
    return findPrimitiveClaims(MdlJsonKeys.VEHICLE_CATEGORY_CODE).map { it.value }.distinct()
        .joinToString(", ").ifEmpty { null }
}

private fun ClaimDomain.Group.toDrivingPrivilegeEntries(): List<DrivingPrivilegeEntry> {
    val categories = items.findPrimitiveClaims(
        key = MdlJsonKeys.VEHICLE_CATEGORY_CODE,
        recursive = false
    ).map { it.value }
    val issueDates = items.findPrimitiveClaims(
        key = MdlJsonKeys.ISSUE_DATE,
        recursive = false
    ).map { it.value }
    val expiryDates = items.findPrimitiveClaims(
        key = MdlJsonKeys.EXPIRY_DATE,
        recursive = false
    ).map { it.value }
    val maxSize = maxOf(categories.size, issueDates.size, expiryDates.size)

    return (0 until maxSize).mapNotNull { index ->
        DrivingPrivilegeEntry(
            category = categories.getOrNull(index),
            issueDate = issueDates.getOrNull(index),
            expiryDate = expiryDates.getOrNull(index)
        ).takeIf { entry ->
            listOf(entry.category, entry.issueDate, entry.expiryDate).any { !it.isNullOrBlank() }
        }
    }
}

private data class DrivingPrivilegeEntry(
    val category: String?,
    val issueDate: String?,
    val expiryDate: String?
)
