package ua.govstack.digitalwallet.app.core.util.system.service

interface SystemServiceProvider {

    fun isNfcServiceAvailable(): Boolean

    fun isNfcServiceEnabled(): Boolean

}