package org.tdv.tdvbackend.web.dto.usuario

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime
import org.tdv.tdvbackend.domain.entity.InvTdvUsuario
import org.tdv.tdvbackend.domain.enums.UsuarioRol

data class InvTdvUsuarioResponse(
    @JsonProperty("idUsuario")
    val idUsuario: Int,
    @JsonProperty("noNusuario")
    val noNusuario: String,
    @JsonProperty("coLogin")
    val coLogin: String,
    @JsonProperty("coRol")
    val coRol: UsuarioRol,
    @JsonProperty("feDfechaCreacion")
    val feDfechaCreacion: LocalDateTime,
    @JsonProperty("feDfechaModificacion")
    val feDfechaModificacion: LocalDateTime?,
)

fun InvTdvUsuario.toUsuarioResponse(): InvTdvUsuarioResponse =
    InvTdvUsuarioResponse(
        idUsuario = checkNotNull(idUsuario) { "id_usuario required" },
        noNusuario = noNusuario,
        coLogin = coLogin,
        coRol = coRol,
        feDfechaCreacion = feDfechaCreacion,
        feDfechaModificacion = feDfechaModificacion,
    )
