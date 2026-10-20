package ua.govstack.digitalwallet.app.opensource.di.data_source.http

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import ua.govstack.digitalwallet.app.opensource.data.data_source.network.interceptors.HttpLoggingInterceptor
import ua.govstack.digitalwallet.app.opensource.data.data_source.network.interceptors.HttpNetworkConnectionInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object KtorHttpClientModule {

    @Provides
    @Singleton
    fun provideKtorHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        networkInterceptor: HttpNetworkConnectionInterceptor,
    ): HttpClient {
        return HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(
                    json = Json {
                        ignoreUnknownKeys = true
                        prettyPrint = true
                        isLenient = true
                    },
                    contentType = ContentType.Application.Json
                )
            }
            engine {
                config {
                    followRedirects(true)
                }
                addInterceptor(loggingInterceptor)
                addInterceptor(networkInterceptor)
            }
        }
    }
}
