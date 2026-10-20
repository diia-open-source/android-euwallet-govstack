package ua.govstack.digitalwallet.app.core.util.extensions.uri

import android.net.Uri

fun Uri.getQueryParameterSafe(key: String): String? {
    return try {
        getQueryParameter(key)
    } catch (e: Exception) {
        null
    }
}