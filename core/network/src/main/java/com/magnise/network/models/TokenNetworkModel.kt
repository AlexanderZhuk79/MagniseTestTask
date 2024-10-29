package com.magnise.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TokenNetworkModel(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("expires_in")
    val expiresIn: Int,
    @SerialName("not-before-policy")
    val notBeforePolicy: Int,
    @SerialName("refresh_expires_in")
    val refreshExpiresIn: Int,
    @SerialName("refresh_token")
    val refreshToken: String,
    val scope: String,
    @SerialName("session_state")
    val sessionState: String,
    @SerialName("token_type")
    val tokenType: String
)