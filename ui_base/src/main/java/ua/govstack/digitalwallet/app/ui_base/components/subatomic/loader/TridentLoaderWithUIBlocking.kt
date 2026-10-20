package ua.govstack.digitalwallet.app.ui_base.components.subatomic.loader

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import ua.govstack.digitalwallet.app.core.util.state.Loader
import ua.govstack.digitalwallet.app.core.util.state.TridentUiBlocking
import ua.govstack.digitalwallet.app.ui_base.R
import ua.govstack.digitalwallet.app.ui_base.components.noRippleClickable
import ua.govstack.digitalwallet.app.ui_base.components.theme.BlackAlpha60

@Composable
fun TridentLoaderWithUIBlocking(
    modifier: Modifier = Modifier,
    loader: Loader,
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loader_white))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = composition != null
    )

    if (loader.isLoadingFullScreen(TridentUiBlocking)) {
        val context = LocalContext.current

        Box(
            modifier = modifier
                .fillMaxSize()
                .background(BlackAlpha60)
                .noRippleClickable { }
                .semantics {
                    contentDescription = context.getString(
                        R.string.accessibility_data_is_loading_please_wait
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            if (composition != null) {
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.size(80.dp),
                    alignment = Alignment.Center
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.trident_white),
                    contentDescription = null,
                    modifier = modifier
                        .size(44.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun TridentLoaderWithUIBlocking_Preview() {
    TridentLoaderWithUIBlocking(
        loader = Loader.createFullScreen(
            isLoading = true,
            indicator = TridentUiBlocking
        )
    )
}