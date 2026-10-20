package ua.govstack.digitalwallet.app.core.util.extensions

fun <T> emptyDeque(): ArrayDeque<T> = ArrayDeque()

fun <T> ArrayDeque<T>.push(element: T): ArrayDeque<T> {
    addLast(element)
    return this
}

fun <T> ArrayDeque<T>.pop(): T? = this.removeLastOrNull()