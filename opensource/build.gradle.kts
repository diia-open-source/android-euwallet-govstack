import com.android.build.api.variant.BuildConfigField
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.diia.android.application)
    alias(libs.plugins.diia.android.application.compose)
    alias(libs.plugins.diia.android.application.flavors)
    alias(libs.plugins.diia.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.navigation.safeargs)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.gms)
}

androidComponents {
    onVariants {
        val platformType = "\"Android\""
        it.buildConfigFields?.put(
            "PLATFORM_TYPE",
            BuildConfigField("String", platformType, "platform type")
        )
    }
}
android {
    namespace = "ua.govstack.digitalwallet.app.opensource"

    signingConfigs {
        getByName("debug") {
            val props = Properties()
            val propsFile = file("keys/debug.properties")
            if (propsFile.canRead()) {
                val fileInputStream = FileInputStream(propsFile)
                props.load(fileInputStream)
                fileInputStream.close()

                storeFile = file(props["storePath"] as String)
                storePassword = props["storePassword"] as String
                keyAlias = props["keyAlias"] as String
                keyPassword = props["keyPassword"] as String
            }
        }
    }

    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("libs/native")
        }
    }

    packaging {
        resources {
            excludes.addAll(
                listOf(
                    "META-INF/ASL-2.0.txt",
                    "META-INF/LGPL-3.0.txt",
                    "META-INF/versions/9/OSGI-INF/MANIFEST.MF"
                )
            )
            pickFirsts.addAll(
                listOf(
                    "draftv4/schema",
                    "draftv3/schema"
                )
            )
        }
    }

    val versionProps = Properties()
    FileInputStream(file("version.properties")).run {
        versionProps.load(this)
        close()
    }

    defaultConfig {
        applicationId = "ua.govstack.digitalwallet.app"
        versionCode = versionProps["VERSION_CODE"].toString().toInt()
        versionName = versionProps["VERSION_NAME"].toString()

        buildConfigField("long", "TOKEN_LEEWAY", "1")
    }

    buildFeatures {
        buildConfig = true
        dataBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            multiDexKeepProguard = file("multidex_keep_file.pro")
        }
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            multiDexKeepProguard = file("multidex_keep_file.pro")
        }
    }

    configurations.configureEach {
        resolutionStrategy {
            force(libs.okhttp)
            force(libs.okhttp.logging)
        }
    }

    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
    }

}

dependencies {
    implementation(projects.libs.core)
    implementation(projects.libs.uiBase)
    implementation(projects.features.home)
    implementation(projects.features.menu)
    implementation(projects.features.login)
    implementation(projects.features.pin)
    implementation(projects.libs.biometric)
    implementation(projects.libs.diiaStorage)
    implementation(projects.features.contextMenu)
    implementation(projects.libs.scannerQr)

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))

    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.appcompat)

    implementation(libs.light.compressor)
    //kotlin
    implementation(libs.androidx.core.ktx)
    //serialization
    implementation(libs.kotlinx.serialization.json)
    //constraint
    implementation(libs.androidx.constraint.layout)
    //lifecycle
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    //work
    implementation(libs.androidx.work.ktx)
    //navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    //hilt
    implementation(libs.hilt.ext.work)
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    ksp(libs.hilt.compiler)
    ksp(libs.hilt.ext.compiler)
    //recycler
    implementation(libs.androidx.recyclerview)
    //viewpager
    implementation(libs.androidx.viewpager2)
    //material
    implementation(libs.material)
    //coroutine
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
    //retrofit
    implementation(libs.retrofit.core)
    //okhttp
    implementation(libs.okhttp)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.retrofit2.kotlin.coroutines.adapter)
    implementation(libs.converter.gson)
    //ktor
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.okhttp)

    // Moshi
    implementation(libs.moshi)
    implementation(libs.moshi.adapters)
    implementation(libs.moshi.kotlin)
    implementation(libs.retrofit.moshi.converter)
    ksp(libs.moshi.codegen)
    //multidex
    implementation(libs.androidx.multidex)
    // Splash
    implementation(libs.androidx.core.splashscreen)

    //ewallet
    implementation(projects.wallet.walletCore)
    implementation(projects.wallet.walletTransaction)
    implementation(projects.wallet.walletIssuing)
    implementation(projects.wallet.walletSharing)
    implementation(projects.wallet.walletGallery)
    implementation(projects.wallet.walletDocPid)
    implementation(projects.wallet.walletDocMdl)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json.jvm)

    implementation(libs.lottie.compose)
}
