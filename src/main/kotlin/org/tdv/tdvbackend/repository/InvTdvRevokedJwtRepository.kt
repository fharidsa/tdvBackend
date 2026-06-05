package org.tdv.tdvbackend.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.tdv.tdvbackend.domain.entity.InvTdvRevokedJwt

interface InvTdvRevokedJwtRepository : JpaRepository<InvTdvRevokedJwt, String>
