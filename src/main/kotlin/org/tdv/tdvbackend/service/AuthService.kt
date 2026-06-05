package org.tdv.tdvbackend.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.tdv.tdvbackend.domain.entity.InvTdvUsuario
import org.tdv.tdvbackend.security.JwtService
import org.tdv.tdvbackend.security.TdvUserPrincipal
import org.tdv.tdvbackend.web.dto.auth.AuthLoginResponse
import org.tdv.tdvbackend.web.dto.auth.ChangePasswordRequest
import org.tdv.tdvbackend.web.dto.auth.LoginRequest
import org.tdv.tdvbackend.web.dto.auth.UserProfileResponse
import org.tdv.tdvbackend.web.dto.auth.toUserProfileResponse
import org.tdv.tdvbackend.web.error.BusinessRuleException
import org.tdv.tdvbackend.web.error.InvalidCredentialsException
import org.tdv.tdvbackend.web.error.ResourceNotFoundException

@Service
class AuthService(
    private val usuarioService: InvTdvUsuarioService,
    private val passwordService: PasswordService,
    private val jwtService: JwtService,
    private val revokedJwtService: RevokedJwtService,
) {

    @Transactional
    fun logout(accessToken: String) {
        val jti = jwtService.extractJti(accessToken.trim()) ?: return
        revokedJwtService.revoke(jti)
    }

    @Transactional(readOnly = true)
    fun login(request: LoginRequest): AuthLoginResponse {
        val login = request.login.trim()
        val user =
            usuarioService.findActiveEntityByLogin(login)
                ?: throw InvalidCredentialsException()

        if (!passwordService.matches(request.password, user.coPasswordHash)) {
            throw InvalidCredentialsException()
        }

        return buildLoginResponse(user)
    }

    @Transactional(readOnly = true)
    fun profile(principal: TdvUserPrincipal): UserProfileResponse {
        val user =
            usuarioService.findActiveEntityById(principal.idUsuario)
                ?: throw ResourceNotFoundException("Usuario no encontrado")
        return user.toUserProfileResponse()
    }

    @Transactional
    fun changePassword(
        principal: TdvUserPrincipal,
        request: ChangePasswordRequest,
    ) {
        if (request.newPassword != request.confirmNewPassword) {
            throw BusinessRuleException("La nueva contraseña y la confirmación no coinciden")
        }

        usuarioService.changePassword(
            id = principal.idUsuario,
            currentPassword = request.currentPassword,
            newPassword = request.newPassword,
        )
    }

    private fun buildLoginResponse(user: InvTdvUsuario): AuthLoginResponse {
        val profile = user.toUserProfileResponse()
        val token = jwtService.generateToken(profile)
        return AuthLoginResponse(
            accessToken = token,
            user = profile,
        )
    }
}
