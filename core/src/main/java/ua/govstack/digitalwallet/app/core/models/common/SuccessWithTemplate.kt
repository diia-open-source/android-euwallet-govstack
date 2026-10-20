package ua.govstack.digitalwallet.app.core.models.common

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ua.govstack.digitalwallet.app.core.models.dialogs.TemplateDialogModel

@JsonClass(generateAdapter = true)
class SuccessWithTemplate(
    @Json(name = "success")
    val success: Boolean?,
    @Json(name = "template")
    val template: TemplateDialogModel?
)