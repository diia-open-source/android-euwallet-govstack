package ua.govstack.digitalwallet.app.core.models.common_compose.org.checkbox

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ua.govstack.digitalwallet.app.core.models.common_compose.mlc.checkbox.SmallCheckIconMlc

@JsonClass(generateAdapter = true)
data class SmallCheckIconItem(
    @Json(name = "smallCheckIconMlc")
    val smallCheckIconMlc: SmallCheckIconMlc
)
