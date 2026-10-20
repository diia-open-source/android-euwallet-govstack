package ua.govstack.digitalwallet.app.wallet_core.utils

import java.util.Locale

object LocaleUtils {

    const val DEFAULT_LOCALE = "en-GB"

    /**
     *  Returns locale from string
     *  @param selectedLanguage example of selectedLanguage : en-GB
     *  @return [Locale] object
     */
    fun getLocaleFromSelectedLanguage(selectedLanguage: String): Locale {
        val (langFirstPart, langSecondPart) = selectedLanguage.split("-")
        return Locale(
            langFirstPart,
            langSecondPart
        )
    }
}