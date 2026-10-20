package ua.govstack.digitalwallet.app.core.util

import ua.govstack.digitalwallet.app.core.BuildConfig
import ua.govstack.digitalwallet.app.core.util.CommonConst.BUILD_TYPE_DEBUG
import ua.govstack.digitalwallet.app.core.util.CommonConst.BUILD_TYPE_RELEASE

fun isReleaseMode(): Boolean = BuildConfig.BUILD_TYPE == BUILD_TYPE_RELEASE

fun isDebugMode(): Boolean = BuildConfig.BUILD_TYPE == BUILD_TYPE_DEBUG

fun isDevMode(): Boolean = !isReleaseMode()