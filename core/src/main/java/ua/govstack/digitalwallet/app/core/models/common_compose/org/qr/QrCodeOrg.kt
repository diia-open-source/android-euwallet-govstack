package ua.govstack.digitalwallet.app.core.models.common_compose.org.qr

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import ua.govstack.digitalwallet.app.core.models.common_compose.atm.button.BtnPlainIconAtm
import ua.govstack.digitalwallet.app.core.models.common_compose.mlc.scancode.QrCodeMlc
import ua.govstack.digitalwallet.app.core.models.common_compose.mlc.text.PaginationMessageMlc
import ua.govstack.digitalwallet.app.core.models.common_compose.subatomic.ExpireLabel

@Parcelize
@JsonClass(generateAdapter = true)
data class QrCodeOrg(
    @Json(name = "componentId")
    val componentId: String,
    @Json(name = "text")
    val text: String? = null,
    @Json(name = "qrCodeMlc")
    val qrCodeMlc: QrCodeMlc?,
    @Json(name = "expireLabelWithoutTimer")
    val expireLabelWithoutTimer: String?,
    @Json(name = "expireLabel")
    val expireLabel: ExpireLabel?,
    @Json(name = "stateAfterExpiration")
    val stateAfterExpiration: StateAfterExpiration?,
    @Json(name = "btnPlainIconAtm")
    val btnPlainIconAtm: BtnPlainIconAtm?,
    @Json(name = "paginationMessageMlc")
    val paginationMessageMlc: PaginationMessageMlc?
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class StateAfterExpiration(
    @Json(name = "paginationMessageMlc")
    val paginationMessageMlc: PaginationMessageMlc
) : Parcelable
