package ua.govstack.digitalwallet.app.wallet_transaction.ui.list

import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import eu.europa.ec.eudi.wallet.transactionLogging.TransactionLog
import ua.govstack.digitalwallet.app.ui_base.components.DiiaResourceIcon
import ua.govstack.digitalwallet.app.ui_base.components.atom.icon.SmallIconAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.status.ChipStatusAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.status.StatusChipType
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.DataActionWrapper
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.UIElementData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIActionKeysCompose
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.UiText
import ua.govstack.digitalwallet.app.ui_base.components.molecule.card.CardMlcV2Data
import ua.govstack.digitalwallet.app.ui_base.components.molecule.header.NavigationPanelMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.message.PaginationMessageMlcData
import ua.govstack.digitalwallet.app.ui_base.components.organism.header.TopGroupOrgData
import ua.govstack.digitalwallet.app.ui_base.provider.ResourceProvider
import ua.govstack.digitalwallet.app.wallet_core.utils.TRANSACTION_FULL_DATETIME_PATTERN
import ua.govstack.digitalwallet.app.wallet_core.utils.formatLocalDateTime
import ua.govstack.digitalwallet.app.wallet_transaction.R
import ua.govstack.digitalwallet.app.wallet_transaction.domain.model.TransactionLogDataDomain
import ua.govstack.digitalwallet.app.wallet_transaction.domain.model.TransactionLogDataDomain.Companion.getDisplayName
import ua.govstack.digitalwallet.app.wallet_transaction.util.TransactionsConst
import javax.inject.Inject

class TransactionsMapper @Inject constructor(
    private val resourceProvider: ResourceProvider
) {

    fun toolbar(): UIElementData = TopGroupOrgData(
        navigationPanelMlcData = NavigationPanelMlcData(
            title = UiText.StringResource(R.string.transactions_title), isContextMenuExist = false
        )
    )

    fun emptyBodyItem(): UIElementData = PaginationMessageMlcData(
        id = "",
        title = UiText.StringResource(R.string.transactions_empty_title),
        description = UiText.StringResource(R.string.transactions_empty_description)
    )

    fun transactionCard(transaction: TransactionLogDataDomain): UIElementData {
        val action = DataActionWrapper(
            type = TransactionsConst.TO_TRANSACTION_DETAILS, resource = transaction.id
        )

        return CardMlcV2Data(
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
            smallIconAtm = SmallIconAtmData(
                actionKey = UIActionKeysCompose.SMALL_ICON_ATM_DATA,
                code = DiiaResourceIcon.ARROW_FILL.code,
                action = action
            ),
            chips = listOfNotNull(
                ChipStatusAtmData(
                    type = StatusChipType.WHITE, title = transaction.transactionTypeTitle()
                )
            ),
            action = action,
            rightLabel = UiText.DynamicString(
                transaction.creationLocalDateTime.formatLocalDateTime(
                    TRANSACTION_FULL_DATETIME_PATTERN
                )
            )
        )
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
