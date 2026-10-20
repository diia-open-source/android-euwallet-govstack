plugins {
    alias(libs.plugins.diia.android.feature)
    alias(libs.plugins.diia.android.library.compose)
}

android {
    namespace = "ua.govstack.digitalwallet.app.wallet_gallery"
}

dependencies {
    implementation(libs.eudi.wallet.core)
    implementation(projects.wallet.walletCore)
    implementation(projects.wallet.walletSharing)

    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.ui.util)
    implementation(libs.androidx.constraint.layout.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.lottie.compose)

    implementation(libs.androidx.work.ktx)
    implementation(projects.libs.diiaStorage)
    implementation(libs.eudi.openid4vci)
    implementation(projects.wallet.walletDocPid)
    implementation(projects.wallet.walletDocMdl)
}
