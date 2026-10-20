package ua.govstack.digitalwallet.app.wallet_core.ui.extensions

import android.content.ClipData
import android.view.View
import com.google.android.material.snackbar.Snackbar
import ua.govstack.digitalwallet.app.core.util.extensions.context.serviceClipboard
import ua.govstack.digitalwallet.app.ui_base.util.view.showTopSnackBar
import ua.govstack.digitalwallet.app.wallet_core.R


fun View.showCopyDocIdClippedSnackBar(docId: String, topPadding: Float) {
    val clip = ClipData.newPlainText("docId", docId)

    context.serviceClipboard
        ?.setPrimaryClip(clip)

    showTopSnackBar(R.string.doc_id_copied, Snackbar.LENGTH_LONG, topPadding)
}
