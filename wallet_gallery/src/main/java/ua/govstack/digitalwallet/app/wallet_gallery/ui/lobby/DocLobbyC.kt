package ua.govstack.digitalwallet.app.wallet_gallery.ui.lobby

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import ua.govstack.digitalwallet.app.core.models.common.BackStackEvent
import ua.govstack.digitalwallet.app.core.util.navigation.HomeNavigation
import ua.govstack.digitalwallet.app.ui_base.R
import ua.govstack.digitalwallet.app.wallet_gallery.ui.gallery.GalleryC
import ua.govstack.digitalwallet.app.wallet_gallery.ui.gallery.GalleryVM

@Composable
fun DocLobbyC(
    modifier: Modifier = Modifier,
    navigationBackStackEventFlow: Flow<BackStackEvent>,
    homeNavigationActionFlow: MutableSharedFlow<HomeNavigation>,
    viewModelStoreOwner: ViewModelStoreOwner
) {
    val vm: GalleryVM = hiltViewModel(viewModelStoreOwner)

    Scaffold(
        modifier = modifier.background(Color.Transparent),
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0.dp),
        bottomBar = {},
        content = { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                WalletGalleryBackground()
                GalleryC(
                    vm = vm,
                    navigationBackStackEventFlow = navigationBackStackEventFlow,
                    homeNavigationActionFlow = homeNavigationActionFlow,
                )
            }
        })
}

@Composable
private fun WalletGalleryBackground() {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.gallery_background)
    )
    val dynamicProperty = rememberLottieDynamicProperties(
        rememberLottieDynamicProperty(
            property = LottieProperty.COLOR,
            keyPath = arrayOf("Shape Layer 1", "Ellipse 1", "Fill 1"),
            value = Color(HomeGradientColor1Mode).toArgb()
        ),
        rememberLottieDynamicProperty(
            property = LottieProperty.COLOR,
            keyPath = arrayOf("Shape Layer 2", "Ellipse 1", "Fill 1"),
            value = Color(HomeGradientColor2Mode).toArgb()
        ),
        rememberLottieDynamicProperty(
            property = LottieProperty.COLOR,
            keyPath = arrayOf("Shape Layer 3", "Ellipse 1", "Fill 1"),
            value = Color(HomeGradientColor3Mode).toArgb()
        ),
        rememberLottieDynamicProperty(
            property = LottieProperty.COLOR,
            keyPath = arrayOf("Shape Layer 4", "Ellipse 1", "Fill 1"),
            value = Color(HomeGradientColor4Mode).toArgb()
        ),
        rememberLottieDynamicProperty(
            property = LottieProperty.COLOR,
            keyPath = arrayOf("Shape Layer 5", "Ellipse 1", "Fill 1"),
            value = Color(HomeGradientColor5Mode).toArgb()
        ),
        rememberLottieDynamicProperty(
            property = LottieProperty.COLOR,
            keyPath = arrayOf("Shape Layer 6", "Ellipse 1", "Fill 1"),
            value = Color(HomeGradientColor6Mode).toArgb()
        ),
        rememberLottieDynamicProperty(
            property = LottieProperty.COLOR,
            keyPath = arrayOf("Shape Layer 7", "Ellipse 1", "Fill 1"),
            value = Color(HomeGradientColor7Mode).toArgb()
        ),
        rememberLottieDynamicProperty(
            property = LottieProperty.COLOR,
            keyPath = arrayOf("Medium Royal Blue Solid 1"),
            value = Color(HomeGradientBackgroundColor).toArgb()
        )
    )

    AnimatedVisibility(
        visible = true,
        enter = fadeIn(animationSpec = tween(250)),
        exit = fadeOut(animationSpec = tween(250))
    ) {
        LottieAnimation(
            modifier = Modifier.fillMaxSize(),
            alignment = Alignment.Center,
            contentScale = ContentScale.FillBounds,
            composition = composition,
            iterations = LottieConstants.IterateForever,
            dynamicProperties = dynamicProperty,
        )
    }
}

private const val HomeGradientColor1Mode = 0x93D1FB
private const val HomeGradientColor2Mode = 0x5199CE
private const val HomeGradientColor3Mode = 0x66FFFB8F
private const val HomeGradientColor4Mode = 0x6EBDD1
private const val HomeGradientColor5Mode = 0x8ED5C2
private const val HomeGradientColor6Mode = 0xABE0E9
private const val HomeGradientColor7Mode = 0x4F96C6
private const val HomeGradientBackgroundColor = 0xE2ECF4
