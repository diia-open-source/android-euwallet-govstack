package ua.govstack.digitalwallet.app.scanner_qr.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BarcodeScannerEAN13

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BarcodeScannerCODE128

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BarcodeScannerQR

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UniversalScanner
