package ua.govstack.digitalwallet.app.wallet_sharing.domain.model

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import ua.govstack.digitalwallet.app.wallet_sharing.domain.serializer.UiSerializable
import ua.govstack.digitalwallet.app.wallet_sharing.domain.serializer.UiSerializableParser
import ua.govstack.digitalwallet.app.wallet_sharing.domain.serializer.SerializableTypeAdapter

sealed interface PresentationMode {
    data class OpenId4Vp(val uri: String) : PresentationMode
    object Ble : PresentationMode
}

data class RequestUriConfig(
    val mode: PresentationMode
) : UiSerializable {

    companion object Parser : UiSerializableParser {
        override val serializedKeyName = "requestUriConfig"

        override fun provideParser(): Gson {
            return GsonBuilder().registerTypeAdapter(
                PresentationMode::class.java,
                SerializableTypeAdapter<PresentationMode>()
            ).create()
        }
    }
}

fun RequestUriConfig.toDomainConfig(): SharingSessionConfig {
    return when (mode) {
        is PresentationMode.Ble -> SharingSessionConfig.Ble
        is PresentationMode.OpenId4Vp -> SharingSessionConfig.OpenId4VP(
            mode.uri,
        )
    }
}