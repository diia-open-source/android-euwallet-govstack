package ua.govstack.digitalwallet.app.wallet_core.documents.model

sealed class ExpandableListItemUi {
    abstract val header: Header

    data class SingleListItem(
        override val header: Header,
    ) : ExpandableListItemUi()

    data class NestedListItem(
        override val header: Header,
        val nestedItems: List<ExpandableListItemUi>,
        val isExpanded: Boolean,
    ) : ExpandableListItemUi()
}

data class Header(val headerId: String, val value: String, val isSelected: Boolean)