package org.tdv.tdvbackend.web.dto.auth

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    @field:NotBlank(message = "Indique el login")
    @JsonProperty("login")
    val login: String,
    @field:NotBlank(message = "Indique la contraseña")
    @JsonProperty("password")
    val password: String,
)
