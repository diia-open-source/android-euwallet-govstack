package ua.govstack.digitalwallet.app.ui_base.provider

import android.content.ContentResolver
import android.content.Context
import androidx.annotation.PluralsRes
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import ua.govstack.digitalwallet.app.ui_base.R
import java.util.Locale
import kotlin.io.bufferedReader
import kotlin.io.readText
import kotlin.io.use

interface ResourceProvider {

    fun provideContext(): Context
    fun provideContentResolver(): ContentResolver
    fun getString(@StringRes resId: Int): String
    fun getStringFromRaw(@RawRes resId: Int): String
    fun getQuantityString(@PluralsRes resId: Int, quantity: Int, vararg formatArgs: Any): String
    fun getString(@StringRes resId: Int, vararg formatArgs: Any): String
    fun genericErrorMessage(): String
    fun genericNetworkErrorMessage(): String
    fun getLocale(): Locale
}

class ResourceProviderImpl(
    private val context: Context
) : ResourceProvider {

    override fun provideContext() = context

    override fun provideContentResolver(): ContentResolver = context.contentResolver

    override fun genericErrorMessage() =
        context.getString(R.string.generic_error_description)

    override fun genericNetworkErrorMessage() =
        context.getString(R.string.generic_network_error_message)


    override fun getString(@StringRes resId: Int): String =
        try {
            context.getString(resId)
        } catch (_: Exception) {
            ""
        }

    override fun getStringFromRaw(@RawRes resId: Int): String =
        try {
            context.resources.openRawResource(resId).bufferedReader().use { it.readText() }
        } catch (_: Exception) {
            ""
        }

    override fun getQuantityString(
        @PluralsRes resId: Int,
        quantity: Int,
        vararg formatArgs: Any
    ): String =
        try {
            context.resources.getQuantityString(resId, quantity, *formatArgs)
        } catch (_: Exception) {
            ""
        }

    override fun getString(resId: Int, vararg formatArgs: Any): String =
        try {
            context.getString(resId, *formatArgs)
        } catch (_: Exception) {
            ""
        }

    override fun getLocale(): Locale = Locale.getDefault()
}