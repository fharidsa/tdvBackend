package org.tdv.tdvbackend.web.dto

import java.time.LocalDateTime

data class InvTdvConteoIcaResponse(
    val idConteoIca: Long,
    val idIca: Long?,
    val feDfecha: LocalDateTime?,
    val nuNlecturas: Int?,
    val nuNcantidadIca: Int?,
    val coCicaIca: String?,
    val flBconforme: Boolean?,
    val numPacking: Int?,
    val idUsuario: Int?,
    val noUsuario: String?,
    /** Reservado; la impresión de etiqueta se dispara de forma asíncrona desde la app. */
    val labelPrinted: Boolean? = null,
)
