package ua.govstack.digitalwallet.app.wallet_issuing.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import ua.govstack.digitalwallet.app.core.util.state.Loader
import ua.govstack.digitalwallet.app.core.util.state.start
import ua.govstack.digitalwallet.app.core.util.state.stop

fun <T> Flow<T>.withLoader(
    loader: MutableStateFlow<Loader>,
): Flow<T> {
    return this
        .onStart { loader.start() }
        .onCompletion {
            loader.stop()
        }
}