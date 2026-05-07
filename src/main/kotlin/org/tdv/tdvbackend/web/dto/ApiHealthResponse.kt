package org.tdv.tdvbackend.web.dto

/**
 * Salud de la API y de la base de datos.
 * [status] refleja el servicio en conjunto; con BD caída es `DEGRADED`.
 */
data class ApiHealthResponse(
    val status: String,
    val database: String,
)
