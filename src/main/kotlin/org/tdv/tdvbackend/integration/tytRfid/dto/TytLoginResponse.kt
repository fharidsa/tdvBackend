package org.tdv.tdvbackend.integration.tytRfid.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class TytLoginResponse(
    val codEmpleado: String? = null,
    val login: String? = null,
    val empleado: String? = null,
    val correoElectronico: String? = null,
    val message: String? = null,
) {
    val valid: Boolean
        get() = !codEmpleado.isNullOrBlank()

    val nombre: String?
        get() = empleado
}
