package org.tdv.tdvbackend.service

import java.time.LocalDate
import java.time.LocalDateTime
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.tdv.tdvbackend.domain.entity.InvTdvConteoIca
import org.tdv.tdvbackend.repository.InvTdvConteoIcaRepository
import org.tdv.tdvbackend.repository.InvTdvConteoIcaSpecifications
import org.tdv.tdvbackend.web.dto.InvTdvConteoIcaCreateRequest
import org.tdv.tdvbackend.web.dto.InvTdvConteoIcaPageDto
import org.tdv.tdvbackend.web.dto.InvTdvConteoIcaResponse

@Service
class InvTdvConteoIcaService(
    private val repository: InvTdvConteoIcaRepository,
) {

    @Transactional(readOnly = true)
    fun search(
        fechaDesde: LocalDate?,
        fechaHasta: LocalDate?,
        conforme: Boolean?,
        coCicaIca: String?,
        pageable: Pageable,
    ): InvTdvConteoIcaPageDto {
        val spec = InvTdvConteoIcaSpecifications.filter(fechaDesde, fechaHasta, conforme, coCicaIca)
        val page: Page<InvTdvConteoIca> = repository.findAll(spec, pageable)
        return InvTdvConteoIcaPageDto(
            content = page.content.map { it.toResponse() },
            page = page.number,
            size = page.size,
            totalElements = page.totalElements,
            totalPages = page.totalPages,
            first = page.isFirst,
            last = page.isLast,
        )
    }

    @Transactional(readOnly = true)
    fun findById(id: Long): InvTdvConteoIcaResponse? =
        repository.findById(id).map { it.toResponse() }.orElse(null)

    @Transactional
    fun create(request: InvTdvConteoIcaCreateRequest): InvTdvConteoIcaResponse {
        val lecturas = request.nuNlecturas
        val cantidad = request.nuNcantidadIca
        val conforme =
            lecturas != null && cantidad != null && lecturas == cantidad

        val entity =
            InvTdvConteoIca(
                idIca = request.idIca,
                feDfecha = LocalDateTime.now(),
                nuNlecturas = lecturas,
                nuNcantidadIca = cantidad,
                coCicaIca = request.coCicaIca,
                flBconforme = conforme,
            )
        val saved = repository.save(entity)
        return saved.toResponse()
    }
}

private fun InvTdvConteoIca.toResponse(): InvTdvConteoIcaResponse =
    InvTdvConteoIcaResponse(
        idConteoIca =
            checkNotNull(idConteoIca) {
                "inv_tdv_conteo_ica.id_conteo_ica must be set for persisted rows"
            },
        idIca = idIca,
        feDfecha = feDfecha,
        nuNlecturas = nuNlecturas,
        nuNcantidadIca = nuNcantidadIca,
        coCicaIca = coCicaIca,
        flBconforme = flBconforme,
    )
