package ua.govstack.digitalwallet.app.ui_base.adapters.binding

import android.widget.Button
import androidx.annotation.StringRes
import androidx.databinding.BindingAdapter
import ua.govstack.digitalwallet.app.core.util.extensions.validateResource

fun Button.setTextRes(@StringRes textRes: Int?) {
    textRes.validateResource { res -> text = context.getString(res) }
}
