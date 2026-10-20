package ua.govstack.digitalwallet.app.pin.utils

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import kotlinx.coroutines.yield

suspend fun assertAwait(block: suspend () -> Unit) {
    while (true) {
        try {
            block()
            break
        } catch (_: AssertionError) {
            yield()
        }
    }
}

suspend fun awaitFor(call: () -> Boolean) {
    while (currentCoroutineContext().isActive) {
        if (call()) {
            return
        } else {
            yield()
        }
    }
}