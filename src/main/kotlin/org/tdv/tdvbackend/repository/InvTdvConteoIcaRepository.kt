package org.tdv.tdvbackend.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.tdv.tdvbackend.domain.entity.InvTdvConteoIca

interface InvTdvConteoIcaRepository :
    JpaRepository<InvTdvConteoIca, Long>,
    JpaSpecificationExecutor<InvTdvConteoIca>
