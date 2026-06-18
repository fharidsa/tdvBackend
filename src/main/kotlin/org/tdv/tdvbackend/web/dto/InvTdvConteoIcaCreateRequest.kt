package org.tdv.tdvbackend.web.dto

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Cuerpo JSON para registrar un conteo. Acepta nombres en camelCase o como columnas SQL (snake_case).
 */
data class InvTdvConteoIcaCreateRequest(
    @JsonProperty("id_ica")
    @JsonAlias("idIca")
    val idIca: Long? = null,
    @JsonProperty("nu_ncantidad_ica")
    @JsonAlias("nuNcantidadIca")
    val nuNcantidadIca: Int? = null,
    @JsonProperty("nu_nlecturas")
    @JsonAlias("nuNlecturas")
    val nuNlecturas: Int? = null,
    @JsonProperty("co_cica_ica")
    @JsonAlias("coCicaIca")
    val coCicaIca: String? = null,
    @JsonProperty("num_packing")
    @JsonAlias("numPacking")
    val numPacking: Int? = null,
    @JsonProperty("id_usuario")
    @JsonAlias("idUsuario")
    val idUsuario: Int? = null,
    @JsonProperty("no_usuario")
    @JsonAlias("noUsuario")
    val noUsuario: String? = null,
)
