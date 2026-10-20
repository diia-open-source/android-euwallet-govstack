package ua.govstack.digitalwallet.app.core.models.common_compose.mlc.list


import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import ua.govstack.digitalwallet.app.core.models.common.message.TextParameter
import ua.govstack.digitalwallet.app.core.models.common_compose.atm.chip.ChipStatusAtm
import ua.govstack.digitalwallet.app.core.models.common_compose.atm.text.AmountAtm
import ua.govstack.digitalwallet.app.core.models.common_compose.general.Action
import ua.govstack.digitalwallet.app.core.models.document.docgroups.v2.IconLeft
import ua.govstack.digitalwallet.app.core.models.document.docgroups.v2.IconRight

@Parcelize
@JsonClass(generateAdapter = true)
data class ListItemMlc(
    @Json(name = "componentId")
    val componentId: String? = null,
    @Json(name = "id")
    val id: String?,
    @Json(name = "chipStatusAtm")
    val chipStatusAtm: ChipStatusAtm?,
    @Json(name = "label")
    val label: String? = null,
    @Json(name = "description")
    val description: String?,
    @Json(name = "iconLeft")
    val iconLeft: IconLeft?,
    @Json(name = "bigIconLeft")
    val bigIconLeft: BigIconLeft?,
    @Json(name = "leftLogoLink")
    val leftLogoLink: String?,
    @Json(name = "iconRight")
    val iconRight: IconRight?,
    @Json(name = "accessibilityDescription")
    val accessibilityDescription: String? = null,
    @Json(name = "logoLeft")
    val logoLeft: String?,
    @Json(name = "state")
    val state: String?,
    @Json(name = "amountAtm")
    val amountAtm: AmountAtm?,
    @Json(name = "action")
    val action: Action?,
    @Json(name = "dataJson")
    val dataJson: String?,
    @Json(name = "parameters")
    val parameters: List<TextParameter>? = null
) :Parcelable {

    @Parcelize
    @JsonClass(generateAdapter = true)
    data class BigIconLeft(
        @Json(name = "code")
        val code: String?
    ) : Parcelable

}
