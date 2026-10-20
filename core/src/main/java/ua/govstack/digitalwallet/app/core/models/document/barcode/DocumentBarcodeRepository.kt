package ua.govstack.digitalwallet.app.core.models.document.barcode

import ua.govstack.digitalwallet.app.core.models.document.Document

interface DocumentBarcodeRepository {
    suspend fun loadBarcode(
        doc: Document,
        position: Int,
        fullInfo: Boolean = false
    ): DocumentBarcodeRepositoryResult
}

data class DocumentBarcodeRepositoryResult(
    val result: DocumentBarcodeResult,
    val showToggle: Boolean
)