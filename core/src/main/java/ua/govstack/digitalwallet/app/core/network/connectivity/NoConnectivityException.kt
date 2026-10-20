package ua.govstack.digitalwallet.app.core.network.connectivity

import java.io.IOException

class NoConnectivityException : IOException() {

    override val message: String
        get() = "No Internet Connection"

}