package ua.govstack.digitalwallet.app.core.models.common_compose.org.bottom

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import ua.govstack.digitalwallet.app.core.models.common_compose.atm.button.BtnPlainAtm
import ua.govstack.digitalwallet.app.core.models.common_compose.atm.button.BtnPrimaryDefaultAtm

@Parcelize
@JsonClass(generateAdapter = true)
data class BottomGroup(
    @Json(name = "btnPrimaryDefaultAtm")
    val btnPrimaryDefaultAtm: BtnPrimaryDefaultAtm?,
    @Json(name = "btnPlainAtm")
    val plainButton: BtnPlainAtm?
): Parcelable