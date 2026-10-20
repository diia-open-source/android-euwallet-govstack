package ua.govstack.digitalwallet.app.core.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ua.govstack.digitalwallet.app.core.models.dialogs.TemplateDialogModel

@JsonClass(generateAdapter = true)
data class ResourceUpdateResponse(
    @Json(name = "resourceId")
    val resourceId: String?,
    @Json(name = "template")
    val template: TemplateDialogModel?
)