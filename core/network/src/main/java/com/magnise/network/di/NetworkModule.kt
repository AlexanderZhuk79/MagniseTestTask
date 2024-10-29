package com.magnise.network.di

import android.util.Log
import com.magnise.datastore.DataStorePreferenceAPI
import com.magnise.datastore.PreferenceDataStoreConstants.ACCESS_TOKEN_KEY
import com.magnise.datastore.PreferenceDataStoreConstants.REFRESH_TOKEN_KEY
import com.magnise.network.RemoteRepository
import com.magnise.network.RemoteRepositoryImpl
import com.magnise.network.models.TokenNetworkModel
import com.magnise.network.restapi.RestService
import com.magnise.network.websocket.WebsocketClientService
import com.magnise.network.websocket.WebsocketClientServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.request.accept
import io.ktor.client.request.forms.submitForm
import io.ktor.http.ContentType.Application.Json
import io.ktor.http.contentType
import io.ktor.http.parameters
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val PING_INTERVAL = 5_000L

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class RestClient

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class WebSocketClient

    @Singleton
    @Provides
    @WebSocketClient
    fun webSocketClient(): HttpClient =
     HttpClient(OkHttp) {
        engine {
            config {
                pingInterval(PING_INTERVAL, TimeUnit.MILLISECONDS)
            }
        }

        install(Logging){
            level = LogLevel.ALL
            logger = object : Logger {
                override fun log(message: String) {
                    Log.i("webSocketClient", message)
                }
            }
        }
        install(WebSockets)
    }


    @Singleton
    @Provides
    @RestClient
    fun httpClient(dataStorePreferenceAPI: DataStorePreferenceAPI): HttpClient =
        HttpClient(OkHttp).config {
            defaultRequest {
                url("https://platform.fintacharts.com/")
                contentType(Json)
                accept(Json)
            }
            install(Logging) {
                level = LogLevel.ALL
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.i("HttpClient", message)
                    }
                }
            }
            install(ContentNegotiation) {
                json(Json{
                    ignoreUnknownKeys = true
                })
            }
            install(Auth) {
                bearer {
                    loadTokens {
                        BearerTokens(
                            accessToken = dataStorePreferenceAPI.getFirstPreference(ACCESS_TOKEN_KEY,"")?:"",
                            refreshToken = dataStorePreferenceAPI.getFirstPreference(REFRESH_TOKEN_KEY,"")?:""
                        )
                    }
                    refreshTokens {
                        val token = client.submitForm(
                            url = "https://platform.fintacharts.com/identity/realms/fintatech/protocol/openid-connect/token",
                            formParameters = parameters {
                                append("grant_type", "password")
                                append("client_id", "app-cli")
                                append("username", "r_test@fintatech.com")
                                append("password", "kisfiz-vUnvy9-sopnyv")
                            }
                        )
                        {
                            markAsRefreshTokenRequest()
                        }.body<TokenNetworkModel>()
                        dataStorePreferenceAPI.putPreference(ACCESS_TOKEN_KEY,token.accessToken)
                        BearerTokens(
                            accessToken = token.accessToken,
                            refreshToken = token.refreshToken
                        )
                    }
                }
            }
    }

    @Singleton
    @Provides
    fun getRemoteRepository(restService: RestService): RemoteRepository =
        RemoteRepositoryImpl(restService)

    @Singleton
    @Provides
    fun getRestService(@RestClient httpClient: HttpClient) =
        RestService(httpClient)

    @Singleton
    @Provides
    fun getWebsocketClient(@WebSocketClient httpClient: HttpClient, dataStorePreferenceAPI: DataStorePreferenceAPI): WebsocketClientService =
        WebsocketClientServiceImpl(httpClient, dataStorePreferenceAPI)





}