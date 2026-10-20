package ua.govstack.digitalwallet.app.wallet_sharing.domain.serializer

import com.google.gson.Gson

interface UiSerializable

interface UiSerializableParser {
    val serializedKeyName: String
    fun provideParser(): Gson {
        return Gson()
    }
}