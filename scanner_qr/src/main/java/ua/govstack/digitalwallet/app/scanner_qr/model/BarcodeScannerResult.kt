package ua.govstack.digitalwallet.app.scanner_qr.model


sealed interface BarcodeScannerResult : ScannerResult {
    data class BarcodeDetected(val barcode: String, val type: Int) : BarcodeScannerResult
    data object BarcodeNotDetected : BarcodeScannerResult
    data object BarcodeCorrupted : BarcodeScannerResult
}
