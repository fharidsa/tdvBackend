package org.tdv.tdvbackend.integration.tytRfid.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class TytEmpleadoResponse(
    val codEmpleado: String? = null,
    val nombre: String? = null,
    val correo: String? = null,
    val telefono: String? = null,
    val dni: String? = null,
)
