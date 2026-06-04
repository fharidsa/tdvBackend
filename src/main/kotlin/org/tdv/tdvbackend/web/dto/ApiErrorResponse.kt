package org.tdv.tdvbackend.web.dto

import com.fasterxml.jackson.annotation.JsonProperty
import org.tdv.tdvbackend.web.error.ApiErrorCode

data class ApiErrorResponse(
    @JsonProperty("code")
    val code: ApiErrorCode,
    @JsonProperty("message")
    val message: String,
)
