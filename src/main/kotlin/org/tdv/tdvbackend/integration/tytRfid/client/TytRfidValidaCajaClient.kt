package org.tdv.tdvbackend.integration.tytRfid.client

import com.fasterxml.jackson.databind.ObjectMapper
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import org.tdv.tdvbackend.integration.tytRfid.config.TytRfidProperties
import org.tdv.tdvbackend.integration.tytRfid.dto.TytValidaCajaResponse
import org.tdv.tdvbackend.integration.tytRfid.exception.TytRfidException

@Component
@ConditionalOnProperty(name = ["tyt-rfid.enabled"], havingValue = "true")
class TytRfidValidaCajaClient(
    private val tytRfidHttpClient: HttpClient,
    private val properties: TytRfidProperties,
    private val objectMapper: ObjectMapper,
    private val authClient: TytRfidAuthClient,
) {
    private val log = LoggerFactory.getLogger(TytRfidValidaCajaClient::class.java)

    fun validarCaja(numCaja: String): TytValidaCajaResponse? {
        val url = "${properties.baseUrl}/api/ValidaCaja/Get?Num_Caja=$numCaja"
        val response = executeWithRetry(url)

        if (response.statusCode() == 404) return null

        if (response.statusCode() !in 200..299) {
            log.error("TyT ValidaCaja failed: status={}, body={}", response.statusCode(), response.body())
            throw TytRfidException("Error validando caja en TyT RFID: HTTP ${response.statusCode()}")
        }

        return objectMapper.readValue(response.body(), TytValidaCajaResponse::class.java)
    }

    private fun executeWithRetry(url: String): HttpResponse<String> {
        var response = doGet(url, authClient.getToken())
        if (response.statusCode() == 401) {
            authClient.invalidateToken()
            response = doGet(url, authClient.refreshToken())
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
}
