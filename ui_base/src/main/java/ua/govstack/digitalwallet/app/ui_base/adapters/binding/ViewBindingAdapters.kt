package ua.govstack.digitalwallet.app.ui_base.adapters.binding

import android.view.View
import androidx.annotation.ColorRes
import androidx.databinding.BindingAdapter
import ua.govstack.digitalwallet.app.core.util.extensions.context.getColorCompatSafe

fun View.setBackgroundCompat(@ColorRes colorRes: Int?) {
    context.getColorCompatSafe(colorRes)?.run(this::setBackgroundColor)
}