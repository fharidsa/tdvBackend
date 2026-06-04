package org.tdv.tdvbackend.web.dto.auth

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank

data class ChangePasswordRequest(
    @field:NotBlank(message = "Indique la contraseña actual")
    @JsonProperty("currentPassword")
    val currentPassword: String,
    @field:NotBlank(message = "Indique la nueva contraseña")
    @JsonProperty("newPassword")
    val newPassword: String,
    @field:NotBlank(message = "Confirme la nueva contraseña")
    @JsonProperty("confirmNewPassword")
    val confirmNewPassword: String,
)
