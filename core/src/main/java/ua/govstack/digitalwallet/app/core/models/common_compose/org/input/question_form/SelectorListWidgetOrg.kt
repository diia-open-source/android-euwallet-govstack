package ua.govstack.digitalwallet.app.core.models.common_compose.org.input.question_form

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ua.govstack.digitalwallet.app.core.models.common_compose.mlc.list.ListItemMlc
import ua.govstack.digitalwallet.app.core.models.common_compose.mlc.list.ListWidgetItemMlc

@JsonClass(generateAdapter = true)
data class SelectorListWidgetOrg(
    @Json(name = "componentId")
    val componentId: String? = null,
    @Json(name = "items")
    val items: List<SelectorListWidgetOrgItem>
)

@JsonClass(generateAdapter = true)
data class SelectorListWidgetOrgItem(
    @Json(name = "listItemMlc")
    val listItemMlc: ListItemMlc?,
    @Json(name = "listWidgetItemMlc")
    val listWidgetItemMlc: ListWidgetItemMlc?,
)