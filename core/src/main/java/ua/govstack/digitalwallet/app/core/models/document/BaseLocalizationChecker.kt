package ua.govstack.digitalwallet.app.core.models.document

interface BaseLocalizationChecker {
    fun checkLocalizationDocs(doc: DocumentWithMetadata): String?
}