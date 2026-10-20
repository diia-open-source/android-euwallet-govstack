package ua.govstack.digitalwallet.app.pin.ui.input

interface AlternativeAuthCallback {

    fun onAlternativeAuthSuccessful()

    fun onAlternativeAuthFailed()
}