pluginManagement {
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

rootProject.name = "Diia"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
// apps
include(":apps:opensource")
project(":apps:opensource").projectDir = File(rootDir, "opensource")

// libs
include(":libs:ui_base")
project(":libs:ui_base").projectDir = File(rootDir, "ui_base")
include(":libs:core")
project(":libs:core").projectDir = File(rootDir, "core")
include(":libs:diia_storage")
project(":libs:diia_storage").projectDir = File(rootDir, "diia_storage")
include(":libs:biometric")
project(":libs:biometric").projectDir = File(rootDir, "biometric")
include(":libs:scanner_qr")
project(":libs:scanner_qr").projectDir = File(rootDir, "scanner_qr")

//features
include(":features:menu")
project(":features:menu").projectDir = File(rootDir, "menu")
include(":features:login")
project(":features:login").projectDir = File(rootDir, "login")
include(":features:pin")
project(":features:pin").projectDir = File(rootDir, "pin")
include(":features:context_menu")
project(":features:context_menu").projectDir = File(rootDir, "context_menu")
include(":features:home")
project(":features:home").projectDir = File(rootDir, "home")

// wallet
include(":wallet:wallet_core")
project(":wallet:wallet_core").projectDir = File(rootDir, "wallet_core")
include(":wallet:wallet_doc_pid")
project(":wallet:wallet_doc_pid").projectDir = File(rootDir, "wallet_doc_pid")
include(":wallet:wallet_doc_mdl")
project(":wallet:wallet_doc_mdl").projectDir = File(rootDir, "wallet_doc_mdl")
include(":wallet:wallet_transaction")
project(":wallet:wallet_transaction").projectDir = File(rootDir, "wallet_transaction")
include(":wallet:wallet_issuing")
project(":wallet:wallet_issuing").projectDir = File(rootDir, "wallet_issuing")
include(":wallet:wallet_sharing")
project(":wallet:wallet_sharing").projectDir = File(rootDir, "wallet_sharing")
include(":wallet:wallet_gallery")
project(":wallet:wallet_gallery").projectDir = File(rootDir, "wallet_gallery")

val useLocalEudiFork = providers.gradleProperty("useLocalEudiFork").orNull == "true"

if (useLocalEudiFork) {
    includeBuild("third_party/eudi-lib-android-wallet-core") {
        dependencySubstitution {
            substitute(module("eu.europa.ec.eudi:eudi-lib-android-wallet-core"))
                .using(project(":wallet-core"))
        }
    }
}
