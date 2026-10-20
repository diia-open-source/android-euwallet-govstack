package ua.govstack.digitalwallet.app.ui_base.mappers.document

import ua.govstack.digitalwallet.app.core.models.document.DocumentContextMenuAction
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.DataActionWrapper
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.UiIcon
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.UiText
import ua.govstack.digitalwallet.app.ui_base.components.molecule.list.ListItemMlcData

fun DocumentContextMenuAction.toListItemMlcData(
    label: UiText,
    iconLeft: UiIcon.DrawableResource
): ListItemMlcData {
    return ListItemMlcData(
        actionKey = this.action,
        id = this.action,
        label = label,
        iconLeft = iconLeft,
        action = DataActionWrapper(type = this.action)
    )
}
