plugins {
    alias(libs.plugins.diia.android.feature)
    alias(libs.plugins.diia.android.library.compose)
}

android {
    namespace = "ua.govstack.digitalwallet.app.menu"
}

dependencies {

    implementation(projects.libs.diiaStorage)
    implementation(projects.wallet.walletCore)

    implementation(libs.androidx.compose.livedata)
    implementation(libs.androidx.constraint.layout)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.viewpager2)
    implementation(libs.androidx.compose.material3)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.bundles.mockito)
    testImplementation(libs.turbine)
}
