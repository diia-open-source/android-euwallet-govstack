plugins {
    alias(libs.plugins.diia.android.feature)
    alias(libs.plugins.diia.android.library.compose)
}

android {
    namespace = "ua.govstack.digitalwallet.app.wallet_sharing"
}

dependencies {
    implementation(libs.eudi.wallet.core)
    implementation(projects.wallet.walletCore)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.constraint.layout.compose)
    implementation(projects.features.pin)
    //Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    implementation(libs.gson)
    implementation(libs.eudi.wallet.core)
    implementation(libs.bundles.moshi)

}
