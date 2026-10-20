plugins {
    alias(libs.plugins.diia.android.feature)
    alias(libs.plugins.diia.android.library.compose)
}

// Wallet provider base URL used by attestation-related flows.
// This is environment-specific and is intentionally injected at build time.
val walletProviderHost =
    providers.gradleProperty("WALLET_PROVIDER_HOST")
        .orElse("https://wallet-provider.eudiw.dev")

// Path to the packaged issuer catalog inside the app assets folder.
// Integrators can replace this per environment without editing Kotlin code.
val walletIssuersConfigAsset =
    providers.gradleProperty("WALLET_ISSUERS_CONFIG_ASSET")
        .orElse("configs/issuers_config.json")

// Security-sensitive trust toggles.
val allowUntrustedIssuers =
    providers.gradleProperty("ALLOW_UNTRUSTED_ISSUERS")
        .orElse("false")
val allowUntrustedVerifiers =
    providers.gradleProperty("ALLOW_UNTRUSTED_VERIFIERS")
        .orElse("true")
val enforceIssuerAllowList =
    providers.gradleProperty("ENFORCE_ISSUER_ALLOW_LIST")
        .orElse("false")

// Enables verbose wallet SDK logging for troubleshooting and integration work.
// Release environments should normally keep this disabled.
val walletDebugLogging =
    providers.gradleProperty("WALLET_DEBUG_LOGGING")
        .orElse("true")

androidComponents {
    onVariants { variant ->
        // Export build-time values to BuildConfig so WalletCoreConfigImpl can assemble
        // the runtime wallet configuration without hardcoded environment constants.
        variant.buildConfigFields?.put(
            "WALLET_PROVIDER_HOST",
            com.android.build.api.variant.BuildConfigField(
                "String",
                "\"${walletProviderHost.get()}\"",
                "Wallet provider host"
            )
        )
        variant.buildConfigFields?.put(
            "WALLET_ISSUERS_CONFIG_ASSET",
            com.android.build.api.variant.BuildConfigField(
                "String",
                "\"${walletIssuersConfigAsset.get()}\"",
                "Wallet issuers config asset path"
            )
        )
        variant.buildConfigFields?.put(
            "ALLOW_UNTRUSTED_ISSUERS",
            com.android.build.api.variant.BuildConfigField(
                "boolean",
                allowUntrustedIssuers.get(),
                "Allow untrusted issuers for wallet flows"
            )
        )
        variant.buildConfigFields?.put(
            "ALLOW_UNTRUSTED_VERIFIERS",
            com.android.build.api.variant.BuildConfigField(
                "boolean",
                allowUntrustedVerifiers.get(),
                "Allow untrusted verifiers for wallet flows"
            )
        )
        variant.buildConfigFields?.put(
            "ENFORCE_ISSUER_ALLOW_LIST",
            com.android.build.api.variant.BuildConfigField(
                "boolean",
                enforceIssuerAllowList.get(),
                "Require issuer allow-list enforcement"
            )
        )
        variant.buildConfigFields?.put(
            "WALLET_DEBUG_LOGGING",
            com.android.build.api.variant.BuildConfigField(
                "boolean",
                walletDebugLogging.get(),
                "Enable verbose wallet logging"
            )
        )
    }
}

android {
    namespace = "ua.govstack.digitalwallet.app.wallet_core"

    buildFeatures {
        // Wallet configuration values are exposed through BuildConfig fields.
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.livedata)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.ui.util)
    implementation(libs.androidx.constraint.layout.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.kotlinx.serialization.json)
    implementation(projects.libs.diiaStorage)
    implementation(libs.mlkit.vision)
    implementation(libs.zxing)
    implementation(libs.lottie.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(projects.features.pin)

    implementation(libs.eudi.wallet.core)
    implementation(libs.bouncycastle)
    implementation(libs.upokecenter.cbor)
    implementation(libs.eudi.openid4vci)
    implementation(libs.bundles.moshi)
    ksp(libs.moshi.codegen)

    //documents
    implementation(projects.wallet.walletDocPid)
    implementation(projects.wallet.walletDocMdl)

    implementation(libs.gson)
    implementation(libs.androidx.biometric)
    implementation(libs.androidx.work.ktx)

    //Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    //hilt
    implementation(libs.hilt.ext.work)
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    ksp(libs.hilt.compiler)
    ksp(libs.hilt.ext.compiler)

    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.bundles.mockito)
    testImplementation(libs.turbine)
    testImplementation(libs.mockk.android)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso.core)

}
