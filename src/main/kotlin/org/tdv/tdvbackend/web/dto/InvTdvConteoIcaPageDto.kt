package org.tdv.tdvbackend.web.dto

/** Respuesta paginada del listado de conteos ICA. */
data class InvTdvConteoIcaPageDto(
    val content: List<InvTdvConteoIcaResponse>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val first: Boolean,
    val last: Boolean,
)
