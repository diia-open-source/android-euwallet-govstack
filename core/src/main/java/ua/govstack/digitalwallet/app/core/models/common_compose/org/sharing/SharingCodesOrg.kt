package ua.govstack.digitalwallet.app.core.models.common_compose.org.sharing


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ua.govstack.digitalwallet.app.core.models.common_compose.mlc.button.BtnLoadIconPlainGroupMlc
import ua.govstack.digitalwallet.app.core.models.common_compose.mlc.scancode.QrCodeMlc
import ua.govstack.digitalwallet.app.core.models.common_compose.mlc.text.StubMessageMlc
import ua.govstack.digitalwallet.app.core.models.common_compose.subatomic.ExpireLabel

@JsonClass(generateAdapter = true)
data class SharingCodesOrg(
    @Json(name = "btnLoadIconPlainGroupMlc")
    val btnLoadIconPlainGroupMlc: BtnLoadIconPlainGroupMlc?,
    @Json(name = "componentId")
    val componentId: String?,
    @Json(name = "expireLabel")
    val expireLabel: ExpireLabel?,
    @Json(name = "qrCodeMlc")
    val qrCodeMlc: QrCodeMlc,
    @Json(name = "stubMessageMlc")
    val stubMessageMlc: StubMessageMlc?
)