package ua.govstack.digitalwallet.app.wallet_gallery.ui.navigation

import ua.govstack.digitalwallet.app.core.models.ConsumableItem
import ua.govstack.digitalwallet.app.core.models.common.BackStackEvent

data class RemoveDocumentBackStackResult(val data: ConsumableItem) : BackStackEvent

data class RemoveDocBackStackResult(val data: ConsumableItem) : BackStackEvent

data class ConfirmRemoveDocBackStackResult(val data: ConsumableItem) : BackStackEvent

data class DocUserActionResult(val data: ConsumableItem) : BackStackEvent