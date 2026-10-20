package ua.govstack.digitalwallet.app.ui_base.fragments

import android.content.ClipData
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import ua.govstack.digitalwallet.app.core.util.extensions.context.serviceClipboard
import ua.govstack.digitalwallet.app.ui_base.util.view.showTopSnackBar

fun Fragment.copyToClipboard(
    label: String,
    content: String,
    @StringRes messageResId: Int,
    topPadding: Float
) {
    val clip = ClipData.newPlainText(label, content)
    (context ?: return).serviceClipboard?.setPrimaryClip(clip)
    view?.showTopSnackBar(messageResId, Snackbar.LENGTH_LONG, topPadding)
}