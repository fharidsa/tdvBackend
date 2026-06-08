package org.tdv.tdvbackend.service

import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket
import java.time.LocalDate
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ZebraLabelPrinterService(
    @Value("\${tdv.printer.host}") private val printerHost: String,
    @Value("\${tdv.printer.port}") private val printerPort: Int,
    @Value("\${tdv.printer.enabled:true}") private val printerEnabled: Boolean,
    @Value("\${tdv.printer.connect-timeout-ms:3000}") private val connectTimeoutMs: Int,
    @Value("\${tdv.printer.socket-timeout-ms:3000}") private val socketTimeoutMs: Int,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Imprime etiqueta 1 x 1 pulgada con nombre de usuario y fecha.
     * @throws IllegalStateException si la impresora está deshabilitada o falla la conexión
     */
    fun printUserDateLabel(
        usuario: String,
        fecha: LocalDate,
        hostOverride: String? = null,
        portOverride: Int? = null,
    ) {
        if (!tryPrintUserDateLabel(usuario, fecha, hostOverride, portOverride)) {
            throw IllegalStateException("No se pudo enviar la etiqueta a la impresora")
        }
    }

    /** @return true si la etiqueta se envió a la impresora; false si está deshabilitada o falló. */
    fun tryPrintUserDateLabel(
        usuario: String,
        fecha: LocalDate,
        hostOverride: String? = null,
        portOverride: Int? = null,
    ): Boolean {
        if (!printerEnabled) {
            log.debug("Impresión deshabilitada (tdv.printer.enabled=false); se omite etiqueta para {}", usuario)
            return false
        }
        return runCatching {
            val zpl = UserDateLabelZplBuilder.build(usuario = usuario, fecha = fecha.toString())
            val host = resolvePrinterHost(hostOverride)
            val port = resolvePrinterPort(portOverride)
            sendZpl(zpl, hostOverride, portOverride)
            log.info("Etiqueta usuario/fecha enviada a {}:{} para {}", host, port, usuario)
            true
        }.getOrElse {
            log.warn("Error al imprimir etiqueta para '{}': {}", usuario, it.message)
            false
        }
    }

    fun buildUserDateLabelZpl(usuario: String, fecha: LocalDate): String =
        UserDateLabelZplBuilder.build(usuario = usuario, fecha = fecha.toString())

    fun printZpl(
        zpl: String,
        hostOverride: String? = null,
        portOverride: Int? = null,
    ) {
        if (!printerEnabled) {
            throw IllegalStateException("Impresora deshabilitada (tdv.printer.enabled=false)")
        }
        sendZpl(zpl, hostOverride, portOverride)
    }

    // SECURITY-REVIEW: conexión TCP directa a impresora en red interna; host validado desde config o petición.
    private fun sendZpl(
        zpl: String,
        hostOverride: String?,
        portOverride: Int?,
    ) {
        val host = resolvePrinterHost(hostOverride)
        val port = resolvePrinterPort(portOverride)
        try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(host, port), connectTimeoutMs.coerceAtLeast(500))
                socket.soTimeout = socketTimeoutMs.coerceAtLeast(500)
                val out: OutputStream = socket.getOutputStream()
                out.write(zpl.toByteArray(Charsets.UTF_8))
                out.flush()
            }
        } catch (e: Exception) {
            throw IllegalStateException("No se pudo enviar la etiqueta a la impresora", e)
        }
    }

    private fun resolvePrinterHost(override: String?): String {
        val host = override?.trim()?.takeIf { it.isNotEmpty() } ?: printerHost.trim()
        require(host.matches(HOST_PATTERN)) { "Dirección de impresora no válida" }
        return host
    }

    private fun resolvePrinterPort(override: Int?): Int {
        val port = override ?: printerPort
        require(port in 1..65535) { "Puerto de impresora no válido" }
        return port
    }

    companion object {
        private val HOST_PATTERN = Regex("^[a-zA-Z0-9.\\-]{1,253}$")
    }
}
