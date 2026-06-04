package org.tdv.tdvbackend.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import org.tdv.tdvbackend.security.TdvUserPrincipal
import org.tdv.tdvbackend.domain.enums.UsuarioRol
import org.tdv.tdvbackend.web.dto.auth.ChangePasswordRequest
import org.tdv.tdvbackend.web.dto.auth.LoginRequest
import org.tdv.tdvbackend.web.error.BusinessRuleException
import org.tdv.tdvbackend.web.error.InvalidCredentialsException

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuthServiceTest {

    @Autowired
    private lateinit var authService: AuthService

    @Test
    fun login_succeedsForBootstrapUser() {
        val response =
            authService.login(
                LoginRequest(login = "sys", password = "test-bootstrap-password"),
            )
        assertNotNull(response.accessToken)
        assertEquals("sys", response.user.coLogin)
        assertEquals(UsuarioRol.ADMIN, response.user.coRol)
    }

    @Test
    fun login_failsWithWrongPassword() {
        assertThrows(InvalidCredentialsException::class.java) {
            authService.login(LoginRequest(login = "sys", password = "wrong"))
        }
    }

    @Test
    fun changePassword_validatesConfirmation() {
        val principal =
            TdvUserPrincipal(
                idUsuario = 1,
                noNusuario = "sistema",
                coLogin = "sys",
                coRol = UsuarioRol.ADMIN,
            )
        val ex =
            assertThrows(BusinessRuleException::class.java) {
                authService.changePassword(
                    principal,
                    ChangePasswordRequest(
                        currentPassword = "test-bootstrap-password",
                        newPassword = "newpass",
                        confirmNewPassword = "different",
                    ),
                )
            }
        assertEquals("La nueva contraseña y la confirmación no coinciden", ex.message)
    }
}
