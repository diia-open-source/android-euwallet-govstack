package ua.govstack.digitalwallet.app

/**
 * This is shared between :app and :benchmarks module to provide configurations type safety.
 */
enum class DiiaBuildType(val applicationIdSuffix: String? = null) {
    DEBUG(".debug"),
    RELEASE,
}
