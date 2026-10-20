package ua.govstack.digitalwallet.app.ui_base.components.molecule.loading

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ua.govstack.digitalwallet.app.ui_base.components.theme.BlackSqueeze

@Composable
fun CircularLoaderBodyBlock(
    modifier: Modifier = Modifier,
    backgroundColor: Color = BlackSqueeze.copy(alpha = 0.6f),
    backgroundBlur: Dp = 8.dp
) {
    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(backgroundBlur)
                .background(backgroundColor)
        )
       CircularLoaderMolecule()
    }
}
