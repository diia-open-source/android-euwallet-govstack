package ua.govstack.digitalwallet.app.core.util.extensions.lifecycle

import ua.govstack.digitalwallet.app.core.util.event.UiDataEvent
import ua.govstack.digitalwallet.app.core.util.event.UiEvent

inline fun <reified T> UiDataEvent<T>.consumeEvent(todo: (T) -> Unit){
    getContentIfNotHandled()?.let { data ->
        handle()
        todo(data)
    }
}

inline fun UiEvent.consumeEvent(todo: () -> Unit){
    if(notHandedYet){
        handle()
        todo.invoke()
    }
}