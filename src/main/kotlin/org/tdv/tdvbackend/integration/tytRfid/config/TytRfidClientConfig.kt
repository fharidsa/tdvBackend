package org.tdv.tdvbackend.integration.tytRfid.config

import java.net.Socket
import java.net.http.HttpClient
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.time.Duration
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLEngine
import javax.net.ssl.TrustManager
import javax.net.ssl.X509ExtendedTrustManager
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty(name = ["tyt-rfid.enabled"], havingValue = "true")
@EnableConfigurationProperties(TytRfidProperties::class)
class TytRfidClientConfig(
    private val properties: TytRfidProperties,
) {

    @Bean
    fun tytRfidHttpClient(): HttpClient {
        System.setProperty("jdk.httpclient.allowRestrictedHeaders", "host")

        val builder = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofMillis(properties.connectTimeoutMs))

        if (!properties.sslVerify) {
            val trustAll = arrayOf<TrustManager>(object : X509ExtendedTrustManager() {
                override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?, socket: Socket?) {}
                override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?, socket: Socket?) {}
                override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?, engine: SSLEngine?) {}
                override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?, engine: SSLEngine?) {}
                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            })
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, trustAll, SecureRandom())
            builder.sslContext(sslContext)
        }

        return builder.build()
    }
}
