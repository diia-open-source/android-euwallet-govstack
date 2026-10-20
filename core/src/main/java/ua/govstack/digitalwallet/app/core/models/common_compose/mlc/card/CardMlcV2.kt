package ua.govstack.digitalwallet.app.core.models.common_compose.mlc.card


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ua.govstack.digitalwallet.app.core.models.common.message.AttentionIconMessageMlc
import ua.govstack.digitalwallet.app.core.models.common_compose.atm.chip.ChipStatusAtm
import ua.govstack.digitalwallet.app.core.models.common_compose.atm.icon.IconUrlAtm
import ua.govstack.digitalwallet.app.core.models.common_compose.atm.icon.SmallIconAtm
import ua.govstack.digitalwallet.app.core.models.common_compose.general.Action
import ua.govstack.digitalwallet.app.core.models.common_compose.general.PaddingMode

@JsonClass(generateAdapter = true)
data class CardMlcV2(
    @Json(name = "action")
    val action: Action?,
    @Json(name = "chipStatusAtm")
    val chipStatusAtm: ChipStatusAtm?,
    @Json(name = "chips")
    val chips: List<Chip>?,
    @Json(name = "componentId")
    val componentId: String?,
    @Json(name = "descriptions")
    val descriptions: List<String>?,
    @Json(name = "iconUrlAtm")
    val iconUrlAtm: IconUrlAtm?,
    @Json(name = "label")
    val label: String?,
    @Json(name = "smallIconAtm")
    val smallIconAtm: SmallIconAtm?,
    @Json(name = "smallIconAtmWithStates")
    val smallIconAtmWithStates: SmallIconAtmWithStates?,
    @Json(name = "paddingMode")
    val paddingMode: PaddingMode?,
    @Json(name = "rows")
    val rows: List<String>?,
    @Json(name = "rightLabel")
    val rightLabel: String?,
    @Json(name = "attentionIconMessageMlc")
    val attentionIconMessageMlc: AttentionIconMessageMlc?,
) {

    data class Chip(
        @Json(name = "chipStatusAtm")
        val chipStatusAtm: ChipStatusAtm?,
    )

    data class SmallIconAtmWithStates(
        @Json(name = "currentState")
        val currentState: String,
        @Json(name = "states")
        val states: List<SmallIconState>
    )

    data class SmallIconState(
        @Json(name = "name")
        val name: String,
        @Json(name = "icon")
        val icon: SmallIconAtm
    )

}