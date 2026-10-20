package ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.isUnspecified
import ua.govstack.digitalwallet.app.ui_base.components.theme.Black
import ua.govstack.digitalwallet.app.ui_base.components.theme.DiiaTextStyle
import ua.govstack.digitalwallet.app.ui_base.util.extensions.language.detectLanguageCode

@Composable
fun AutoSizeLimitedText(
    text: String,
    style: TextStyle = DiiaTextStyle.h2MediumHeading,
    maxLines: Int,
    modifier: Modifier,
    color: Color = Black,
    useAutoSize: Boolean = true
) {
    var resizedStyle by remember {
        mutableStateOf(style)
    }
    var shouldDraw by remember {
        mutableStateOf(false)
    }

    val defStyle = DiiaTextStyle.h2MediumHeading.fontSize

    Text(
        text = buildAnnotatedString {
            val textContent = text
            val languageCode = textContent.detectLanguageCode()
            withStyle(style = SpanStyle(localeList = LocaleList(languageCode))) {
                append(textContent)
            }
        },
        color = color,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier.drawWithContent {
            if (shouldDraw) {
                drawContent()
            }
        },
        style = resizedStyle,
        onTextLayout = { result ->
            if (result.didOverflowHeight and useAutoSize) {
                if (style.fontSize.isUnspecified) {
                    resizedStyle = resizedStyle.copy(
                        fontSize = defStyle
                    )
                }
                resizedStyle = resizedStyle.copy(
                    fontSize = resizedStyle.fontSize * 0.95
                )
            } else {
                shouldDraw = true
            }
        }
    )
}