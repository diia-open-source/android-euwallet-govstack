package ua.govstack.digitalwallet.app.ui_base.components.molecule.card

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import ua.govstack.digitalwallet.app.core.models.common_compose.mlc.card.CardImageMlc
import ua.govstack.digitalwallet.app.ui_base.components.DiiaResourceIcon
import ua.govstack.digitalwallet.app.ui_base.components.DiiaResourceRaw
import ua.govstack.digitalwallet.app.ui_base.components.atom.icon.SmallIconAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.space.SpacerAtm
import ua.govstack.digitalwallet.app.ui_base.components.atom.space.SpacerAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.space.SpacerAtmType
import ua.govstack.digitalwallet.app.ui_base.components.conditional
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.DataActionWrapper
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIAction
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIActionKeysCompose
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.SidePaddingMode
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.TopPaddingMode
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.UiText
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.toDynamicString
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.toDp
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.toSidePaddingMode
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.toTopPaddingMode
import ua.govstack.digitalwallet.app.ui_base.components.molecule.message.AttentionIconMessageMlc
import ua.govstack.digitalwallet.app.ui_base.components.molecule.message.AttentionIconMessageMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.message.BackgroundMode
import ua.govstack.digitalwallet.app.ui_base.components.molecule.message.toUiModel
import ua.govstack.digitalwallet.app.ui_base.components.noRippleClickable
import ua.govstack.digitalwallet.app.ui_base.components.organism.carousel.SimpleCarouselCard
import ua.govstack.digitalwallet.app.ui_base.components.organism.list.pagination.SimplePagination
import ua.govstack.digitalwallet.app.ui_base.components.subatomic.loader.LoaderSpinnerLoaderAtm
import ua.govstack.digitalwallet.app.ui_base.components.theme.BlackAlpha30
import ua.govstack.digitalwallet.app.ui_base.components.theme.BlackAlpha50
import ua.govstack.digitalwallet.app.ui_base.components.theme.DiiaTextStyle
import ua.govstack.digitalwallet.app.ui_base.util.toDataActionWrapper

@Composable
fun CardImageMlc(
    modifier: Modifier = Modifier,
    data: CardImageMlcData,
    clickable: Boolean = true,
    inCarousel: Boolean = false,
    onUIAction: (UIAction) -> Unit
) {
    Column(
        modifier = modifier
            .conditional(!inCarousel) {
                padding(
                    start = data.paddingHorizontal.toDp(defaultPadding = 16.dp),
                    top = data.paddingTop.toDp(defaultPadding = 8.dp),
                    end = data.paddingHorizontal.toDp(defaultPadding = 16.dp)
                )
            }
            .background(color = Color.White, shape = RoundedCornerShape(16.dp))
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(16.dp)
            .noRippleClickable {
                onUIAction(
                    UIAction(
                        actionKey = data.actionKey,
                        data = data.id,
                        action = data.action
                    )
                )
            }
            .semantics(mergeDescendants = true) {
                onClick(label = null, action = null)
            }
            .testTag(data.componentId?.asString() ?: "")
    ) {
        data.label?.let {
            Text(
                text = it.asString(),
                style = DiiaTextStyle.h4ExtraSmallHeading,
                textAlign = TextAlign.Center,
                color = Color.Black,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusable()
            )
        }
        data.description?.let {
            Text(
                text = data.description.asString(),
                style = DiiaTextStyle.t2TextDescription,
                textAlign = TextAlign.Center,
                color = BlackAlpha50,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .focusable()
            )
        }
        if (data.label != null || data.description != null) {
            SpacerAtm(data = SpacerAtmData(SpacerAtmType.MEDIUM))
        }
        data.gif?.let {
            CardGifPlaceholder(
                code = it,
                modifier = Modifier.focusable(),
                accessibilityDescription = data.accessibilityDescription
            )
        }
        data.image?.let {
            SubcomposeAsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 3f)
                    .clip(shape = RoundedCornerShape(8.dp))
                    .focusable(),
                model = data.image,
                contentDescription = null,
                loading = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(4f / 3f)
                            .clip(shape = RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        LoaderSpinnerLoaderAtm()
                    }
                },
                success = {
                    Image(
                        modifier = Modifier
                            .semantics(mergeDescendants = true) {
                                role = Role.Image
                            }
                            .focusable(),
                        painter = painter,
                        contentDescription = data.accessibilityDescription.orEmpty(),
                        contentScale = ContentScale.Crop
                    )
                },
                error = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(4f / 3f)
                            .clip(shape = RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            modifier = Modifier
                                .size(56.dp),
                            painter = painterResource(
                                DiiaResourceIcon.getResourceId(
                                    DiiaResourceIcon.PLACEHOLDER.code
                                )
                            ),
                            colorFilter = ColorFilter.tint(BlackAlpha30),
                            contentDescription = null
                        )
                    }
                }
            )
        }
        data.attentionIconMessageMlc?.let {
            AttentionIconMessageMlc(
                modifier = Modifier.focusable(),
                data = data.attentionIconMessageMlc.copy(
                    paddingHorizontal = SidePaddingMode.NONE
                ), onUIAction = onUIAction
            )
        }
    }
}

