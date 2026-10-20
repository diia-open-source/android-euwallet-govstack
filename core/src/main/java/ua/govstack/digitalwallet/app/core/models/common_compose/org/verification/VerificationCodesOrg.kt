package ua.govstack.digitalwallet.app.core.models.common_compose.org.verification

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import ua.govstack.digitalwallet.app.core.models.common_compose.mlc.scancode.BarCodeMlc
import ua.govstack.digitalwallet.app.core.models.common_compose.mlc.scancode.QrCodeMlc
import ua.govstack.digitalwallet.app.core.models.common_compose.mlc.text.StubMessageMlc
import ua.govstack.digitalwallet.app.core.models.common_compose.org.group.ToggleButtonGroupOrg
import ua.govstack.digitalwallet.app.core.models.common_compose.subatomic.ExpireLabel

@Parcelize
@JsonClass(generateAdapter = true)
data class VerificationCodesOrg(
    @Json(name = "componentId")
    val componentId: String?,
    @Json(name = "EN")
    val en: Localization?,
    @Json(name = "UA")
    val ua: Localization?
) : Parcelable {

    @Parcelize
    @JsonClass(generateAdapter = true)
    data class Localization(
        @Json(name = "barCodeMlc")
        val barCodeMlc: BarCodeMlc?,
        @Json(name = "expireLabel")
        val expireLabel: ExpireLabel?,
        @Json(name = "qrCodeMlc")
        val qrCodeMlc: QrCodeMlc,
        @Json(name = "stubMessageMlc")
        val stubMessageMlc: StubMessageMlc?,
        @Json(name = "toggleButtonGroupOrg")
        val toggleButtonGroupOrg: ToggleButtonGroupOrg?
    ): Parcelable
}