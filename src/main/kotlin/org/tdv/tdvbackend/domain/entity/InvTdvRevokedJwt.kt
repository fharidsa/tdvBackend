package org.tdv.tdvbackend.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "inv_tdv_revoked_jwt", schema = "dbo")
class InvTdvRevokedJwt(
    @Id
    @Column(name = "jti", nullable = false, length = 64)
    var jti: String = "",

    @Column(name = "fe_revocado", nullable = false)
    var feRevocado: LocalDateTime = LocalDateTime.now(),
)
