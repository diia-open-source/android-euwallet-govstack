package ua.govstack.digitalwallet.app.wallet_sharing.domain.serializer

import android.util.Base64


interface UiSerializer {
    fun <M : UiSerializable> toBase64(
        model: M,
        parser: UiSerializableParser
    ): String?

    fun <M : UiSerializable> fromBase64(
        payload: String?,
        model: Class<M>,
        parser: UiSerializableParser
    ): M?
}

class UiSerializerImpl : UiSerializer {

    override fun <M : UiSerializable> toBase64(
        model: M,
        parser: UiSerializableParser
    ): String? {
        return try {
            parser.provideParser().toJson(model).encodeToBase64()
        } catch (e: Exception) {
            null
        }
    }

    override fun <M : UiSerializable> fromBase64(
        payload: String?,
        model: Class<M>,
        parser: UiSerializableParser
    ): M? {
        return try {
            parser.provideParser().fromJson(
                payload?.decodeFromBase64(),
                model
            )
        } catch (e: Exception) {
            null
        }
    }
}

fun String.encodeToBase64(): String = Base64.encodeToString(
    this.toByteArray(Charsets.UTF_8),
    Base64.NO_WRAP or Base64.NO_PADDING or Base64.URL_SAFE
)

fun String.decodeFromBase64(): String = Base64.decode(
    this, Base64.NO_WRAP or Base64.NO_PADDING or Base64.URL_SAFE
).toString(Charsets.UTF_8)