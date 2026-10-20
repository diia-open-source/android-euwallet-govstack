package ua.govstack.digitalwallet.app.core.models.common_compose.mlc.checkbox

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import ua.govstack.digitalwallet.app.core.models.common.message.TextParameter

@Parcelize
@JsonClass(generateAdapter = true)
data class CheckboxSquareMlc(
    @Json(name = "componentId")
    val componentId: String? = null,
    @Json(name = "id")
    val id: String?,
    @Json(name = "blocker")
    val blocker: Boolean?,
    @Json(name = "label")
    val label: String,
    @Json(name = "parameters")
    val parameters: List<TextParameter>?,
    @Json(name = "options")
    val options: List<CBSquareItem>?,
    @Json(name = "isSelected")
    val isSelected: Boolean?,
    @Json(name = "isEnabled")
    val isEnabled: Boolean?
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class CBSquareItem(
    @Json(name = "id")
    val id: String,
    @Json(name = "isSelected")
    val isSelected: Boolean
) : Parcelable
