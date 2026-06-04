package org.tdv.tdvbackend.web

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import org.tdv.tdvbackend.security.TdvUserPrincipal
import org.tdv.tdvbackend.service.InvTdvUsuarioService
import org.tdv.tdvbackend.web.dto.usuario.InvTdvUsuarioCreateRequest
import org.tdv.tdvbackend.web.dto.usuario.InvTdvUsuarioResponse
import org.tdv.tdvbackend.web.dto.usuario.InvTdvUsuarioUpdateRequest

@RestController
@RequestMapping("/api/v1/inv-tdv-usuario")
@PreAuthorize("hasRole('ADMIN')")
class InvTdvUsuarioController(
    private val service: InvTdvUsuarioService,
) {

    @GetMapping
    fun list(): ResponseEntity<List<InvTdvUsuarioResponse>> =
        ResponseEntity.ok(service.listActive())

    @GetMapping("/{id}")
    fun getById(
        @PathVariable id: Int,
    ): ResponseEntity<InvTdvUsuarioResponse> =
        ResponseEntity.ok(service.findActiveById(id))

    @PostMapping
    fun create(
        @Valid @RequestBody body: InvTdvUsuarioCreateRequest,
    ): ResponseEntity<InvTdvUsuarioResponse> {
        val created = service.create(body)
        val location =
            ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.idUsuario)
                .toUri()
        return ResponseEntity.status(HttpStatus.CREATED).location(location).body(created)
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Int,
        @Valid @RequestBody body: InvTdvUsuarioUpdateRequest,
    ): ResponseEntity<InvTdvUsuarioResponse> =
        ResponseEntity.ok(service.update(id, body))

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: Int,
        @AuthenticationPrincipal principal: TdvUserPrincipal,
    ): ResponseEntity<Void> {
        service.softDelete(id, principal.idUsuario)
        return ResponseEntity.noContent().build()
    }
}
