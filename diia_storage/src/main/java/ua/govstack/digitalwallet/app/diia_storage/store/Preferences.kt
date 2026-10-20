package ua.govstack.digitalwallet.app.diia_storage.store

import ua.govstack.digitalwallet.app.diia_storage.model.PreferenceKey

object Preferences {

    object Settings {
        const val NAME_DIIA = "DIIA_PREF"
    }

    object Scopes {
        const val USER_SCOPE = "user"
        const val PIN_SCOPE = "pin"
        const val AUTH_SCOPE = "auth"
    }

    const val DEF = "PREF_DEF"

    open class UserDataKey(name: String, dataType: Class<*>) :
        PreferenceKey(name, Scopes.USER_SCOPE, dataType)
    object UseTouchId : UserDataKey("use_touch_id", Boolean::class.java)
    object PublicServicesCategories : UserDataKey("public_services_categories", PublicServicesCategories::class.java)

    open class PinKey(name: String, dataType: Class<*>) :
        PreferenceKey(name, Scopes.PIN_SCOPE, dataType)
    object PinTryCountGlobal : PinKey("pin_try_count_global", Int::class.java)
}
