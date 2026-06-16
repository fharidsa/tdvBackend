package org.tdv.tdvbackend.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.tdv.tdvbackend.domain.entity.InvTdvUsuario
import org.tdv.tdvbackend.domain.enums.UsuarioRol
import org.tdv.tdvbackend.integration.tytRfid.TytRfidFacadeService
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
    private val tytRfidFacade: TytRfidFacadeService?,
) {
    private val log = LoggerFactory.getLogger(AuthService::class.java)

    @Transactional
    fun logout(accessToken: String) {
        val jti = jwtService.extractJti(accessToken.trim()) ?: return
        revokedJwtService.revoke(jti)
    }

    @Transactional(readOnly = true)
    fun login(request: LoginRequest): AuthLoginResponse {
        if (tytRfidFacade != null) {
            return loginViaTyt(request)
        }
        return loginLocal(request)
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

    private fun loginLocal(request: LoginRequest): AuthLoginResponse {
        val login = request.login.trim()
        val user =
            usuarioService.findActiveEntityByLogin(login)
                ?: throw InvalidCredentialsException()

        if (!passwordService.matches(request.password, user.coPasswordHash)) {
            throw InvalidCredentialsException()
        }

        return buildLoginResponse(user)
    }

    private fun loginViaTyt(request: LoginRequest): AuthLoginResponse {
        val login = request.login.trim()
        val tytResponse = tytRfidFacade!!.validarUsuarioLogin(login, request.password)

        if (!tytResponse.valid) {
            throw InvalidCredentialsException()
        }

        val user = usuarioService.findActiveEntityByLogin(login)
            ?: usuarioService.createFromTyt(
                login = login,
                displayName = tytResponse.nombre ?: login,
                password = request.password,
            )

        log.info("User '{}' authenticated via TyT RFID", login)
        return buildLoginResponse(user)
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
