package ua.govstack.digitalwallet.app.opensource.data.data_source.network.interceptors

import okhttp3.Interceptor
import okhttp3.Response
import ua.govstack.digitalwallet.app.core.di.actions.GlobalActionNetworkState
import ua.govstack.digitalwallet.app.core.network.connectivity.ConnectivityObserver
import ua.govstack.digitalwallet.app.core.network.connectivity.NoConnectivityException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HttpNetworkConnectionInterceptor @Inject constructor(
    @param:GlobalActionNetworkState private val connectivityObserver: ConnectivityObserver
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        return if (!connectivityObserver.isAvailable) {
            throw NoConnectivityException()
        } else {
            chain.proceed(chain.request())
        }
    }
}