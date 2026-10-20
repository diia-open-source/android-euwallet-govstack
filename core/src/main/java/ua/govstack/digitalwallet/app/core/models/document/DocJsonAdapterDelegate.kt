package ua.govstack.digitalwallet.app.core.models.document

open class DocJsonAdapterDelegate<T : Document>(
    val subtype: Class<T>,
    val label: String
)