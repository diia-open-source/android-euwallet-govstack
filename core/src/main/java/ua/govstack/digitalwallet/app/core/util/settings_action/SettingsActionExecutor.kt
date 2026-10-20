package ua.govstack.digitalwallet.app.core.util.settings_action


interface SettingsActionExecutor {

    val actionKey: String

    suspend fun executeAction()

}