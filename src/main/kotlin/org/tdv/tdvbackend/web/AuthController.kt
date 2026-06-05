package org.tdv.tdvbackend.web

import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.tdv.tdvbackend.security.TdvUserPrincipal
import org.tdv.tdvbackend.service.AuthService
import org.tdv.tdvbackend.web.dto.auth.AuthLoginResponse
import org.tdv.tdvbackend.web.dto.auth.ChangePasswordRequest
import org.tdv.tdvbackend.web.dto.auth.LoginRequest
import org.tdv.tdvbackend.web.dto.auth.UserProfileResponse

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService,
) {

    @PostMapping("/login")
    fun login(
        @Valid @RequestBody body: LoginRequest,
    ): ResponseEntity<AuthLoginResponse> =
        ResponseEntity.ok(authService.login(body))

    @GetMapping("/me")
    fun me(
        @AuthenticationPrincipal principal: TdvUserPrincipal,
    ): ResponseEntity<UserProfileResponse> =
        ResponseEntity.ok(authService.profile(principal))

    @PutMapping("/me/password")
    fun changePassword(
        @AuthenticationPrincipal principal: TdvUserPrincipal,
        @Valid @RequestBody body: ChangePasswordRequest,
    ): ResponseEntity<Void> {
        authService.changePassword(principal, body)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/logout")
    fun logout(request: HttpServletRequest): ResponseEntity<Void> {
        val token = extractBearerToken(request)
        if (!token.isNullOrBlank()) {
            authService.logout(token)
        }
        return ResponseEntity.noContent().build()
    }

    private fun extractBearerToken(request: HttpServletRequest): String? {
        val header = request.getHeader(HttpHeaders.AUTHORIZATION) ?: return null
        if (!header.startsWith("Bearer ", ignoreCase = true)) return null
        return header.removePrefix("Bearer ").trim().takeIf { it.isNotEmpty() }
    }
}
