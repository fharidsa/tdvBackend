package org.tdv.tdvbackend.integration.tytRfid.client

import com.fasterxml.jackson.databind.ObjectMapper
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import org.tdv.tdvbackend.integration.tytRfid.config.TytRfidProperties
import org.tdv.tdvbackend.integration.tytRfid.dto.TytTokenRequest
import org.tdv.tdvbackend.integration.tytRfid.dto.TytTokenResponse
import org.tdv.tdvbackend.integration.tytRfid.exception.TytRfidException

@Component
@ConditionalOnProperty(name = ["tyt-rfid.enabled"], havingValue = "true")
class TytRfidAuthClient(
    private val tytRfidHttpClient: HttpClient,
    private val properties: TytRfidProperties,
    private val objectMapper: ObjectMapper,
) {
    private val log = LoggerFactory.getLogger(TytRfidAuthClient::class.java)
    private val lock = ReentrantLock()

    @Volatile
    private var cachedToken: String? = null

    fun getToken(): String {
        cachedToken?.let { return it }
        return refreshToken()
    }

    fun refreshToken(): String = lock.withLock {
        val url = "${properties.baseUrl}/api/Authenticate/TokenLogin"
        val body = objectMapper.writeValueAsString(
            TytTokenRequest(clientId = properties.clientId, clientSecret = properties.clientSecret)
        )

        log.debug("TyT TokenLogin request: url={}, hostHeader={}, body={}", url, properties.hostHeader, body)

        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/json")
            .header("Host", properties.hostHeader)
            .timeout(Duration.ofMillis(properties.readTimeoutMs))
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build()

        log.debug("TyT TokenLogin request headers: {}", request.headers().map())

        val response = tytRfidHttpClient.send(request, HttpResponse.BodyHandlers.ofString())

        log.debug("TyT TokenLogin response: status={}, headers={}", response.statusCode(), response.headers().map())

        if (response.statusCode() !in 200..299) {
            log.error("TyT TokenLogin failed: status={}, body={}", response.statusCode(), response.body())
            throw TytRfidException("Error obteniendo token de TyT RFID: HTTP ${response.statusCode()}")
        }

        val tokenResponse = objectMapper.readValue(response.body(), TytTokenResponse::class.java)
        val token = tokenResponse.resolveToken()
        cachedToken = token
        log.info("TyT RFID token obtained successfully")
        return token
    }

    fun invalidateToken() {
        cachedToken = null
    }
}
