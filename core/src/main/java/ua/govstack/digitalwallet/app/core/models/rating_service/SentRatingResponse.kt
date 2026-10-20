package ua.govstack.digitalwallet.app.core.models.rating_service


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ua.govstack.digitalwallet.app.core.models.dialogs.TemplateDialogModel

@JsonClass(generateAdapter = true)
data class SentRatingResponse(
    @Json(name = "processCode")
    val processCode: Long?,
    @Json(name = "template")
    val template: TemplateDialogModel?
)