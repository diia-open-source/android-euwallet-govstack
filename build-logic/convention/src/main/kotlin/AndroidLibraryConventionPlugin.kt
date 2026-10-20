import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin
import ua.govstack.digitalwallet.app.configureFlavors
import ua.govstack.digitalwallet.app.configureKotlinAndroid
import ua.govstack.digitalwallet.app.configurePrintApksTask
import ua.govstack.digitalwallet.app.disableUnnecessaryAndroidTests
import ua.govstack.digitalwallet.app.libs

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
                apply("diia.android.lint")
            }
            extensions.configure<LibraryExtension> {
                val consumerRules = project.file("consumer-rules.pro")

                configureKotlinAndroid(this)
                defaultConfig.run {
                    targetSdk = 35
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    if (consumerRules.isFile && consumerRules.length() > 0L) {
                        consumerProguardFiles(consumerRules)
                    }
                }
                testOptions.animationsDisabled = true
                configureFlavors(this)
                buildTypes {
                    release {
                        isMinifyEnabled = false
                    }
                    debug {
                        isMinifyEnabled = false
                    }
                }
            }
            extensions.configure<LibraryAndroidComponentsExtension> {
                configurePrintApksTask(this)
                disableUnnecessaryAndroidTests(target)
            }
            dependencies {
                add("implementation", fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))

                add("androidTestImplementation", kotlin("test"))
                add("testImplementation", kotlin("test"))

                add("implementation", libs.findLibrary("androidx.tracing.ktx").get())
            }
        }
    }
}
