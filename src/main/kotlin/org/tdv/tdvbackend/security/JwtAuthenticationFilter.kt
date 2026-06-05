package org.tdv.tdvbackend.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.tdv.tdvbackend.service.RevokedJwtService

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    private val revokedJwtService: RevokedJwtService,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val header = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            val token = header.removePrefix(BEARER_PREFIX).trim()
            if (token.isNotEmpty() && SecurityContextHolder.getContext().authentication == null) {
                val authenticated =
                    runCatching {
                        val jti = jwtService.extractJti(token)
                        if (jti.isNullOrBlank() || revokedJwtService.isRevoked(jti)) {
                            return@runCatching false
                        }
                        val principal = jwtService.parsePrincipal(token)
                        val authentication =
                            UsernamePasswordAuthenticationToken(
                                principal,
                                null,
                                principal.authorities,
                            )
                        authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                        SecurityContextHolder.getContext().authentication = authentication
                        true
                    }.getOrDefault(false)
                if (!authenticated && token.isNotEmpty()) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token revocado o no válido")
                    return
                }
            }
        }
        filterChain.doFilter(request, response)
    }

    companion object {
        private const val BEARER_PREFIX = "Bearer "
    }
}
