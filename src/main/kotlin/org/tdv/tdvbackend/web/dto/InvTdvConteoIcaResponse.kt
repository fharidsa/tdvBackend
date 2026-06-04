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
    val idUsuario: Int?,
    val noUsuario: String?,
    /** true/false solo al crear conteo conforme; null en listados o si no aplica impresión. */
    val labelPrinted: Boolean? = null,
)
