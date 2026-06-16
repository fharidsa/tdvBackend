package org.tdv.tdvbackend.integration.tytRfid.client

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import org.tdv.tdvbackend.integration.tytRfid.config.TytRfidProperties
import org.tdv.tdvbackend.integration.tytRfid.dto.TytEmpleadoResponse
import org.tdv.tdvbackend.integration.tytRfid.dto.TytLoginRequest
import org.tdv.tdvbackend.integration.tytRfid.dto.TytLoginResponse
import org.tdv.tdvbackend.integration.tytRfid.exception.TytRfidException

@Component
@ConditionalOnProperty(name = ["tyt-rfid.enabled"], havingValue = "true")
class TytRfidValidarUsuarioClient(
    private val tytRfidHttpClient: HttpClient,
    private val properties: TytRfidProperties,
    private val authClient: TytRfidAuthClient,
) {
    private val log = LoggerFactory.getLogger(TytRfidValidarUsuarioClient::class.java)
    private val objectMapper = jacksonObjectMapper()

    fun login(login: String, password: String): TytLoginResponse {
        val url = "${properties.baseUrl}/api/ValidarUsuario/Login_Get"
        val body = objectMapper.writeValueAsString(TytLoginRequest(login = login, contrasenia = password))
        val response = executePostWithRetry(url, body)

        if (response.statusCode() !in 200..299) {
            log.error("TyT Login_Get failed: status={}, body={}", response.statusCode(), response.body())
            throw TytRfidException("Error validando usuario en TyT RFID: HTTP ${response.statusCode()}")
        }

        return objectMapper.readValue(response.body(), TytLoginResponse::class.java)
    }

    fun obtenerEmpleado(codEmpleado: String): TytEmpleadoResponse? {
        val url = "${properties.baseUrl}/api/ValidarUsuario/CodEmpleado_Get?CodEmpleado=$codEmpleado"
        val response = executeGetWithRetry(url)

        if (response.statusCode() == 404) return null

        if (response.statusCode() !in 200..299) {
            log.error("TyT CodEmpleado_Get failed: status={}, body={}", response.statusCode(), response.body())
            throw TytRfidException("Error obteniendo empleado de TyT RFID: HTTP ${response.statusCode()}")
        }

        return objectMapper.readValue(response.body(), TytEmpleadoResponse::class.java)
    }

    private fun executeGetWithRetry(url: String): HttpResponse<String> {
        var response = doGet(url, authClient.getToken())
        if (response.statusCode() == 401) {
            authClient.invalidateToken()
            response = doGet(url, authClient.refreshToken())
        }
        return response
    }

    private fun executePostWithRetry(url: String, body: String): HttpResponse<String> {
        val token = authClient.getToken()
        log.debug("TyT executePostWithRetry: url={}, token={}...", url, token.take(20))
        var response = doPost(url, body, token)
        log.debug("TyT executePostWithRetry first attempt: status={}, body={}", response.statusCode(), response.body())
        if (response.statusCode() == 401) {
            authClient.invalidateToken()
            val newToken = authClient.refreshToken()
            log.debug("TyT executePostWithRetry retry with new token={}...", newToken.take(20))
            response = doPost(url, body, newToken)
            log.debug("TyT executePostWithRetry retry: status={}, body={}", response.statusCode(), response.body())
        }
        return response
    }

    private fun doGet(url: String, token: String): HttpResponse<String> {
        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Host", properties.hostHeader)
            .header("Authorization", "Bearer $token")
            .header("Accept", "application/json")
            .timeout(Duration.ofMillis(properties.readTimeoutMs))
            .GET()
            .build()
        return tytRfidHttpClient.send(request, HttpResponse.BodyHandlers.ofString())
    }

    private fun doPost(url: String, body: String, token: String): HttpResponse<String> {
        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Host", properties.hostHeader)
            .header("Authorization", "Bearer $token")
            .header("Content-Type", "application/json")
            .timeout(Duration.ofMillis(properties.readTimeoutMs))
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build()
        log.debug("TyT doPost request: url={}, headers={}, body={}", url, request.headers().map(), body)
        val response = tytRfidHttpClient.send(request, HttpResponse.BodyHandlers.ofString())
        log.debug("TyT doPost response: status={}, headers={}", response.statusCode(), response.headers().map())
        return response
    }
}
