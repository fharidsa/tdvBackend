package org.tdv.tdvbackend.integration.tytRfid.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class TytValidaCajaResponse(
    @JsonProperty("num_Caja")
    val numCaja: Int,
    @JsonProperty("num_Packing")
    val numPacking: Int,
    @JsonProperty("num_Prendas")
    val numPrendas: Int,
    @JsonProperty("num_Prendas_Leidas")
    val numPrendasLeidas: Int,
)