@Composable
private fun CardGifPlaceholder(
    code: String,
    modifier: Modifier = Modifier,
    accessibilityDescription: String? = null
) {
    val resId = DiiaResourceRaw.getResourceId(code)
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(
            resId
        )
    )
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(4f / 3f)
            .clip(shape = RoundedCornerShape(8.dp))
            .semantics {
                contentDescription = accessibilityDescription.orEmpty()
            },
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            modifier = Modifier.fillMaxSize(),
            alignment = Alignment.Center,
            contentScale = ContentScale.Fit,
            composition = composition,
            progress = { progress },
        )
    }
}

data class CardImageMlcData(
    val actionKey: String = UIActionKeysCompose.CARD_IMAGE_MLC,
    override val id: String,
    val label: UiText? = null,
    val description: UiText? = null,
    val image: String? = null,
    val gif: String? = null,
    val action: DataActionWrapper? = null,
    val accessibilityDescription: String? = null,
    val componentId: UiText? = null,
    val paddingTop: TopPaddingMode? = null,
    val paddingHorizontal: SidePaddingMode? = null,
    val attentionIconMessageMlc: AttentionIconMessageMlcData? = null
) : SimpleCarouselCard, SimplePagination

fun CardImageMlc.toUiModel(
    id: String? = null,
    contentDescription: UiText? = null
): CardImageMlcData {
    return CardImageMlcData(
        id = id ?: componentId ?: UIActionKeysCompose.CARD_IMAGE_MLC,
        componentId = componentId?.let { UiText.DynamicString(it) },
        label = label.toDynamicString(),
        description = description.toDynamicString(),
        image = image,
        gif = gif,
        action = action?.toDataActionWrapper(),
        attentionIconMessageMlc = attentionIconMessageMlc?.toUiModel(),
        paddingTop = this.paddingMode?.top.toTopPaddingMode(),
        paddingHorizontal = this.paddingMode?.side.toSidePaddingMode(),
    )
}

enum class CardImageMlcMockType {
    image, gif
}

fun generateCardImageMlcMockData(type: CardImageMlcMockType): CardImageMlcData {
    return when (type) {
        CardImageMlcMockType.image -> {
            CardImageMlcData(
                id = "",
                label = UiText.DynamicString("label"),
                description = UiText.DynamicString("description"),
                image = "https://design.google/library/evolving-google-identity",
                attentionIconMessageMlc = AttentionIconMessageMlcData(
                    icon = SmallIconAtmData(code = DiiaResourceIcon.ELLIPSE_INFO.code),
                    text = UiText.DynamicString("Щоб надіслати заяву, потрібно вказати всі дані."),
                    backgroundMode = BackgroundMode.NOTE
                )
            )
        }

        CardImageMlcMockType.gif -> {
            CardImageMlcData(
                id = "",
                label = UiText.DynamicString("label"),
                description = UiText.DynamicString("description"),
                gif = DiiaResourceRaw.DEFAULT_GLOBAL.code,
                attentionIconMessageMlc = AttentionIconMessageMlcData(
                    icon = SmallIconAtmData(code = DiiaResourceIcon.ELLIPSE_INFO.code),
                    text = UiText.DynamicString("Щоб надіслати заяву, потрібно вказати всі дані."),
                    backgroundMode = BackgroundMode.NOTE
                )
            )
        }
    }
}

@Composable
@Preview
fun CardImageMlcPreview_Image() {
    val data = generateCardImageMlcMockData(CardImageMlcMockType.image)
    CardImageMlc(
        data = data,
        onUIAction = {
            /* no-op */
        }
    )
}

@Composable
@Preview
fun CardImageMlcPreview_Gif() {
    val data = generateCardImageMlcMockData(CardImageMlcMockType.gif)
    CardImageMlc(
        data = data,
        onUIAction = {
            /* no-op */
        }
    )
}