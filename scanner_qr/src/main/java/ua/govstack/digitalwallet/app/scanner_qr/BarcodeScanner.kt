package ua.govstack.digitalwallet.app.scanner_qr

import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.flow.MutableSharedFlow
import ua.govstack.digitalwallet.app.scanner_qr.model.BarcodeScannerResult
import ua.govstack.digitalwallet.app.scanner_qr.model.ScannerResult
import ua.govstack.digitalwallet.app.scanner_qr.util.DiiaScanner

class BarcodeScanner(
    val barcodeFormats: List<Int>
) : DiiaScanner {

    override val scanResultFlow = MutableSharedFlow<ScannerResult>()

    private var isRunning = false

    private lateinit var scanner: BarcodeScanner

    override suspend fun processImage(inputImage: InputImage) {
        if (!isRunning) return
        val status = inputToData(inputImage)
        val result = when (status) {
            is BarcodeScannerStatus.Detected -> BarcodeScannerResult.BarcodeDetected(
                status.value,
                status.format
            )

            is BarcodeScannerStatus.Corrupted -> BarcodeScannerResult.BarcodeCorrupted
            is BarcodeScannerStatus.None -> BarcodeScannerResult.BarcodeNotDetected
        }
        scanResultFlow.emit(result)
    }

    override fun init() {
        if (!isRunning) {
            val options = BarcodeScannerOptions.Builder()
                .setFormats(barcodeFormats)
                .build()
            scanner = BarcodeScanning.getClient(options)
            isRunning = true
        }
    }

    override fun dismiss() {
        if (isRunning) {
            isRunning = false
            scanner.close()
        }
    }

    private fun inputToData(inputImage: InputImage): BarcodeScannerStatus {
        if (!this::scanner.isInitialized) return BarcodeScannerStatus.None

        return try {
            val barcodes = Tasks.await(scanner.process(inputImage))

            if (barcodes.isEmpty()) return BarcodeScannerStatus.None

            val barcode = barcodes.first()
            val value = barcode.displayValue
            val format = barcode.format

            when {
                value == null -> BarcodeScannerStatus.Corrupted

                barcodeFormats.isNotEmpty() && !barcodeFormats.contains(format) -> BarcodeScannerStatus.Corrupted

                else -> BarcodeScannerStatus.Detected(value, format)
            }
        } catch (e: Exception) {
            BarcodeScannerStatus.Corrupted
        }
    }

    private fun BarcodeScannerOptions.Builder.setFormats(formats: List<Int>): BarcodeScannerOptions.Builder {
        return when {
            formats.isEmpty() -> {
                setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
            }

            formats.size == 1 -> {
                setBarcodeFormats(formats.first())
            }

            else -> {
                setBarcodeFormats(formats.first(), *formats.drop(1).toIntArray())
            }
        }
    }

    private sealed class BarcodeScannerStatus {
        data class Detected(val value: String, val format: Int) : BarcodeScannerStatus()
        data object Corrupted : BarcodeScannerStatus()
        data object None : BarcodeScannerStatus()
    }

}
