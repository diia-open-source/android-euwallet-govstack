package ua.govstack.digitalwallet.app.core.models.common

data class DeferredIssuingEvent(
    val status: String,
    val error: String? = null,
)
