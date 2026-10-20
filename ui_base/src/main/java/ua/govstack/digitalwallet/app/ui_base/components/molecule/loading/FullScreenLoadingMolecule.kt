package ua.govstack.digitalwallet.app.ui_base.components.molecule.loading

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ua.govstack.digitalwallet.app.ui_base.R
import ua.govstack.digitalwallet.app.ui_base.components.noRippleClickable
import ua.govstack.digitalwallet.app.ui_base.components.subatomic.loader.WhiteLoaderSpinnerLoaderAtm

@Composable
fun FullScreenLoadingMolecule(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0x99000000),
    backgroundBlur: Dp = 8.dp
) {
    val view = LocalView.current
    val context = LocalContext.current
    val message = context.getString(R.string.accessibility_loading_please_wait)

    LaunchedEffect(Unit) {
        view.announceForAccessibility(message)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .noRippleClickable { // To block bottom layer
            }
            .semantics {
                contentDescription = message
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(backgroundBlur)
                .background(backgroundColor)
        )
        WhiteLoaderSpinnerLoaderAtm(modifier = Modifier.size(24.dp))
    }
}

@Preview
@Composable
fun FullScreenLoadingMoleculePreview() {
    FullScreenLoadingMolecule()
}
