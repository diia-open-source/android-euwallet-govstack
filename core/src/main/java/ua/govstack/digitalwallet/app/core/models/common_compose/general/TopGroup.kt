package ua.govstack.digitalwallet.app.core.models.common_compose.general

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ua.govstack.digitalwallet.app.core.models.common_compose.mlc.header.NavigationPanelMlc
import ua.govstack.digitalwallet.app.core.models.common_compose.mlc.header.NavigationPanelMlcV2
import ua.govstack.digitalwallet.app.core.models.common_compose.mlc.input.SearchInputMlc
import ua.govstack.digitalwallet.app.core.models.common_compose.mlc.text.ScalingTitleMlc
import ua.govstack.digitalwallet.app.core.models.common_compose.org.chip.ChipTabsOrg
import ua.govstack.digitalwallet.app.core.models.common_compose.org.chip.MapChipTabsOrg
import ua.govstack.digitalwallet.app.core.models.common_compose.org.header.TopGroupOrg
import ua.govstack.digitalwallet.app.core.models.common_compose.org.input.SearchBarOrg

@JsonClass(generateAdapter = true)
data class TopGroup(
    @Json(name = "topGroupOrg")
    val topGroupOrg: TopGroupOrg?,
    @Json(name = "searchInputMlc")
    val searchInputMlc: SearchInputMlc?,
    @Json(name = "searchBarOrg")
    val searchBarOrg: SearchBarOrg?,
    @Json(name = "chipTabsOrg")
    val chipTabsOrg: ChipTabsOrg?,
    @Json(name = "navigationPanelMlc")
    val navigationPanelMlc: NavigationPanelMlc?,
    @Json(name = "mapChipTabsOrg")
    val mapChipTabsOrg: MapChipTabsOrg? = null,
    @Json(name = "scalingTitleMlc")
    val scalingTitleMlc: ScalingTitleMlc? = null,
    @Json(name = "navigationPanelMlcV2")
    val navigationPanelMlcV2: NavigationPanelMlcV2? = null
)