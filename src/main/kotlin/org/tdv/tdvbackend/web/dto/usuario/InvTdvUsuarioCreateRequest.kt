package org.tdv.tdvbackend.web.dto.usuario

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.tdv.tdvbackend.domain.enums.UsuarioRol

data class InvTdvUsuarioCreateRequest(
    @field:NotBlank(message = "Indique el nombre de usuario")
    @field:Size(max = 500)
    @JsonProperty("noNusuario")
    @JsonAlias("no_nusuario")
    val noNusuario: String,
    @field:NotBlank(message = "Indique el login")
    @field:Size(max = 100)
    @JsonProperty("coLogin")
    @JsonAlias("co_login")
    val coLogin: String,
    @field:NotBlank(message = "Indique la contraseña para el usuario nuevo")
    @JsonProperty("password")
    val password: String,
    @field:NotBlank(message = "Seleccione el rol del usuario (ADMIN o USER)")
    @field:Pattern(regexp = "ADMIN|USER", message = "Rol inválido; use ADMIN o USER")
    @JsonProperty("coRol")
    @JsonAlias("co_rol")
    val coRol: String,
) {
    fun rolEnum(): UsuarioRol = UsuarioRol.fromWire(coRol)
}
