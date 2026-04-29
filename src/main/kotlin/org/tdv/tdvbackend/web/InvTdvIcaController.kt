package org.tdv.tdvbackend.web

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.tdv.tdvbackend.service.InvTdvIcaService
import org.tdv.tdvbackend.web.dto.InvTdvIcaResponse

@RestController
@RequestMapping("/api/v1/inv-tdv-ica")
class InvTdvIcaController(
    private val service: InvTdvIcaService,
) {

    /**
     * Sin [coCica]: lista completa.
     * Con [coCica]: un registro con ese valor exacto en columna co_cica, o 404.
     */
    @GetMapping
    fun get(
        @RequestParam(name = "co_cica", required = false) coCica: String?,
    ): ResponseEntity<Any> =
        when {
            coCica.isNullOrBlank() -> ResponseEntity.ok(service.findAll())
            else ->
                service.findByCoCica(coCica.trim())
                    ?.let { ResponseEntity.ok(it) }
                    ?: ResponseEntity.notFound().build()
        }
}
