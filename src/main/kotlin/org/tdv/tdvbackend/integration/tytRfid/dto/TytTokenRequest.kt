package org.tdv.tdvbackend.integration.tytRfid.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class TytTokenRequest(
    @JsonProperty("client_id")
    val clientId: String,
    @JsonProperty("client_secret")
    val clientSecret: String,
)
