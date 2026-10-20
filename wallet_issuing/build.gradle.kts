plugins {
    alias(libs.plugins.diia.android.feature)
    alias(libs.plugins.diia.android.library.compose)
}

android {
    namespace = "ua.govstack.digitalwallet.app.wallet_issuing"
}

dependencies {
    implementation(libs.eudi.wallet.core)
    implementation(projects.wallet.walletCore)
    implementation(projects.features.pin)
    implementation(libs.androidx.work.ktx)
    implementation(libs.gson)
    implementation(libs.bundles.moshi)
    implementation(projects.libs.diiaStorage)

    //hilt
    implementation(libs.hilt.ext.work)
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    ksp(libs.hilt.compiler)
    ksp(libs.hilt.ext.compiler)
}
