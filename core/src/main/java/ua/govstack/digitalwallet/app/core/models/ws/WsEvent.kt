package ua.govstack.digitalwallet.app.core.models.ws

import com.squareup.moshi.Json

abstract class WsEvent(
    @property:Json(name = "event")
    var event: String
)