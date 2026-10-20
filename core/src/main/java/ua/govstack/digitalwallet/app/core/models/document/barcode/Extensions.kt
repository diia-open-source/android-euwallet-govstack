package ua.govstack.digitalwallet.app.core.models.document.barcode

import android.graphics.Bitmap


fun Bitmap.toDocumentBarcodeImage(): DocumentBarcodeImageData {
    return DocumentBarcodeImageData(this)
}
