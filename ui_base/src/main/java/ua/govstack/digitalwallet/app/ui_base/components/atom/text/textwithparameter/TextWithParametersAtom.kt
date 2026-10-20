package ua.govstack.digitalwallet.app.ui_base.components.atom.text.textwithparameter

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import ua.govstack.digitalwallet.app.core.models.TextWithParameters
import ua.govstack.digitalwallet.app.ui_base.R
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.UIElementData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIAction
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIActionKeysCompose
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.isTalkBackEnabled
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.UiText
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.toDynamicString
import ua.govstack.digitalwallet.app.ui_base.components.theme.DiiaTextStyle

private const val LINK = "link"
private const val PHONE = "phone"
private const val EMAIL = "email"

@Composable
fun TextWithParametersAtom(
    modifier: Modifier = Modifier,
    data: TextWithParametersData,
    style: TextStyle = DiiaTextStyle.t3TextBody,
    maxLines: Int? = null,
    onUIAction: (UIAction) -> Unit
) {
    val context = LocalContext.current
    val fullText = data.text.asString()
    val isTalkBackEnabled = context.isTalkBackEnabled()

    val annotatedText: AnnotatedString =
        data.text.buildWithMetadataComposeEdition(data.parameters ?: emptyList())

    val clickableTags = setOf(LINK, PHONE, EMAIL)
    val clickableParameters = data.parameters ?: emptyList()
    val clickableMap: Map<String, TextParameter> = clickableParameters
        .filter { it.type in clickableTags }
        .mapNotNull { parameter ->
            val placeholderKey: String? = parameter.data?.name?.asString()?.let { name -> "{$name}" }
            if (placeholderKey != null) {
                placeholderKey to parameter
            } else {
                null
            }
        }
        .toMap()

    val placeholders = clickableMap.keys.toList()
    val containsPlaceholder = placeholders.any { fullText.contains(it) }

    if (isTalkBackEnabled && (containsPlaceholder || fullText.contains('\n'))) {
        val structuredParts = parseTextToStructuredParts(fullText, clickableMap)
        val finalParts = structuredParts.filter {
            it !is String || it.isNotEmpty() || (it.isEmpty() && structuredParts.indexOf(it) != structuredParts.size - 1)
        }

        Column(modifier = modifier) {
            val totalParts = finalParts.size

            finalParts.forEachIndexed { partIndex, part ->
                val isLastPart = partIndex == totalParts - 1

                when (part) {
                    is String -> {
                        if (part.isNotEmpty()) {
                            Text(
                                text = part,
                                style = style,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .focusable()
                            )
                        } else {
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        if (part.isNotEmpty() && !isLastPart) {
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                    is TextParameter -> {
                        val linkPlaceholder =
                            part.data?.name?.asString()?.let { "{$it}" } ?: return@forEachIndexed
                        var finalAlt = part.data.alt
                        val linkDescription = when (part.type) {
                            LINK -> {
                                val linkTextLength = part.data.alt?.asString()?.length ?: 0
                                if (linkTextLength < 25) {
                                    finalAlt =
                                        context.getString(R.string.accessibility_details_at_link)
                                            .toDynamicString()
                                    context.getString(R.string.accessibility_details_at_link)
                                } else {
                                    part.data.alt?.asString() ?: linkPlaceholder
                                }
                            }
                            PHONE, EMAIL -> part.data.alt?.asString() ?: linkPlaceholder
                            else -> linkPlaceholder
                        }

                        val paramData = part.data.copy(alt = finalAlt)
                        val linkAnnotatedText = linkPlaceholder.toDynamicString()
                            .buildWithMetadataComposeEdition(listOf(part.copy(data = paramData)))

                        ClickableText(
                            modifier = Modifier
                                .fillMaxWidth()
                                .semantics {
                                    onClick(label = null, action = null)
                                    contentDescription =
                                        "$linkDescription. ${context.getString(R.string.accessibility_open_in_new_window)}"
                                },
                            text = linkAnnotatedText,
                            style = style,
                        ) {
                            val annotation = linkAnnotatedText.getStringAnnotations(it, it)
                            if (annotation.isNotEmpty()) {
                                when (annotation[0].tag) {
                                    EMAIL -> emailLinkHandler(context, mailto = annotation[0].item)
                                    PHONE -> phoneLinkHandler(context, phone = annotation[0].item)
                                    LINK -> openLink(context, link = annotation[0].item)
                                    else -> onUIAction(
                                        UIAction(
                                            actionKey = data.actionKey,
                                            data = annotation[0].item,
                                            optionalId = annotation[0].tag
                                        )
                                    )
                                }
                            }
                        }
                        if (!isLastPart) {
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }
        }
    } else {
        ClickableText(
            modifier = modifier.fillMaxWidth(),
            text = annotatedText,
            style = style,
            maxLines = maxLines ?: Int.MAX_VALUE
        ) {
            val annotation = annotatedText.getStringAnnotations(it, it)
            if (annotation.isNotEmpty()) {
                when (annotation[0].tag) {
                    EMAIL -> emailLinkHandler(context, mailto = annotation[0].item)
                    PHONE -> phoneLinkHandler(context, phone = annotation[0].item)
                    LINK -> openLink(context, link = annotation[0].item)
                    else -> onUIAction(
                        UIAction(
                            actionKey = data.actionKey,
                            data = annotation[0].item,
                            optionalId = annotation[0].tag
                        )
                    )
                }
            }
        }
    }
}

private fun parseTextToStructuredParts(
    fullText: String,
    clickableMap: Map<String, TextParameter>
): List<Any> {
    val allLines = fullText.split('\n', ignoreCase = false, limit = 0)
    val placeholders = clickableMap.keys
    val structuredParts = mutableListOf<Any>()

    for (line in allLines) {
        val containsLink = placeholders.any { line.contains(it) }

        if (line.isEmpty()) {
            structuredParts.add(line)
            continue
        }

        if (!containsLink) {
            structuredParts.add(line)
        } else {
            var remainingLine = line
            while (remainingLine.isNotEmpty()) {
                var foundPlaceholder: String? = null
                var foundIndex = -1

                for (ph in placeholders) {
                    val index = remainingLine.indexOf(ph)
                    if (index != -1 && (foundIndex == -1 || index < foundIndex)) {
                        foundIndex = index
                        foundPlaceholder = ph
                    }
                }

                if (foundPlaceholder != null) {
                    val textBefore = remainingLine.substring(0, foundIndex)
                    if (textBefore.isNotEmpty()) {
                        structuredParts.add(textBefore)
                    }
                    clickableMap[foundPlaceholder]?.let { structuredParts.add(it) }
                    var nextIndex = foundIndex + foundPlaceholder.length
                    if (nextIndex < remainingLine.length && remainingLine[nextIndex] == '.') {
                        nextIndex++
                    }
                    remainingLine = remainingLine.substring(nextIndex)
                } else {
                    if (remainingLine.isNotEmpty()) {
                        structuredParts.add(remainingLine)
                    }
                    remainingLine = ""
                }
            }
        }
    }
    return structuredParts
}

data class TextWithParametersData(
    val actionKey: String = UIActionKeysCompose.TEXT_WITH_PARAMETERS,
    val text: UiText,
    val parameters: List<TextParameter>? = null
) : UIElementData

fun TextWithParameters?.toComposeTextWithParameters(actionKey: String = UIActionKeysCompose.TEXT_WITH_PARAMETERS): TextWithParametersData? {
    val entity = this
    if (entity?.text == null) return null
    return entity.text?.let { text ->
        TextWithParametersData(
            actionKey = actionKey,
            text = UiText.DynamicString(text),
            parameters = if (entity.parameters != null) {
                mutableListOf<TextParameter>().apply {
                    entity.parameters?.forEach {
                        add(
                            TextParameter(
                                data = TextParameter.Data(
                                    name = it.data?.name?.let { n ->
                                        UiText.DynamicString(
                                            n
                                        )
                                    },
                                    resource = it.data?.resource?.let { r ->
                                        UiText.DynamicString(
                                            r
                                        )
                                    },
                                    alt = it.data?.alt?.let { a ->
                                        UiText.DynamicString(
                                            a
                                        )
                                    },
                                ),
                                type = it.type
                            )
                        )
                    }
                }
            } else {
                emptyList()
            }
        )
    }
}

fun String?.toComposeTextWithParameters(): TextWithParametersData? {
    if (this.isNullOrEmpty()) return null
    return TextWithParametersData(
        text = this.toDynamicString(),
        parameters = emptyList()
    )
}

@Preview(showBackground = true)
@Composable
fun TextWithParametersAtomPreview() {
    val linkText =
        "Міністерство цифрової трансформації та UNITED24 розпочинають проєкт «Армія дронів»! {details}"
    val linkParameter = TextParameter(
        type = TextWithParametersConstants.TYPE_LINK,
        data = TextParameter.Data(
            name = UiText.DynamicString("details"),
            alt = UiText.DynamicString("Детальніше на сайті UNITED24"),
            resource = UiText.DynamicString("https://u24.gov.ua/"),
        )
    )

    val phoneText =
        "Щоб вирішити це питання, будь ласка, зверніться до ДМС України за номером {dmsPhoneNumber}. Дякую!"
    val phoneParameter = TextParameter(
        type = TextWithParametersConstants.TYPE_PHONE,
        data = TextParameter.Data(
            name = UiText.DynamicString("dmsPhoneNumber"),
            alt = UiText.DynamicString("+38 (044) 278-34-02"),
            resource = UiText.DynamicString("+380442783402"),
        )
    )

    val emailText = "Напишіть нам і ми обов'язково вам відповімо {myCustomEmail}!."
    val emailParameter = TextParameter(
        type = TextWithParametersConstants.TYPE_MAIL,
        data = TextParameter.Data(
            name = UiText.DynamicString("myCustomEmail"),
            alt = UiText.DynamicString("ababagalamaga@gmail.com"),
            resource = UiText.DynamicString("ababagalamaga@gmail.com"),
        )
    )

    val textWithList =
        UiText.DynamicString("У межах державної програми єВідновлення можна отримати виплату на відновлення житла для тих, хто постраждав від збройної агресії росії. \n\nКошти надаються, якщо майно: \n• пошкоджене через бойові дії; \n• розміщене на неокупованій території; \n• підлягає відновленню, тобто не повністю зруйноване; \n• не було відремонтоване самостійно. \n\nОтримати виплату можна на ремонт: \n• приватних будинків; \n• квартир; \n• кімнат, наприклад, у гуртожитках. \n\nВажливо: громадяни, які вчиняли злочини проти основ національної безпеки або  перебувають під санкціями, не можуть отримати грошову допомогу. Це також стосується їхніх спадкоємців.")

    val testMetadata = listOf(linkParameter, phoneParameter, emailParameter)

    val textWithParametersData =
        TextWithParametersData(text = textWithList, parameters = testMetadata)

    TextWithParametersAtom(
        modifier = Modifier.padding(16.dp),
        data = textWithParametersData
    ) {
    }
}

@Preview
@Composable
fun TextWithParametersAtomPreview_Link() {
    val linkText =
        UiText.DynamicString("Міністерство цифрової трансформації та UNITED24 розпочинають проєкт «Армія дронів»! {details}")
    val linkParameter = TextParameter(
        type = TextWithParametersConstants.TYPE_LINK,
        data = TextParameter.Data(
            name = UiText.DynamicString("details"),
            alt = UiText.DynamicString("Детальніше на сайті UNITED24"),
            resource = UiText.DynamicString("https://u24.gov.ua/"),
        )
    )

    val textWithParametersData =
        TextWithParametersData(text = linkText, parameters = listOf(linkParameter))

    TextWithParametersAtom(
        modifier = Modifier.padding(16.dp),
        data = textWithParametersData
    ) {
    }
}

@Preview
@Composable
fun TextWithParametersAtomPreview_Phone() {
    val context = LocalContext.current
    val phoneText =
        UiText.DynamicString("Щоб вирішити це питання, будь ласка, зверніться до ДМС України \nза номером {dmsPhoneNumber}")
    val phoneParameter = TextParameter(
        type = TextWithParametersConstants.TYPE_PHONE,
        data = TextParameter.Data(
            name = UiText.DynamicString("dmsPhoneNumber"),
            alt = UiText.DynamicString("+38 (044) 278-34-02"),
            resource = UiText.DynamicString("+380442783402"),
        )
    )

    val textWithParametersData =
        TextWithParametersData(text = phoneText, parameters = listOf(phoneParameter))

    TextWithParametersAtom(
        modifier = Modifier.padding(16.dp),
        data = textWithParametersData
    ) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", it.data, null));
        context.startActivity(intent)
    }
}

@Preview(showBackground = true)
@Composable
fun TextWithParametersAtomPreview_Email() {
    val context = LocalContext.current
    val emailText =
        UiText.DynamicString("Напишіть нам і ми обов'язково вам відповімо {myCustomEmail}!.")
    val emailParameter = TextParameter(
        type = TextWithParametersConstants.TYPE_MAIL,
        data = TextParameter.Data(
            name = UiText.DynamicString("myCustomEmail"),
            alt = UiText.DynamicString("ababagalamaga@gmail.com"),
            resource = UiText.DynamicString("ababagalamaga@gmail.com"),
        )
    )

    val textWithParametersData =
        TextWithParametersData(text = emailText, parameters = listOf(emailParameter))

    TextWithParametersAtom(
        modifier = Modifier.padding(16.dp),
        data = textWithParametersData
    ) {
        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            val to = arrayOf(it.data?.replace("mailto:", ""))
            putExtra(Intent.EXTRA_EMAIL, to)
        }
        context.startActivity(Intent.createChooser(emailIntent, ""))
    }
}

private fun emailLinkHandler(context: Context, mailto: String?) {
    val emailIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        val to = arrayOf(mailto?.replace("mailto:", ""))
        putExtra(Intent.EXTRA_EMAIL, to)
    }
    context.startActivity(Intent.createChooser(emailIntent, ""))
}

private fun phoneLinkHandler(context: Context, phone: String) {
    val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
    context.startActivity(intent)
}

private fun openLink(context: Context, link: String) {
    if (link.startsWith("https:")) {
        val uri = try {
            link.toUri()
        } catch (_: Exception) {
            return
        }

        context.startActivity(Intent(Intent.ACTION_VIEW).apply { data = uri })
    }
}

@Composable
fun UiText.buildWithMetadataComposeEdition(metadata: List<TextParameter>): AnnotatedString {
    val source = this.asString()
    return buildAnnotatedString {
        if (metadata.isEmpty()) {
            append(source)
        } else {
            var result = source
            val annotationsList = mutableListOf<StringAnnotation>()

            val regex = Regex("\\{(.*?)\\}") // Example: "{example}"
            val matches = regex.findAll(result)
            val itemsToReplace = matches.map { it.groupValues[1] }

            for (item in itemsToReplace) {
                metadata.find {
                    it.data?.name?.asString() == item
                }.let { textParameter ->
                    val old = "{${textParameter?.data?.name?.asString()}}"
                    val new = textParameter?.data?.alt?.asString() ?: ""
                    val oldValueOccurrenceCounter = result.windowed(old.length).count { it == old }
                    repeat(oldValueOccurrenceCounter) {
                        val startIndex = result.indexOf(old)
                        val endIndex = startIndex + new.length
                        result = result.replaceFirst(old, new)
                        annotationsList.add(
                            StringAnnotation(
                                startIndex,
                                endIndex,
                                textParameter?.type ?: "no type from params",
                                textParameter?.data?.resource?.asString() ?: "no resource for param"
                            )
                        )
                    }
                }
            }
            append(result)
            for (item in annotationsList) {
                addStringAnnotation(
                    tag = item.tag,
                    annotation = item.annotation,
                    start = item.startIndex,
                    end = item.endIndex
                )
                addStyle(
                    SpanStyle(textDecoration = TextDecoration.Underline),
                    item.startIndex,
                    item.endIndex
                )
            }
        }
    }
}

private data class StringAnnotation(
    val startIndex: Int,
    val endIndex: Int,
    val tag: String,
    val annotation: String
)