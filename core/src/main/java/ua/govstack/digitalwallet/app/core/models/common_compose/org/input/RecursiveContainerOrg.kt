package ua.govstack.digitalwallet.app.core.models.common_compose.org.input

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ua.govstack.digitalwallet.app.core.models.common_compose.atm.button.BtnAddOptionAtm
import ua.govstack.digitalwallet.app.core.models.common_compose.atm.button.BtnWhiteLargeIconAtm
import ua.govstack.digitalwallet.app.core.models.common_compose.org.container.BackgroundWhiteOrg
import ua.govstack.digitalwallet.app.core.models.common_compose.org.container.InputBlockOrg

@JsonClass(generateAdapter = true)
data class RecursiveContainerOrg(
    @Json(name = "componentId")
    val componentId: String?,
    @Json(name = "inputCode")
    val inputCode: String?,
    @Json(name = "items")
    val items: List<RecursiveContainerOrgItem>?,
    @Json(name = "mandatory")
    val mandatory: Boolean?,
    @Json(name = "maxNumber")
    val maxNumber: Int?,
    @Json(name = "template")
    val template: Template?,
    @Json(name = "btnWhiteLargeIconAtm")
    val btnWhiteLargeIconAtm: BtnWhiteLargeIconAtm?,
    @Json(name = "btnAddOptionAtm")
    val btnAddOptionAtm: BtnAddOptionAtm?
) {
    @JsonClass(generateAdapter = true)
    data class Template(
        @Json(name = "backgroundWhiteOrg")
        val backgroundWhiteOrg: BackgroundWhiteOrg?,
        @Json(name = "inputBlockOrg")
        val inputBlockOrg: InputBlockOrg?
    )
}