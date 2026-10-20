package ua.govstack.digitalwallet.app.opensource.util.extensions.activity

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.view.View
import android.view.Window
import androidx.annotation.RequiresApi
import androidx.core.app.ComponentActivity
import androidx.core.view.WindowCompat
import ua.govstack.digitalwallet.app.core.util.extensions.context.getColorCompat

fun ComponentActivity.overrideConfiguration(context: Context?) {
    if (Build.VERSION.SDK_INT >= 27) {
        val newOverride = Configuration(context?.resources?.configuration)
        newOverride.fontScale = 1.0f
        applyOverrideConfiguration(newOverride)
    }
}

fun ComponentActivity.setUpEdgeToEdge() {
    val impl = when {
        Build.VERSION.SDK_INT >= 35 -> EdgeToEdgeApi35()
        else -> EdgeToEdgeApi26()
    }

    impl.setUp(
        window = window,
        view = findViewById(android.R.id.content),
        theme = theme
    )
}

private interface EdgeToEdgeImpl {
    fun setUp(window: Window, view: View, theme: Resources.Theme)
}

@RequiresApi(35)
private class EdgeToEdgeApi35 : EdgeToEdgeImpl {

    override fun setUp(
        window: Window,
        view: View,
        theme: Resources.Theme
    ) {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }
    }
}

@Suppress("DEPRECATION")
private class EdgeToEdgeApi26 : EdgeToEdgeImpl {

    override fun setUp(
        window: Window,
        view: View,
        theme: Resources.Theme
    ) {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        window.statusBarColor =
            view.context.getColorCompat(android.R.color.transparent, theme)

        window.navigationBarColor =
            view.context.getColorCompat(android.R.color.transparent, theme)
    }
}
