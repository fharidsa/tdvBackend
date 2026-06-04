package org.tdv.tdvbackend.service

import java.time.LocalDateTime
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.tdv.tdvbackend.domain.entity.InvTdvUsuario
import org.tdv.tdvbackend.domain.enums.UsuarioRol
import org.tdv.tdvbackend.repository.InvTdvUsuarioRepository
import org.tdv.tdvbackend.web.dto.usuario.InvTdvUsuarioCreateRequest
import org.tdv.tdvbackend.web.dto.usuario.InvTdvUsuarioResponse
import org.tdv.tdvbackend.web.dto.usuario.InvTdvUsuarioUpdateRequest
import org.tdv.tdvbackend.web.dto.usuario.toUsuarioResponse
import org.tdv.tdvbackend.web.error.BusinessRuleException
import org.tdv.tdvbackend.web.error.DuplicateLoginException
import org.tdv.tdvbackend.web.error.ResourceNotFoundException

@Service
class InvTdvUsuarioService(
    private val repository: InvTdvUsuarioRepository,
    private val passwordService: PasswordService,
) {

    @Transactional(readOnly = true)
    fun listActive(): List<InvTdvUsuarioResponse> =
        repository.findAllByFlBeliminadoFalseOrderByNoNusuarioAsc().map { it.toUsuarioResponse() }

    @Transactional(readOnly = true)
    fun findActiveById(id: Int): InvTdvUsuarioResponse =
        repository.findByIdUsuarioAndFlBeliminadoFalse(id)?.toUsuarioResponse()
            ?: throw ResourceNotFoundException("Usuario no encontrado")

    @Transactional
    fun create(request: InvTdvUsuarioCreateRequest): InvTdvUsuarioResponse {
        val nombre = request.noNusuario.trim()
        val login = request.coLogin.trim()
        if (repository.existsByCoLoginIgnoreCaseAndFlBeliminadoFalse(login)) {
            throw DuplicateLoginException()
        }
        val entity =
            InvTdvUsuario(
                noNusuario = nombre,
                coLogin = login,
                coPasswordHash = passwordService.hash(request.password),
                coRol = request.rolEnum(),
                flBeliminado = false,
                feDfechaCreacion = LocalDateTime.now(),
            )
        return repository.save(entity).toUsuarioResponse()
    }

    @Transactional
    fun update(
        id: Int,
        request: InvTdvUsuarioUpdateRequest,
    ): InvTdvUsuarioResponse {
        val entity =
            repository.findByIdUsuarioAndFlBeliminadoFalse(id)
                ?: throw ResourceNotFoundException("Usuario no encontrado")

        val login = request.coLogin.trim()
        if (repository.existsByCoLoginIgnoreCaseAndFlBeliminadoFalseAndIdUsuarioNot(login, id)) {
            throw DuplicateLoginException("Ya existe otro usuario activo con ese login")
        }

        entity.noNusuario = request.noNusuario.trim()
        entity.coLogin = login
        entity.coRol = request.rolEnum()
        val newPassword = request.password?.trim().orEmpty()
        if (newPassword.isNotEmpty()) {
            entity.coPasswordHash = passwordService.hash(newPassword)
        }
        entity.feDfechaModificacion = LocalDateTime.now()
        return repository.save(entity).toUsuarioResponse()
    }

    @Transactional
    fun softDelete(
        id: Int,
        currentUserId: Int,
    ) {
        if (id == currentUserId) {
            throw BusinessRuleException("No puede eliminar su propio usuario mientras tiene la sesión activa")
        }
        val entity =
            repository.findByIdUsuarioAndFlBeliminadoFalse(id)
                ?: throw ResourceNotFoundException("Usuario no encontrado")
        entity.flBeliminado = true
        entity.feDfechaModificacion = LocalDateTime.now()
        repository.save(entity)
    }

    @Transactional(readOnly = true)
    fun findActiveEntityByLogin(login: String): InvTdvUsuario? =
        repository.findByCoLoginIgnoreCaseAndFlBeliminadoFalse(login.trim())

    @Transactional(readOnly = true)
    fun findActiveEntityById(id: Int): InvTdvUsuario? =
        repository.findByIdUsuarioAndFlBeliminadoFalse(id)

    @Transactional
    fun changePassword(
        id: Int,
        currentPassword: String,
        newPassword: String,
    ) {
        val user =
            repository.findByIdUsuarioAndFlBeliminadoFalse(id)
                ?: throw ResourceNotFoundException("Usuario no encontrado")
        if (!passwordService.matches(currentPassword, user.coPasswordHash)) {
            throw BusinessRuleException("La contraseña actual no es correcta")
        }
        user.coPasswordHash = passwordService.hash(newPassword)
        user.feDfechaModificacion = LocalDateTime.now()
        repository.save(user)
    }
}
