package org.tdv.tdvbackend.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.tdv.tdvbackend.domain.entity.InvTdvIca
import org.tdv.tdvbackend.integration.tytRfid.TytRfidFacadeService
import org.tdv.tdvbackend.repository.InvTdvIcaRepository
import org.tdv.tdvbackend.web.dto.InvTdvIcaResponse

@Service
class InvTdvIcaService(
    private val repository: InvTdvIcaRepository,
    private val tytRfidFacade: TytRfidFacadeService?,
) {

    @Transactional(readOnly = true)
    fun findAll(): List<InvTdvIcaResponse> =
        repository.findAll().map { it.toResponse() }

    @Transactional(readOnly = true)
    fun findByCoCica(coCica: String): InvTdvIcaResponse? {
        if (tytRfidFacade != null) {
            return findByCoCicaViaTyt(coCica)
        }
        return repository.findFirstByCoCica(coCica)?.toResponse()
    }

    private fun findByCoCicaViaTyt(coCica: String): InvTdvIcaResponse? {
        val tytResponse = tytRfidFacade!!.validarCaja(coCica) ?: return null
        return InvTdvIcaResponse(
            idIca = null,
            coCica = coCica,
            nuNCantidad = tytResponse.numPrendas,
            numPacking = tytResponse.numPacking,
        )
    }
}

private fun InvTdvIca.toResponse(): InvTdvIcaResponse =
    InvTdvIcaResponse(
        idIca = checkNotNull(id) { "inv_tdv_ica.id_ica must be set for persisted rows" },
        coCica = coCica,
        nuNCantidad = nuNCantidad,
    )
