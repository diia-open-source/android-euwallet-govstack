plugins {
    alias(libs.plugins.diia.android.feature)
    alias(libs.plugins.diia.android.library.compose)
}

android {
    namespace = "ua.govstack.digitalwallet.app.scanner_qr"
}

dependencies {
    api(libs.mlkit.barcode)
    api(libs.cameraview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.constraint.layout.compose)
}
