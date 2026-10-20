package ua.govstack.digitalwallet.app.core.models.common_compose.org.card


import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import ua.govstack.digitalwallet.app.core.models.common_compose.mlc.list.ListItemMlc

@Parcelize
@JsonClass(generateAdapter = true)
data class CardHorizontalScrollItem(
    @Json(name = "listItemMlc")
    val listItemMlc: ListItemMlc?
): Parcelable