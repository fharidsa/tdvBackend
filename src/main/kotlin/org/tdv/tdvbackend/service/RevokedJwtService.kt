package org.tdv.tdvbackend.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.tdv.tdvbackend.domain.entity.InvTdvRevokedJwt
import org.tdv.tdvbackend.repository.InvTdvRevokedJwtRepository
import java.time.LocalDateTime

@Service
class RevokedJwtService(
    private val repository: InvTdvRevokedJwtRepository,
) {
    fun isRevoked(jti: String): Boolean = repository.existsById(jti)

    @Transactional
    fun revoke(jti: String) {
        if (jti.isBlank() || repository.existsById(jti)) return
        repository.save(
            InvTdvRevokedJwt(
                jti = jti,
                feRevocado = LocalDateTime.now(),
            ),
        )
    }
}
