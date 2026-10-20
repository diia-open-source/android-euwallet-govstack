package ua.govstack.digitalwallet.app.core.models.ws

data class ChannelEvent(
    val data: String?,
    val t: Throwable? = null
)