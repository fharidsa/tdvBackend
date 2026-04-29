package org.tdv.tdvbackend.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.tdv.tdvbackend.domain.entity.InvTdvIca

interface InvTdvIcaRepository : JpaRepository<InvTdvIca, Long> {

    fun findFirstByCoCica(coCica: String): InvTdvIca?
}
