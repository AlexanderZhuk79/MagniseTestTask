package com.magnise.network.restapi

import com.magnise.network.models.CountBackNetworkModel
import com.magnise.network.models.InstrumentsNetworkModel
import com.magnise.network.models.TokenNetworkModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.http.parameters
import javax.inject.Inject

class RestService  @Inject constructor(
    private val httpClient: HttpClient
) {
    suspend fun login(): ApiResponse<TokenNetworkModel> =
        apiCall {
            httpClient.submitForm(
                url = "https://platform.fintacharts.com/identity/realms/fintatech/protocol/openid-connect/token",
                formParameters = parameters {
                    append("grant_type", "password")
                    append("client_id", "app-cli")
                    append("username", "r_test@fintatech.com")
                    append("password", "kisfiz-vUnvy9-sopnyv")
                }
            ).body()
        }


    suspend fun getInstruments(): ApiResponse<InstrumentsNetworkModel> =
        apiCall {
            httpClient.get("https://platform.fintacharts.com/api/instruments/v1/instruments") {
                url {
                    parameters.append("provider", "oanda")
                    parameters.append("kind", "forex")
//                    parameters.append("symbol", "EUR/USD")
//                    parameters.append("page", "1")
//                    parameters.append("size", "10")
                }
            }.body()
        }

    suspend fun getCountBack(instrumentID: String): ApiResponse<CountBackNetworkModel> =
        apiCall {
            httpClient.get("https://platform.fintacharts.com/api/bars/v1/bars/count-back") {
                url {
                    parameters.append("provider", "oanda")
                    parameters.append("instrumentID", instrumentID)
                    parameters.append("periodicity", "minute")
                    parameters.append("interval", "1")
                    parameters.append("barsCount", "10")
                }
            }.body()
        }


}