package ua.govstack.digitalwallet.app.core.models.common_compose.org.payment

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ua.govstack.digitalwallet.app.core.models.common_compose.table.Item
import ua.govstack.digitalwallet.app.core.models.common_compose.table.TableMainHeadingMlc
import ua.govstack.digitalwallet.app.core.models.common_compose.table.TableSecondaryHeadingMlc

@JsonClass(generateAdapter = true)
data class PaymentInfoOrg(
    @Json(name = "componentId")
    val componentId: String? = null,
    @Json(name = "tableMainHeadingMlc")
    val tableMainHeadingMlc: TableMainHeadingMlc? = null,
    @Json(name = "tableSecondaryHeadingMlc")
    val tableSecondaryHeadingMlc: TableSecondaryHeadingMlc? = null,
    @Json(name = "items")
    val items: List<Item>
)