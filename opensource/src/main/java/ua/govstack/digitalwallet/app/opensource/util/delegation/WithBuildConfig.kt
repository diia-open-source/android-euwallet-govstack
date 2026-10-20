package ua.govstack.digitalwallet.app.opensource.util.delegation

import android.os.Build
import ua.govstack.digitalwallet.app.core.util.delegation.WithBuildConfig
import ua.govstack.digitalwallet.app.opensource.BuildConfig
import javax.inject.Inject

class WithBuildConfigImpl @Inject constructor() : WithBuildConfig {
    override fun getApplicationId(): String = BuildConfig.APPLICATION_ID

    override fun getSign(): String = ""

    override fun getDGCVerificationBaseUrl(): String = ""

    override fun getVersionCode(): Int = BuildConfig.VERSION_CODE

    override fun getVersionName(): String = BuildConfig.VERSION_NAME

    override fun getSdkVersion(): Int = Build.VERSION.SDK_INT

    override fun getBuildType(): String = BuildConfig.BUILD_TYPE
}
