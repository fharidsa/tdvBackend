package org.tdv.tdvbackend.web

import java.time.LocalDate
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import org.tdv.tdvbackend.service.InvTdvConteoIcaService
import org.tdv.tdvbackend.web.dto.InvTdvConteoIcaCreateRequest
import org.tdv.tdvbackend.web.dto.InvTdvConteoIcaPageDto
import org.tdv.tdvbackend.web.dto.InvTdvConteoIcaResponse

/**
 * Recurso `inv-tdv-conteo-ica` (REST sobre HTTP).
 *
 * - **GET** sin `id`: listado **paginado** (`InvTdvConteoIcaPageDto`, 200 OK).
 *   Query opcionales: `fecha_desde`, `fecha_hasta` (ISO fecha `yyyy-MM-dd`, rango inclusive por día en zona del servidor),
 *   `conforme` (`true` = conformes, `false` = no conformes, omitido = todos),
 *   paginación estándar Spring: `page`, `size`, `sort` (p. ej. `sort=feDfecha,desc`).
 * - **GET** con `id`: un registro (200 o 404).
 * - **POST**: alta (201 Created, cuerpo del recurso; cabecera Location al GET con ese id).
 */
@RestController
@RequestMapping("/api/v1/inv-tdv-conteo-ica")
class InvTdvConteoIcaController(
    private val service: InvTdvConteoIcaService,
) {

    @GetMapping
    fun list(
        @RequestParam(name = "fecha_desde", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        fechaDesde: LocalDate?,
        @RequestParam(name = "fecha_hasta", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        fechaHasta: LocalDate?,
        @RequestParam(required = false)
        conforme: Boolean?,
        @PageableDefault(
            size = 20,
            sort = ["feDfecha", "idConteoIca"],
            direction = Sort.Direction.DESC,
        )
        pageable: Pageable,
    ): ResponseEntity<InvTdvConteoIcaPageDto> =
        ResponseEntity.ok(service.search(fechaDesde, fechaHasta, conforme, pageable))

    @GetMapping("/{id}")
    fun getById(
        @PathVariable id: Long,
    ): ResponseEntity<InvTdvConteoIcaResponse> =
        service.findById(id)
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()

    @PostMapping
    fun create(
        @RequestBody body: InvTdvConteoIcaCreateRequest,
    ): ResponseEntity<InvTdvConteoIcaResponse> {
        val created = service.create(body)
        val location =
            ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.idConteoIca)
                .toUri()
        return ResponseEntity.status(HttpStatus.CREATED).location(location).body(created)
    }
}
