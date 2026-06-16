package org.tdv.tdvbackend.integration.tytRfid.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class TytLoginRequest(
    @JsonProperty("login")
    val login: String,
    @JsonProperty("contrasenia")
    val contrasenia: String,
)
