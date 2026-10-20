package ua.govstack.digitalwallet.app.core.util.extensions

import ua.govstack.digitalwallet.app.core.network.connectivity.NoConnectivityException

fun Exception.noInternetException() =  this is NoConnectivityException