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
import org.tdv.tdvbackend.integration.tytRfid.dto.TytValidaCajaResponse
import org.tdv.tdvbackend.integration.tytRfid.exception.TytRfidException

@Component
@ConditionalOnProperty(name = ["tyt-rfid.enabled"], havingValue = "true")
class TytRfidValidaCajaClient(
    private val tytRfidHttpClient: HttpClient,
    private val properties: TytRfidProperties,
    private val authClient: TytRfidAuthClient,
) {
    private val log = LoggerFactory.getLogger(TytRfidValidaCajaClient::class.java)
    private val objectMapper = jacksonObjectMapper()

    fun validarCaja(numIca: String): TytValidaCajaResponse? {
        val url = "${properties.baseUrl}/api/ValidaIca/Get?Num_Ica=$numIca"
        log.info("TyT ValidaIca request: numIca={}, url={}", numIca, url)

        val response = executeWithRetry(url)
        log.info("TyT ValidaIca response: status={}, body={}", response.statusCode(), response.body())

        if (response.statusCode() == 404) return null

        if (response.statusCode() !in 200..299) {
            log.error("TyT ValidaIca failed: status={}, body={}", response.statusCode(), response.body())
            throw TytRfidException("Error validando ICA en TyT RFID: HTTP ${response.statusCode()}")
        }

        val result = objectMapper.readValue(response.body(), TytValidaCajaResponse::class.java)
        log.info("TyT ValidaIca parsed: {}", result)
        return result
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
        // TODO: remove - debug only
        log.debug("TyT HTTP request: method=GET, url={}, Host={}, Authorization=Bearer {}", url, properties.hostHeader, token)
        return tytRfidHttpClient.send(request, HttpResponse.BodyHandlers.ofString())
    }
}
