package org.tdv.tdvbackend.service

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class PasswordService {
    private val encoder = BCryptPasswordEncoder(BCRYPT_COST)

    fun hash(plainPassword: String): String =
        checkNotNull(encoder.encode(plainPassword)) { "bcrypt hash must not be null" }

    fun matches(
        plainPassword: String,
        passwordHash: String,
    ): Boolean = encoder.matches(plainPassword, passwordHash)

    companion object {
        /** Mismo coste que tdvAPP (PasswordHasher COST = 12). */
        private const val BCRYPT_COST = 12
    }
}
