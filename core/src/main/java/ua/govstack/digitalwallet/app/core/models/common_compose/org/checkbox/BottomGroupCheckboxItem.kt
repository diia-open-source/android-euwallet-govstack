package ua.govstack.digitalwallet.app.core.models.common_compose.org.checkbox

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import ua.govstack.digitalwallet.app.core.models.common_compose.mlc.checkbox.CheckboxSquareMlc

@Parcelize
@JsonClass(generateAdapter = true)
data class BottomGroupCheckboxItem(
    @Json(name = "checkboxSquareMlc")
    val checkboxSquareMlc: CheckboxSquareMlc
): Parcelable