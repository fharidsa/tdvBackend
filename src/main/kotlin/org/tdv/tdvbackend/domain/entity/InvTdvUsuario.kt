package org.tdv.tdvbackend.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime
import org.tdv.tdvbackend.domain.enums.UsuarioRol

@Entity
@Table(name = "inv_tdv_usuario", schema = "dbo")
class InvTdvUsuario(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    var idUsuario: Int? = null,

    @Column(name = "no_nusuario", nullable = false, length = 500)
    var noNusuario: String = "",

    @Column(name = "co_login", nullable = false, length = 100)
    var coLogin: String = "",

    @Column(name = "co_password_hash", nullable = false, length = 100)
    var coPasswordHash: String = "",

    @Enumerated(EnumType.STRING)
    @Column(name = "co_rol", nullable = false, length = 10)
    var coRol: UsuarioRol = UsuarioRol.USER,

    @Column(name = "fl_beliminado", nullable = false)
    var flBeliminado: Boolean = false,

    @Column(name = "fe_dfecha_creacion", nullable = false)
    var feDfechaCreacion: LocalDateTime = LocalDateTime.now(),

    @Column(name = "fe_dfecha_modificacion")
    var feDfechaModificacion: LocalDateTime? = null,
)
