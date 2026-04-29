package org.tdv.tdvbackend.repository

import jakarta.persistence.criteria.Predicate
import java.time.LocalDate
import org.springframework.data.jpa.domain.Specification
import org.tdv.tdvbackend.domain.entity.InvTdvConteoIca

object InvTdvConteoIcaSpecifications {

    /**
     * @param fechaDesde inclusive (inicio del día en servidor)
     * @param fechaHasta inclusive (fin del día en servidor)
     * @param conforme si no es null, filtra [InvTdvConteoIca.flBconforme] igual a ese valor
     */
    fun filter(
        fechaDesde: LocalDate?,
        fechaHasta: LocalDate?,
        conforme: Boolean?,
    ): Specification<InvTdvConteoIca> =
        Specification { root, _, cb ->
            val predicates = mutableListOf<Predicate>()
            fechaDesde?.let {
                predicates += cb.greaterThanOrEqualTo(root.get("feDfecha"), it.atStartOfDay())
            }
            fechaHasta?.let {
                predicates += cb.lessThan(root.get("feDfecha"), it.plusDays(1).atStartOfDay())
            }
            conforme?.let { predicates += cb.equal(root.get<Boolean>("flBconforme"), it) }
            if (predicates.isEmpty()) {
                cb.conjunction()
            } else {
                cb.and(*predicates.toTypedArray())
            }
        }
}
