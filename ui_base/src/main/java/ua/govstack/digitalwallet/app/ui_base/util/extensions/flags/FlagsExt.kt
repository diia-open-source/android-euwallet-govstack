package ua.govstack.digitalwallet.app.ui_base.util.extensions.flags

import android.content.Context
import ua.govstack.digitalwallet.app.ui_base.R

fun String.toFlagDescription(context: Context): String {
    return when (this) {
        "🇺🇦" -> context.getString(R.string.accessibility_ukraine_flag)
        "🇬🇧" -> context.getString(R.string.accessibility_gb_flag)
        "🇩🇪" -> context.getString(R.string.accessibility_germany_flag)
        "🇫🇷" -> context.getString(R.string.accessibility_france_flag)
        "🇮🇹" -> context.getString(R.string.accessibility_italy_flag)
        "🇪🇸" -> context.getString(R.string.accessibility_spain_flag)
        "🇸🇪" -> context.getString(R.string.accessibility_sweden_flag)
        "🇵🇱" -> context.getString(R.string.accessibility_poland_flag)
        "🇬🇪" -> context.getString(R.string.accessibility_georgia_flag)
        "🇵🇹" -> context.getString(R.string.accessibility_portugal_flag)
        "🇳🇴" -> context.getString(R.string.accessibility_norway_flag)
        "🇫🇮" -> context.getString(R.string.accessibility_finland_flag)
        "🇦🇲" -> context.getString(R.string.accessibility_armenia_flag)
        "🇭🇷" -> context.getString(R.string.accessibility_croatia_flag)
        "🇸🇰" -> context.getString(R.string.accessibility_slovakia_flag)
        "🇨🇿" -> context.getString(R.string.accessibility_czechia_flag)
        "🇱🇻" -> context.getString(R.string.accessibility_latvia_flag)
        "🇩🇰" -> context.getString(R.string.accessibility_denmark_flag)
        "🇪🇪" -> context.getString(R.string.accessibility_estonia_flag)
        "🇺🇸" -> context.getString(R.string.accessibility_usa_flag)
        "🇨🇦" -> context.getString(R.string.accessibility_canada_flag)
        "🇲🇽" -> context.getString(R.string.accessibility_mexico_flag)
        "🇨🇴" -> context.getString(R.string.accessibility_colombia_flag)
        "🇧🇷" -> context.getString(R.string.accessibility_brazil_flag)
        "🇦🇿" -> context.getString(R.string.accessibility_azerbaijan_flag)
        "🇰🇿" -> context.getString(R.string.accessibility_kazakhstan_flag)
        "🇸🇾" -> context.getString(R.string.accessibility_syria_flag)
        "🇯🇵" -> context.getString(R.string.accessibility_japan_flag)
        "🇦🇺" -> context.getString(R.string.accessibility_australia_flag)
        "🇳🇿" -> context.getString(R.string.accessibility_new_zealand_flag)
        "🇲🇾" -> context.getString(R.string.accessibility_malaysia_flag)
        "🇹🇼" -> context.getString(R.string.accessibility_taiwan_flag)
        else -> ""
    }
}