package org.tdv.tdvbackend.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "inv_tdv_conteo_ica", schema = "dbo")
class InvTdvConteoIca(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_conteo_ica")
    var idConteoIca: Long? = null,

    @Column(name = "id_ica")
    var idIca: Long? = null,

    @Column(name = "fe_dfecha")
    var feDfecha: LocalDateTime? = null,

    @Column(name = "nu_nlecturas")
    var nuNlecturas: Int? = null,

    @Column(name = "nu_ncantidad_ica")
    var nuNcantidadIca: Int? = null,

    @Column(name = "co_cica_ica", length = 1000)
    var coCicaIca: String? = null,

    @Column(name = "fl_bconforme")
    var flBconforme: Boolean? = null,

    @Column(name = "id_usuario")
    var idUsuario: Int? = null,

    @Column(name = "no_usuario", length = 500)
    var noUsuario: String? = null,
)
