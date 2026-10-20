import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import ua.govstack.digitalwallet.app.libs

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("diia.android.library")
                apply("diia.hilt")
                apply("org.jetbrains.kotlin.plugin.serialization")
                apply("androidx.navigation.safeargs.kotlin")
                apply("org.jetbrains.kotlin.plugin.parcelize")
            }
            extensions.configure<LibraryExtension> {
                testOptions.animationsDisabled = true
            }
            dependencies {
                add("implementation", project(":libs:core"))
                add("implementation", project(":libs:ui_base"))

                add("implementation", libs.findLibrary("androidx.lifecycle.runtimeCompose").get())
                add("implementation", libs.findLibrary("androidx.lifecycle.viewModelCompose").get())
                add("implementation", libs.findLibrary("androidx.lifecycle.livedata.ktx").get())
                add("implementation", libs.findLibrary("androidx.navigation.compose").get())
                add("implementation", libs.findLibrary("androidx.fragment.ktx").get())
                add("implementation", libs.findLibrary("androidx.constraint.layout").get())
                add("implementation", libs.findLibrary("androidx.appcompat").get())
            }
        }
    }
}
