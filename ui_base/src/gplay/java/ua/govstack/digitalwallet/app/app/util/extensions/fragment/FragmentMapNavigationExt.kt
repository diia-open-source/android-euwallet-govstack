package ua.govstack.digitalwallet.app.app.util.extensions.fragment

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment

fun Fragment.openNavigationMap(lat: Double, lng: Double, title: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?daddr=$lat,$lng"))
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        fallbackToDefaultNavIntent(lat, lng, title)
    }
}

private fun Fragment.fallbackToDefaultNavIntent(
    lat: Double,
    lng: Double,
    title: String,
) {
    val intent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("geo:<$lat>,<$lng>?q=<$lat>,<$lng>(${title})")
    )
    try {
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        e.printStackTrace()
    }
}