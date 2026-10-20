package ua.govstack.digitalwallet.app.core.models.common_compose.table

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import ua.govstack.digitalwallet.app.core.models.common_compose.general.PaddingMode

@Parcelize
@JsonClass(generateAdapter = true)
data class HeadingWithSubtitlesMlc(
    @Json(name = "componentId")
    val componentId: String? = null,
    @Json(name = "paddingMode")
    val paddingMode: PaddingMode? = null,
    @Json(name = "subtitles")
    val subtitles: List<String>? = null,
    @Json(name = "value")
    val value: String? = null,
    @Json(name = "maxLines")
    val maxLines: Int = 2,
    @Json(name = "isAutoSized")
    val isAutoSized: Boolean = true
) : Parcelable
