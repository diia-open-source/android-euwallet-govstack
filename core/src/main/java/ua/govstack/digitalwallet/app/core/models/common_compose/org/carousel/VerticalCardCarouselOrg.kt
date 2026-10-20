package ua.govstack.digitalwallet.app.core.models.common_compose.org.carousel


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ua.govstack.digitalwallet.app.core.models.common_compose.mlc.card.VerticalCardMlc

@JsonClass(generateAdapter = true)
data class VerticalCardCarouselOrg(
    @Json(name = "items")
    val items: List<Item>
) {
    @JsonClass(generateAdapter = true)
    data class Item(
        @Json(name = "verticalCardMlc")
        val verticalCardMlc: VerticalCardMlc
    )
}
