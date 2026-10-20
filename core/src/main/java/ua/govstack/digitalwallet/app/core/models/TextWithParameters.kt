package ua.govstack.digitalwallet.app.core.models

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import ua.govstack.digitalwallet.app.core.models.common.message.TextParameter

@JsonClass(generateAdapter = true)
@Parcelize
data class TextWithParameters(
    @Json(name = "parameters")
    val parameters: List<TextParameter>?,
    @Json(name = "text")
    val text: String?
): Parcelable