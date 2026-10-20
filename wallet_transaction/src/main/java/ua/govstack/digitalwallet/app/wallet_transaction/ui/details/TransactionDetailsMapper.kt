package ua.govstack.digitalwallet.app.wallet_transaction.ui.details

import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import eu.europa.ec.eudi.wallet.transactionLogging.TransactionLog
import ua.govstack.digitalwallet.app.ui_base.components.DiiaResourceIcon
import ua.govstack.digitalwallet.app.ui_base.components.atom.icon.SmallIconAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.status.ChipStatusAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.status.StatusChipType
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.UIElementData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIActionKeysCompose
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.SidePaddingMode
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.TopPaddingMode
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.UiText
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.toDynamicString
import ua.govstack.digitalwallet.app.ui_base.components.molecule.card.CardMlcV2Data
import ua.govstack.digitalwallet.app.ui_base.components.molecule.header.NavigationPanelMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.list.table.items.contentgroup.TableBlockOrgV2Data
import ua.govstack.digitalwallet.app.ui_base.components.molecule.list.table.items.tableblock.TableBlockItem
import ua.govstack.digitalwallet.app.ui_base.components.molecule.list.table.items.tableblock.TableItemHorizontalMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.list.table.items.tableblock.TableItemVerticalMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.list.table.items.tableblock.TableMainHeadingMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.message.AttentionIconMessageMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.message.BackgroundMode
import ua.govstack.digitalwallet.app.ui_base.components.organism.accordion.AccordionOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.header.TopGroupOrgData
import ua.govstack.digitalwallet.app.ui_base.provider.ResourceProvider
import ua.govstack.digitalwallet.app.wallet_core.documents.model.IssuedDocumentClaimDomain
import ua.govstack.digitalwallet.app.wallet_core.documents.model.asDisplayString
import ua.govstack.digitalwallet.app.wallet_core.documents.util.MdlJsonKeys
import ua.govstack.digitalwallet.app.wallet_core.utils.FULL_DATETIME_PATTERN
import ua.govstack.digitalwallet.app.wallet_core.utils.formatLocalDateTime
import ua.govstack.digitalwallet.app.wallet_transaction.R
import ua.govstack.digitalwallet.app.wallet_transaction.domain.model.TransactionLogDataDomain
import ua.govstack.digitalwallet.app.wallet_transaction.domain.model.TransactionLogDataDomain.Companion.getDisplayName
import ua.govstack.digitalwallet.app.wallet_transaction.domain.model.TransactionLogDataDomain.Companion.getTransactionDocumentNames
import javax.inject.Inject

