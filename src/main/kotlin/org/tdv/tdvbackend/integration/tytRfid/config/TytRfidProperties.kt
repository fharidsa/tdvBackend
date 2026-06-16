package org.tdv.tdvbackend.integration.tytRfid.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "tyt-rfid")
data class TytRfidProperties(
    val enabled: Boolean = false,
    val baseUrl: String = "https://192.168.1.56:8080/TyT_Rfid_Be",
    val hostHeader: String = "devapps.tdv.pe",
    val clientId: String = "",
    val clientSecret: String = "",
    val sslVerify: Boolean = false,
    val connectTimeoutMs: Long = 10000,
    val readTimeoutMs: Long = 15000,
)
