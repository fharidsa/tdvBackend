package org.tdv.tdvbackend.web.dto.auth

import com.fasterxml.jackson.annotation.JsonProperty

data class AuthLoginResponse(
    @JsonProperty("accessToken")
    val accessToken: String,
    @JsonProperty("tokenType")
    val tokenType: String = "Bearer",
    @JsonProperty("expiresInMinutes")
    val expiresInMinutes: Long,
    @JsonProperty("user")
    val user: UserProfileResponse,
)
