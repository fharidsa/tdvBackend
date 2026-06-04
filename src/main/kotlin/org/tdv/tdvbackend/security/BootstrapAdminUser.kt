package org.tdv.tdvbackend.security

import java.time.LocalDateTime
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import org.tdv.tdvbackend.domain.entity.InvTdvUsuario
import org.tdv.tdvbackend.domain.enums.UsuarioRol
import org.tdv.tdvbackend.repository.InvTdvUsuarioRepository
import org.tdv.tdvbackend.service.PasswordService

/**
 * Crea el usuario administrador inicial si no existe (equivalente a BootstrapUsers en tdvAPP).
 * // SECURITY-REVIEW: contraseña desde variable de entorno TDV_BOOTSTRAP_PASSWORD.
 */
@Component
class BootstrapAdminUser(
    private val repository: InvTdvUsuarioRepository,
    private val passwordService: PasswordService,
    @Value("\${tdv.bootstrap.login}") private val bootstrapLogin: String,
    @Value("\${tdv.bootstrap.display-name}") private val bootstrapDisplayName: String,
    @Value("\${tdv.bootstrap.password:}") private val bootstrapPassword: String,
) : ApplicationRunner {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun run(args: ApplicationArguments) {
        val login = bootstrapLogin.trim()
        if (login.isEmpty()) return

        if (repository.findByCoLoginIgnoreCaseAndFlBeliminadoFalse(login) != null) {
            return
        }

        val password = bootstrapPassword.trim()
        if (password.isEmpty()) {
            log.warn(
                "Usuario bootstrap '{}' no creado: configure TDV_BOOTSTRAP_PASSWORD",
                login,
            )
            return
        }

        val entity =
            InvTdvUsuario(
                noNusuario = bootstrapDisplayName.trim().ifEmpty { login },
                coLogin = login,
                coPasswordHash = passwordService.hash(password),
                coRol = UsuarioRol.ADMIN,
                flBeliminado = false,
                feDfechaCreacion = LocalDateTime.now(),
            )
        repository.save(entity)
        log.info("Usuario administrador bootstrap '{}' creado", login)
    }
}
