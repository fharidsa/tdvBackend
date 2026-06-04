package org.tdv.tdvbackend.web.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class PrinterTestRequest(
    @JsonProperty("printerHost")
    val printerHost: String? = null,
    @JsonProperty("printerPort")
    val printerPort: Int? = null,
)
