package ua.govstack.digitalwallet.app.core.models.common_compose.mlc.button

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import ua.govstack.digitalwallet.app.core.models.common_compose.atm.button.BtnLoadPlainIconAtm

@Parcelize
@JsonClass(generateAdapter = true)
data class Item(
    @Json(name = "btnLoadPlainIconAtm")
    val btnLoadPlainIconAtm: BtnLoadPlainIconAtm,
) : Parcelable