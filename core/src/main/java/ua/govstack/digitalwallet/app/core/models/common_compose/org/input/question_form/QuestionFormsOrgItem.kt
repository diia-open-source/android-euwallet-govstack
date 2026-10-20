package ua.govstack.digitalwallet.app.core.models.common_compose.org.input.question_form

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ua.govstack.digitalwallet.app.core.models.common_compose.mlc.checkbox.CheckboxSquareMlc
import ua.govstack.digitalwallet.app.core.models.common_compose.mlc.input.InputDateMlc
import ua.govstack.digitalwallet.app.core.models.common_compose.mlc.input.InputTextMlc
import ua.govstack.digitalwallet.app.core.models.common_compose.org.input.InputPhoneCodeOrg

@JsonClass(generateAdapter = true)
data class QuestionFormsOrgItem(
    @Json(name = "id")
    val id: String?,
    @Json(name = "condition")
    val condition: String? = null,
    @Json(name = "inputTextMlc")
    val inputTextMlc: InputTextMlc? = null,
    @Json(name = "inputDateMlc")
    val inputDateMlc: InputDateMlc? = null,
    @Json(name = "selectorOrg")
    val selectorOrg: SelectorOrg? = null,
    @Json(name = "checkboxSquareMlc")
    val checkboxSquareMlc: CheckboxSquareMlc? = null,
    @Json(name = "inputPhoneCodeOrg")
    val inputPhoneCodeOrg: InputPhoneCodeOrg? = null,
)