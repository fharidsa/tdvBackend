package org.tdv.tdvbackend.web.advice

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.tdv.tdvbackend.web.dto.ApiErrorResponse
import org.tdv.tdvbackend.web.error.ApiErrorCode
import org.tdv.tdvbackend.web.error.ApiException
import org.tdv.tdvbackend.web.error.InvalidCredentialsException

@RestControllerAdvice
class ApiExceptionHandler {

    private val log = LoggerFactory.getLogger(ApiExceptionHandler::class.java)

    @ExceptionHandler(ApiException::class)
    fun handleApiException(ex: ApiException): ResponseEntity<ApiErrorResponse> =
        ResponseEntity
            .status(ex.status)
            .body(ApiErrorResponse(code = ex.code, message = ex.message))

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ApiErrorResponse> {
        val message =
            ex.bindingResult.fieldErrors
                .firstOrNull()
                ?.defaultMessage
                ?: "Datos inválidos"
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiErrorResponse(code = ApiErrorCode.VALIDATION_ERROR, message = message))
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(ex: AccessDeniedException): ResponseEntity<ApiErrorResponse> =
        ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(ApiErrorResponse(code = ApiErrorCode.FORBIDDEN, message = "Acceso denegado"))

    @ExceptionHandler(Exception::class)
    fun handleUnexpected(ex: Exception): ResponseEntity<ApiErrorResponse> {
        log.error("Unhandled exception in controller", ex)
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiErrorResponse(code = ApiErrorCode.VALIDATION_ERROR, message = "Error interno del servidor"))
    }
}
