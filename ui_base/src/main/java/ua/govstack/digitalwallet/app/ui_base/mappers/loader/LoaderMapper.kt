package ua.govstack.digitalwallet.app.ui_base.mappers.loader

import ua.govstack.digitalwallet.app.core.util.state.FullScreen
import ua.govstack.digitalwallet.app.core.util.state.Loader
import ua.govstack.digitalwallet.app.core.util.state.Component
import ua.govstack.digitalwallet.app.core.util.state.None
import ua.govstack.digitalwallet.app.core.util.state.toFullScreenIndicator

/**
 * If no loading in progress there is no need to cast Loader to FullScreen or Component loaders
 */
fun mapToLoader(
    progress: Pair<String, Boolean> = "" to false,
    content: Pair<String, Boolean> = "" to true
): Loader {

    return if (content.first.isNotEmpty() && !content.second) {
        FullScreen(
            indicator = content.first.toFullScreenIndicator(),
            isLoading = true
        )
    } else if (progress.first.isNotEmpty() && progress.second) {
        Component(
            key = progress.first,
            isLoading = true
        )
    } else {
        None
    }
}

