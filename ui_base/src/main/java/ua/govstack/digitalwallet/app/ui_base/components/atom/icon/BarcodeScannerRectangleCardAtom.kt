package ua.govstack.digitalwallet.app.ui_base.components.atom.icon

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.govstack.digitalwallet.app.ui_base.components.theme.BlackAlpha70

@Composable
fun BarcodeScannerRectangleCardAtom(
    modifier: Modifier = Modifier,
    framePosition: (Rect) -> Unit
) {
    val density = LocalDensity.current

    val horizontalPadding = 24.dp
    val scannerPadding = with(density) { horizontalPadding.toPx() }
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    val frameWidth = with(density) { (screenWidth - horizontalPadding * 2).toPx() }
    val frameHeight = frameWidth / 1.5F

    val strokeLength = with(density) { 16.dp.toPx() }
    val strokeWidth = with(density) { 2.dp.toPx() }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer(alpha = 0.99f)
            .onGloballyPositioned {
                framePosition.invoke(
                    Rect(
                        left = scannerPadding,
                        top = it.size.height / 2f - frameHeight / 2,
                        right = it.size.width - scannerPadding,
                        bottom = it.size.height / 2f + frameHeight / 2
                    )
                )
            },
        onDraw = {
            drawRect(
                color = BlackAlpha70,
                size = size
            )
            drawRoundRect(
                color = Color.Transparent,
                size = Size(size.width - scannerPadding * 2, frameHeight),
                topLeft = Offset(scannerPadding, size.height / 2f - frameHeight / 2),
                blendMode = BlendMode.SrcOut,
            )

            val frameTopLeft = Offset(scannerPadding, size.height / 2f - frameHeight / 2)

            // Top(Left)
            drawRect(
                color = Color.White,
                topLeft = frameTopLeft,
                size = Size(strokeLength, strokeWidth),
                style = Stroke(width = strokeWidth)
            )
            drawRect(
                color = Color.White,
                topLeft = frameTopLeft,
                size = Size(strokeWidth, strokeLength),
                style = Stroke(width = strokeWidth)
            )

            // Top(Right)
            drawRect(
                color = Color.White,
                topLeft = Offset(frameTopLeft.x + frameWidth - strokeLength, frameTopLeft.y),
                size = Size(strokeLength, strokeWidth),
                style = Stroke(width = strokeWidth)
            )
            drawRect(
                color = Color.White,
                topLeft = Offset(frameTopLeft.x + frameWidth - strokeWidth, frameTopLeft.y),
                size = Size(strokeWidth, strokeLength),
                style = Stroke(width = strokeWidth)
            )

            // Bottom(Left)
            drawRect(
                color = Color.White,
                topLeft = Offset(frameTopLeft.x, frameTopLeft.y + frameHeight - strokeWidth),
                size = Size(strokeLength, strokeWidth),
                style = Stroke(width = strokeWidth)
            )
            drawRect(
                color = Color.White,
                topLeft = Offset(frameTopLeft.x, frameTopLeft.y + frameHeight - strokeLength),
                size = Size(strokeWidth, strokeLength),
                style = Stroke(width = strokeWidth)
            )

            // Bottom(Right)
            drawRect(
                color = Color.White,
                topLeft = Offset(
                    frameTopLeft.x + frameWidth - strokeLength,
                    frameTopLeft.y + frameHeight - strokeWidth
                ),
                size = Size(strokeLength, strokeWidth),
                style = Stroke(width = strokeWidth)
            )
            drawRect(
                color = Color.White,
                topLeft = Offset(
                    frameTopLeft.x + frameWidth - strokeWidth,
                    frameTopLeft.y + frameHeight - strokeLength
                ),
                size = Size(strokeWidth, strokeLength),
                style = Stroke(width = strokeWidth)
            )
        }
    )
}

@Preview
@Composable
fun BarcodeScannerRectangleCardAtomPreview() {
    BarcodeScannerRectangleCardAtom {}
}