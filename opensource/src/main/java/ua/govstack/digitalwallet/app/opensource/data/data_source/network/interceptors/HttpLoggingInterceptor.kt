package ua.govstack.digitalwallet.app.opensource.data.data_source.network.interceptors

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import ua.govstack.digitalwallet.app.core.util.isDevMode
import ua.govstack.digitalwallet.app.opensource.data.data_source.network.ApiLogger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HttpLoggingInterceptor @Inject constructor(
    logger: ApiLogger
) : Interceptor {

    private var httpLoggingInterceptor: HttpLoggingInterceptor? = null

    init {
        if (isDevMode()) {
            val logging = HttpLoggingInterceptor(logger).apply {
                level = (HttpLoggingInterceptor.Level.BODY)
            }
            httpLoggingInterceptor = logging
        }
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        return httpLoggingInterceptor?.intercept(chain) ?: chain.proceed(chain.request())
    }

}