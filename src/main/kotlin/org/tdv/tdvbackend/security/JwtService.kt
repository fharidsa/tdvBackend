package org.tdv.tdvbackend.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.Date
import javax.crypto.SecretKey
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.tdv.tdvbackend.domain.enums.UsuarioRol
import org.tdv.tdvbackend.web.dto.auth.UserProfileResponse

@Service
class JwtService(
    @Value("\${tdv.security.jwt.secret}") secret: String,
    @Value("\${tdv.security.jwt.expiration-minutes}") private val expirationMinutes: Long,
) {
    private val signingKey: SecretKey = Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))

    fun generateToken(user: UserProfileResponse): String {
        val now = Instant.now()
        val expiry = now.plusSeconds(expirationMinutes * 60)
        return Jwts
            .builder()
            .subject(user.idUsuario.toString())
            .claim(CLAIM_LOGIN, user.coLogin)
            .claim(CLAIM_NAME, user.noNusuario)
            .claim(CLAIM_ROLE, user.coRol.name)
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiry))
            .signWith(signingKey)
            .compact()
    }

    fun parsePrincipal(token: String): TdvUserPrincipal {
        val claims =
            Jwts
                .parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .payload

        val id = claims.subject.toInt()
        val login = claims[CLAIM_LOGIN, String::class.java]
        val name = claims[CLAIM_NAME, String::class.java]
        val role = UsuarioRol.fromWire(claims[CLAIM_ROLE, String::class.java])
        return TdvUserPrincipal(
            idUsuario = id,
            noNusuario = name,
            coLogin = login,
            coRol = role,
        )
    }

    companion object {
        private const val CLAIM_LOGIN = "login"
        private const val CLAIM_NAME = "name"
        private const val CLAIM_ROLE = "role"
    }
}
