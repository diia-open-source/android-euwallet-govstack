package ua.govstack.digitalwallet.app.core.models.common_compose.mlc.card


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ua.govstack.digitalwallet.app.core.models.common_compose.atm.icon.DoubleIconAtm
import ua.govstack.digitalwallet.app.core.models.common_compose.atm.icon.IconAtm
import ua.govstack.digitalwallet.app.core.models.common_compose.atm.icon.LargeIconAtm
import ua.govstack.digitalwallet.app.core.models.common_compose.atm.icon.SmallIconAtm
import ua.govstack.digitalwallet.app.core.models.common_compose.general.Action

@JsonClass(generateAdapter = true)
data class WhiteCardMlc(
    @Json(name = "action")
    val action: Action?,
    @Json(name = "doubleIconAtm")
    val doubleIconAtm: DoubleIconAtm?,
    @Json(name = "iconAtm")
    val iconAtm: IconAtm?,
    @Json(name = "label")
    val label: String?,
    @Json(name = "smallIconAtm")
    val smallIconAtm: SmallIconAtm?,
    @Json(name = "title")
    val title: String?,
    @Json(name = "largeIconAtm")
    val largeIconAtm: LargeIconAtm?,
    @Json(name = "image")
    val image: String?
)