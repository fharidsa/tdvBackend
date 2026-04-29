package org.tdv.tdvbackend.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.tdv.tdvbackend.domain.entity.InvTdvIca
import org.tdv.tdvbackend.repository.InvTdvIcaRepository
import org.tdv.tdvbackend.web.dto.InvTdvIcaResponse

@Service
class InvTdvIcaService(
    private val repository: InvTdvIcaRepository,
) {

    @Transactional(readOnly = true)
    fun findAll(): List<InvTdvIcaResponse> =
        repository.findAll().map { it.toResponse() }

    @Transactional(readOnly = true)
    fun findByCoCica(coCica: String): InvTdvIcaResponse? =
        repository.findFirstByCoCica(coCica)?.toResponse()
}

private fun InvTdvIca.toResponse(): InvTdvIcaResponse =
    InvTdvIcaResponse(
        idIca = checkNotNull(id) { "inv_tdv_ica.id_ica must be set for persisted rows" },
        coCica = coCica,
        nuNCantidad = nuNCantidad,
    )
