package ua.govstack.digitalwallet.app.core.models.common_compose.atm

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SpacerAtm(
    @Json(name = "value")
    val type: String?,
    @Json(name = "componentId")
    val componentId: String? = null,
)