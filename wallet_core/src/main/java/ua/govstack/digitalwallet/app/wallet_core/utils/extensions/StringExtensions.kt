package ua.govstack.digitalwallet.app.wallet_core.utils.extensions

import android.net.Uri
import android.net.Uri.EMPTY
import android.util.Base64

fun String.decodeFromBase64(): String = Base64.decode(
    this, Base64.NO_WRAP or Base64.NO_PADDING or Base64.URL_SAFE
).toString(Charsets.UTF_8)

/**
 * Converts a string to a [Uri].
 *
 * If the string is a valid URI, it will be parsed and returned as a [Uri] object.
 * If the string is not a valid URI, an empty [Uri] ([Uri.EMPTY]) will be returned instead.
 * This prevents crashes due to invalid URI strings and provides a fallback mechanism.
 *
 * @return The parsed [Uri] if the string is a valid URI, otherwise [Uri.EMPTY].
 */
fun String.toUri(): Uri = try {
    Uri.parse(this)
} catch (_: Exception) {
    EMPTY
}
