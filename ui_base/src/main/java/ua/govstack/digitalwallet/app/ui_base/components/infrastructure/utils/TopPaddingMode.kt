package ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ua.govstack.digitalwallet.app.core.models.common_compose.general.ElementPaddingMode

enum class TopPaddingMode {
    NONE, MEDIUM, LARGE
}

fun ElementPaddingMode?.toTopPaddingMode(): TopPaddingMode? = when (this) {
    ElementPaddingMode.NONE -> TopPaddingMode.NONE
    ElementPaddingMode.MEDIUM -> TopPaddingMode.MEDIUM
    ElementPaddingMode.LARGE -> TopPaddingMode.LARGE
    else -> null
}

fun TopPaddingMode?.toDp(defaultPadding: Dp): Dp = when (this) {
    TopPaddingMode.NONE -> 0.dp
    TopPaddingMode.MEDIUM -> 8.dp
    TopPaddingMode.LARGE -> 16.dp
    else -> defaultPadding
}