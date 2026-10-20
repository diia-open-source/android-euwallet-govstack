package ua.govstack.digitalwallet.app.core.models.common

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ua.govstack.digitalwallet.app.core.models.ContextMenuField
import ua.govstack.digitalwallet.app.core.models.common.menu.ContextMenuItem

@JsonClass(generateAdapter = true)
data class NavigationPanel(
    @Json(name = "header")
    val header: String?,
    @Json(name = "contextMenu")
    val contextMenu: List<ContextMenuItem>?,
) {
    val menu: Array<ContextMenuField>
        get() = contextMenu?.toTypedArray() ?: arrayOf()
}