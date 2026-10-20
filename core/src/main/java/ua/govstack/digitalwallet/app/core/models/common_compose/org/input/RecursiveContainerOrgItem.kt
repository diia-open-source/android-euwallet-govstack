package ua.govstack.digitalwallet.app.core.models.common_compose.org.input

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ua.govstack.digitalwallet.app.core.models.common_compose.org.container.BackgroundWhiteOrg
import ua.govstack.digitalwallet.app.core.models.common_compose.org.container.InputBlockOrg

@JsonClass(generateAdapter = true)
data class RecursiveContainerOrgItem(
    @Json(name = "backgroundWhiteOrg")
    val backgroundWhiteOrg: BackgroundWhiteOrg?,
    @Json(name = "inputBlockOrg")
    val inputBlockOrg: InputBlockOrg?
)