package ua.govstack.digitalwallet.app.core.models.common_compose.org.container

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ua.govstack.digitalwallet.app.core.models.common_compose.general.Body

@JsonClass(generateAdapter = true)
data class UpdatedContainerOrg(
    @Json(name = "componentId")
    val componentId: String? = null,
    @Json(name = "id")
    val id: String? = null,
    @Json(name = "items")
    val items: List<Body>? = null,
)