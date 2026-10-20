package ua.govstack.digitalwallet.app.core.models.common_compose.org.carousel

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ua.govstack.digitalwallet.app.core.models.common_compose.atm.indicators.DotBlackNavigationAtm
import ua.govstack.digitalwallet.app.core.models.common_compose.general.PaddingMode
import ua.govstack.digitalwallet.app.core.models.common_compose.mlc.card.CardImageMlc

@JsonClass(generateAdapter = true)
data class CardImageCarouselOrg(
    @Json(name = "componentId")
    val componentId: String?,
    @Json(name = "dotBlackNavigationAtm")
    val dotBlackNavigationAtm: DotBlackNavigationAtm?,
    @Json(name = "items")
    val items: List<CardImageItem>?,
    @Json(name = "paddingMode")
    val paddingMode: PaddingMode?
) {
    @JsonClass(generateAdapter = true)
    data class CardImageItem(
        @Json(name = "cardImageMlc")
        val cardImageMlc: CardImageMlc?
    )
}