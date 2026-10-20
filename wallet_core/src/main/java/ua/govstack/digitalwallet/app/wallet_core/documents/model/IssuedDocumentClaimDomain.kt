package ua.govstack.digitalwallet.app.wallet_core.documents.model

import ua.govstack.digitalwallet.app.ui_base.provider.ResourceProvider
import ua.govstack.digitalwallet.app.wallet_core.documents.helper.getBooleanValue
import ua.govstack.digitalwallet.app.wallet_core.documents.helper.getGenderDisplayValue

data class IssuedDocumentClaimDomain(
    val identifier: String,
    val value: Value?,
    val displayTitle: String,
    val children: List<IssuedDocumentClaimDomain> = emptyList(),
) {
    sealed class Value {
        data class Text(val value: String) : Value()
        data class BooleanValue(val value: Boolean) : Value()
        data class Base64Image(val value: String) : Value()
        data class Gender(val value: String) : Value()
        data class Date(val value: String) : Value()
    }
}

fun IssuedDocumentClaimDomain.Value?.asDisplayString(resourceProvider: ResourceProvider): String {
    return when (this) {
        is IssuedDocumentClaimDomain.Value.Text -> value
        is IssuedDocumentClaimDomain.Value.BooleanValue -> getBooleanValue(value, resourceProvider)
        is IssuedDocumentClaimDomain.Value.Base64Image -> ""
        is IssuedDocumentClaimDomain.Value.Gender -> getGenderDisplayValue(value, resourceProvider)
        is IssuedDocumentClaimDomain.Value.Date -> value
        null -> ""
    }
}

fun IssuedDocumentClaimDomain.Value?.asDisplayString(): String {
    return when (this) {
        is IssuedDocumentClaimDomain.Value.Text -> value
        is IssuedDocumentClaimDomain.Value.BooleanValue -> value.toString()
        is IssuedDocumentClaimDomain.Value.Base64Image -> ""
        is IssuedDocumentClaimDomain.Value.Gender -> value
        is IssuedDocumentClaimDomain.Value.Date -> value
        null -> ""
    }
}

fun IssuedDocumentClaimDomain.Value?.isNotBlank(): Boolean {
    return asDisplayString().isNotBlank()
}
