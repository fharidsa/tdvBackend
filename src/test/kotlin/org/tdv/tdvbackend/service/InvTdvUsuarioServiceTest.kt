package org.tdv.tdvbackend.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import org.tdv.tdvbackend.web.dto.usuario.InvTdvUsuarioCreateRequest
import org.tdv.tdvbackend.web.error.BusinessRuleException
import org.tdv.tdvbackend.web.error.DuplicateLoginException

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class InvTdvUsuarioServiceTest {

    @Autowired
    private lateinit var service: InvTdvUsuarioService

    @Test
    fun create_rejectsDuplicateLogin() {
        service.create(
            InvTdvUsuarioCreateRequest(
                noNusuario = "Usuario Uno",
                coLogin = "dupuser",
                password = "secret",
                coRol = "USER",
            ),
        )
        assertThrows(DuplicateLoginException::class.java) {
            service.create(
                InvTdvUsuarioCreateRequest(
                    noNusuario = "Usuario Dos",
                    coLogin = "dupuser",
                    password = "secret2",
                    coRol = "USER",
                ),
            )
        }
    }

    @Test
    fun softDelete_blocksSelfDelete() {
        val created =
            service.create(
                InvTdvUsuarioCreateRequest(
                    noNusuario = "Admin Test",
                    coLogin = "admintest",
                    password = "secret",
                    coRol = "ADMIN",
                ),
            )
        val ex =
            assertThrows(BusinessRuleException::class.java) {
                service.softDelete(created.idUsuario, created.idUsuario)
            }
        assertEquals("No puede eliminar su propio usuario mientras tiene la sesión activa", ex.message)
    }
}
