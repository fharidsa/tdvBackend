package org.tdv.tdvbackend.integration.tytRfid.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class TytTokenResponse(
    @JsonProperty("token")
    val token: String? = null,
    @JsonProperty("accessToken")
    val accessToken: String? = null,
) {
    fun resolveToken(): String =
        token ?: accessToken ?: throw IllegalStateException("No token field found in TyT response")
}
