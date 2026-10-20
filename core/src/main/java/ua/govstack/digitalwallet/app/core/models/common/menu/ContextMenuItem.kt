package ua.govstack.digitalwallet.app.core.models.common.menu

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import ua.govstack.digitalwallet.app.core.models.ContextMenuField

@Keep
@Parcelize
@JsonClass(generateAdapter = true)
data class ContextMenuItem(
    @Json(name = "type")
    val type: String,
    @Json(name = "name")
    val name: String,
    @Json(name = "code")
    val code: String?,
) : Parcelable, ContextMenuField {

    override fun getActionType() = type

    override fun getSubType(): String? = code

    override fun getDisplayName() = name

}