package ua.govstack.digitalwallet.app.scanner_qr.di

import com.google.mlkit.vision.barcode.common.Barcode
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import ua.govstack.digitalwallet.app.scanner_qr.BarcodeScanner
import ua.govstack.digitalwallet.app.scanner_qr.util.DiiaScanner

@Module
@InstallIn(ViewModelComponent::class)
object BarcodeScannerModule {

    @Provides
    @BarcodeScannerEAN13
    fun provideBarcodeScannerEAN13(): DiiaScanner = BarcodeScanner(
        barcodeFormats = listOf(Barcode.FORMAT_EAN_13)
    )

    @Provides
    @BarcodeScannerCODE128
    fun provideBarcodeScannerCODE128(): DiiaScanner = BarcodeScanner(
        barcodeFormats = listOf(Barcode.FORMAT_CODE_128)
    )

    @Provides
    @BarcodeScannerQR
    fun provideBarcodeScannerQR(): DiiaScanner = BarcodeScanner(
        barcodeFormats = listOf(Barcode.FORMAT_QR_CODE)
    )

    @Provides
    @UniversalScanner
    fun provideUniversalScanner(): DiiaScanner = BarcodeScanner(
        barcodeFormats = listOf(
            Barcode.FORMAT_QR_CODE,
            Barcode.FORMAT_DATA_MATRIX,
            Barcode.FORMAT_EAN_13,
            Barcode.FORMAT_CODE_128
        )
    )

}
