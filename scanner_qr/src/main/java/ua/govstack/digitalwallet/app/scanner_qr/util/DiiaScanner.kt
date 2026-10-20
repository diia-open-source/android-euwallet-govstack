package ua.govstack.digitalwallet.app.scanner_qr.util

import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.flow.SharedFlow
import ua.govstack.digitalwallet.app.scanner_qr.model.ScannerResult

interface DiiaScanner {

    val scanResultFlow: SharedFlow<ScannerResult>

    suspend fun processImage(inputImage: InputImage)

    fun init()

    fun dismiss()

}
