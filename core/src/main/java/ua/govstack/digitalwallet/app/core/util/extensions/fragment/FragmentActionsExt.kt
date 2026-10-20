package ua.govstack.digitalwallet.app.core.util.extensions.fragment

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.StringRes
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import ua.govstack.digitalwallet.app.core.util.extensions.context.getStringSafe

fun Fragment.openLink(link: String) {
    context?.openLink(link)
}

fun Context.openLink(link: String) {
    if (link.startsWith("https:")) {
        val uri = try {
            link.toUri()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = uri
        }
        startActivity(intent)
    }
}

fun Context.openPlayMarket() {
    val uri = Uri.parse("market://details?id=ua.govstack.digitalwallet.app.app")
    val goToMarket = Intent(Intent.ACTION_VIEW, uri).apply {
        addFlags(
            Intent.FLAG_ACTIVITY_NO_HISTORY
                    or Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET
                    or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        )
    }
    try {
        startActivity(goToMarket)
    } catch (e: ActivityNotFoundException) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://play.google.com/store/apps/details?id=ua.govstack.digitalwallet.app.app")
            )
        )
        e.printStackTrace()
    }
}

fun Context.handlePhoneIntent(data: String) {
    val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", data, null)) ;
    startActivity(intent)
}

fun Fragment.shareLink(
    link: String,
    @StringRes chooserTitle: Int? = null
) {
    val chooserTitleDisplayString = requireContext().getStringSafe(chooserTitle)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, link)
    }
    startActivity(Intent.createChooser(intent, chooserTitleDisplayString))
}