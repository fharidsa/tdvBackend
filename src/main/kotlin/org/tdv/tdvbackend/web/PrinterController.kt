package org.tdv.tdvbackend.web

import java.time.LocalDateTime
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.tdv.tdvbackend.security.TdvUserPrincipal
import org.tdv.tdvbackend.service.ConteoLabelZplBuilder
import org.tdv.tdvbackend.service.ZebraLabelPrinterService
import org.tdv.tdvbackend.web.dto.PrinterTestRequest
import org.springframework.security.core.annotation.AuthenticationPrincipal

@RestController
@RequestMapping("/api/v1/printer")
class PrinterController(
    private val labelPrinter: ZebraLabelPrinterService,
) {

    @PostMapping("/test-print")
    fun testPrint(): ResponseEntity<Map<String, Any>> {
        val now = LocalDateTime.now()
        val fecha = now.toLocalDate().toString()
        val hora = "%02d:%02d:%02d".format(now.hour, now.minute, now.second)
        val zpl =
            ConteoLabelZplBuilder.build(
                usuario = "Juan Perez",
                codigoIca = "E28011606000040341234567",
                fecha = fecha,
                hora = hora,
                cantidadEsperada = 128,
                conteoRealizado = 125,
            )
        return runCatching {
            labelPrinter.printZpl(zpl)
            ok("Etiqueta de prueba enviada")
        }.getOrElse { printerError(it) }
    }

    @PostMapping("/test-print-user-date")
    fun testPrintUserDate(
        @AuthenticationPrincipal principal: TdvUserPrincipal,
        @RequestBody(required = false) body: PrinterTestRequest?,
    ): ResponseEntity<Map<String, Any>> {
        val now = LocalDateTime.now()
        val usuario = principal.noNusuario.ifBlank { principal.coLogin }
        return runCatching {
            labelPrinter.printUserDateLabel(
                usuario = usuario,
                fecha = now.toLocalDate(),
                hostOverride = body?.printerHost,
                portOverride = body?.printerPort,
            )
            ok("Etiqueta usuario/fecha enviada")
        }.getOrElse { printerError(it) }
    }

    private fun ok(message: String): ResponseEntity<Map<String, Any>> =
        ResponseEntity.ok(mapOf("success" to true, "message" to message))

    private fun printerError(e: Throwable): ResponseEntity<Map<String, Any>> =
        ResponseEntity.status(500).body(
            mapOf("success" to false, "message" to "Error al imprimir: ${e.message}"),
        )
}
