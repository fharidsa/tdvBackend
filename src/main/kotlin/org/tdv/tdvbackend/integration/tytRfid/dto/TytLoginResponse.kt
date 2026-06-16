package org.tdv.tdvbackend.integration.tytRfid.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class TytLoginResponse(
    val valid: Boolean = false,
    val codEmpleado: String? = null,
    val nombre: String? = null,
    val message: String? = null,
)
