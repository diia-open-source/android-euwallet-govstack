package ua.govstack.digitalwallet.app.core.util.system.service

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import ua.govstack.digitalwallet.app.core.util.extensions.context.serviceNfc
import javax.inject.Inject

class SystemServiceProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SystemServiceProvider {

    override fun isNfcServiceAvailable(): Boolean = context.serviceNfc?.defaultAdapter != null

    override fun isNfcServiceEnabled(): Boolean {
        val nfcAdapter = context.serviceNfc?.defaultAdapter
        return nfcAdapter?.isEnabled == true
    }
}