package org.tdv.tdvbackend.web.dto.auth

import com.fasterxml.jackson.annotation.JsonProperty
import org.tdv.tdvbackend.domain.entity.InvTdvUsuario
import org.tdv.tdvbackend.domain.enums.UsuarioRol

data class UserProfileResponse(
    @JsonProperty("idUsuario")
    val idUsuario: Int,
    @JsonProperty("noNusuario")
    val noNusuario: String,
    @JsonProperty("coLogin")
    val coLogin: String,
    @JsonProperty("coRol")
    val coRol: UsuarioRol,
)

fun InvTdvUsuario.toUserProfileResponse(): UserProfileResponse =
    UserProfileResponse(
        idUsuario = checkNotNull(idUsuario) { "id_usuario required" },
        noNusuario = noNusuario,
        coLogin = coLogin,
        coRol = coRol,
    )
