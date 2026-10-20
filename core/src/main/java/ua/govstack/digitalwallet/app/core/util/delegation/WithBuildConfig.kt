package ua.govstack.digitalwallet.app.core.util.delegation

interface WithBuildConfig {
    fun getApplicationId(): String
    fun getSign(): String
    fun getDGCVerificationBaseUrl(): String
    fun getVersionCode(): Int
    fun getVersionName(): String
    fun getSdkVersion(): Int
    fun getBuildType(): String
}