class TransactionDetailsMapper @Inject constructor(
    private val resourceProvider: ResourceProvider
) {

    fun toolbar(): UIElementData = TopGroupOrgData(
        navigationPanelMlcData = NavigationPanelMlcData(
            title = UiText.StringResource(R.string.transactions_title),
            isContextMenuExist = false
        )
    )

    fun transactionDocumentNames(transaction: TransactionLogDataDomain): List<String> {
        return transaction.getTransactionDocumentNames(resourceProvider.getLocale())
    }

    fun unknownDocumentName(): String {
        return resourceProvider.getString(R.string.transaction_document_unknown)
    }

    fun dataSharedDescription(): String {
        return resourceProvider.getString(R.string.transaction_data_shared)
    }

    fun dataIssuedDescription(): String {
        return resourceProvider.getString(R.string.transaction_data_issued)
    }

    fun transactionCard(transaction: TransactionLogDataDomain): UIElementData = CardMlcV2Data(
        actionKey = UIActionKeysCompose.CARD_MLC_V2,
        id = transaction.id,
        label = UiText.DynamicString(transaction.getDisplayName(resourceProvider)),
        chip = ChipStatusAtmData(
            componentId = UiText.DynamicString(transaction.id),
            type = if (transaction.status == TransactionLog.Status.Completed) {
                StatusChipType.POSITIVE
            } else {
                StatusChipType.NEUTRAL
            },
            title = transaction.status.name.toUpperCase(Locale.current)
        ),
        chips = listOfNotNull(
            ChipStatusAtmData(
                type = StatusChipType.WHITE,
                title = transaction.transactionTypeTitle()
            )
        ),
        rightLabel = UiText.DynamicString(
            transaction.creationLocalDateTime.formatLocalDateTime(FULL_DATETIME_PATTERN)
        ),
        paddingHorizontal = SidePaddingMode.MEDIUM
    )

    fun mapBodyContent(transaction: TransactionLogDataDomain): List<UIElementData> {
        val documentNames = transactionDocumentNames(transaction)
        val cardItem = transactionCard(transaction)

        val tableBlocks = when (transaction) {
            is TransactionLogDataDomain.PresentationLog -> transaction.documents.toTableBlocks(
                documentNames = documentNames,
                description = dataSharedDescription()
            )

            is TransactionLogDataDomain.IssuanceLog -> transaction.documents.toTableBlocks(
                documentNames = documentNames,
                description = dataIssuedDescription()
            )

            is TransactionLogDataDomain.DeletingLog -> {
                transaction.documents.map {
                    emptyDocument(it.name)
                }
            }

            else -> emptyList()
        }

        return listOf(cardItem) + tableBlocks
    }

    private fun List<TransactionLogDataDomain.IssuanceDocument>.toTableBlocks(
        documentNames: List<String>,
        description: String
    ): List<UIElementData> = mapIndexed { index, document ->
        documentTableBlock(
            documentName = documentNames.getOrNull(index) ?: unknownDocumentName(),
            description = description,
            document = document
        )
    }

    private fun documentTableBlock(
        documentName: String,
        description: String,
        document: TransactionLogDataDomain.IssuanceDocument
    ): UIElementData {
        val documentTableItems = getTableItems(document)
        return if (documentTableItems.isEmpty()) {
            emptyDocument(document.name)
        } else {
            TableBlockOrgV2Data(
                actionKey = UIActionKeysCompose.TABLE_BLOCK_ORG,
                headerMain = TableMainHeadingMlcData(
                    title = UiText.DynamicString(documentName),
                    description = UiText.DynamicString(description)
                ),
                items = documentTableItems
            )
        }
    }

    private fun emptyDocument(name: String): UIElementData = TableBlockOrgV2Data(
        actionKey = UIActionKeysCompose.TABLE_BLOCK_ORG,
        headerMain = TableMainHeadingMlcData(
            title = UiText.DynamicString(name),
        ),
        attentionIconMessageMlc =
            AttentionIconMessageMlcData(
                icon = SmallIconAtmData(code = DiiaResourceIcon.ELLIPSE_INFO.code),
                text = UiText.StringResource(R.string.transaction_document_not_found),
                backgroundMode = BackgroundMode.NOTE,
            )
    )

    private fun getTableItems(document: TransactionLogDataDomain.IssuanceDocument): List<TableBlockItem> =
        document.fields.map { item ->
            if (item.children.isEmpty()) {
                TableItemVerticalMlcData(
                    title = UiText.DynamicString(item.displayTitle.capitalize() + ":"),
                    value = UiText.DynamicString(item.value.asDisplayString(resourceProvider))
                )
            } else {
                AccordionOrgData(
                    heading = item.displayTitle.capitalize(),
                    state = false,
                    paddingTop = TopPaddingMode.NONE,
                    paddingHorizontal = SidePaddingMode.NONE,
                    collapsedIcon = SmallIconAtmData(
                        componentId = "e_01".toDynamicString(),
                        code = "chevronDown",
                        accessibilityDescription = resourceProvider.getString(
                            R.string.transaction_accessibility_expand_details
                        ),
                        action = null
                    ),
                    expandedIcon = SmallIconAtmData(
                        componentId = "c_02".toDynamicString(),
                        code = "chevronUp",
                        accessibilityDescription = resourceProvider.getString(
                            R.string.transaction_accessibility_collapse_details
                        ),
                        action = null
                    ),
                    expandedContent = item.filterContent()
                )
            }
        }

    private fun IssuedDocumentClaimDomain.filterContent(): List<TableBlockItem> {
        val childrenToDisplay = if (identifier == MdlJsonKeys.DRIVING_PRIVILEGES) {
            children.filter { it.identifier == MdlJsonKeys.VEHICLE_CATEGORY_CODE }
        } else {
            children
        }
        return childrenToDisplay.toAccordionTableItems()
    }

    private fun List<IssuedDocumentClaimDomain>.toAccordionTableItems(): List<TableBlockItem> {
        return flatMap { field ->
            if (field.children.isEmpty()) {
                listOf(
                    TableItemHorizontalMlcData(
                        title = UiText.DynamicString(field.displayTitle.capitalize() + ":"),
                        value = field.value.asDisplayString(resourceProvider)
                    )
                )
            } else {
                listOf(
                    TableItemHorizontalMlcData(
                        title = UiText.DynamicString(field.displayTitle.capitalize() + ":"),
                        value = field.value.asDisplayString(resourceProvider)
                    )
                ) + field.children.toAccordionTableItems()
            }
        }
    }

    private fun String.capitalize() = this.replaceFirstChar {
        if (it.isLowerCase()) {
            it.titlecase(resourceProvider.getLocale())
        } else {
            it.toString()
        }
    }

    private fun TransactionLogDataDomain.transactionTypeTitle(): String {
        return when (this) {
            is TransactionLogDataDomain.PresentationLog -> resourceProvider.getString(R.string.transaction_type_sharing)
            is TransactionLogDataDomain.IssuanceLog -> resourceProvider.getString(R.string.transaction_type_issuing)
            is TransactionLogDataDomain.SigningLog -> resourceProvider.getString(R.string.transaction_type_signing)
            is TransactionLogDataDomain.DeletingLog -> resourceProvider.getString(R.string.transaction_type_deleting)
        }
    }
}
