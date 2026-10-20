package ua.govstack.digitalwallet.app.core.models.common

import ua.govstack.digitalwallet.app.core.models.ConsumableItem
import ua.govstack.digitalwallet.app.core.models.ConsumableString

interface BackStackEvent {

    data object Empty : BackStackEvent

    data class UserActionResult(val data: ConsumableString) : BackStackEvent

    data class TemplateRetryResult(val data: ConsumableString) : BackStackEvent

}
