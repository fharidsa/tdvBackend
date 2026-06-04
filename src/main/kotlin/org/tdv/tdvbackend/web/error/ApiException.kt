package org.tdv.tdvbackend.web.error

open class ApiException(
    val code: ApiErrorCode,
    override val message: String,
    val status: Int,
) : RuntimeException(message)

class ResourceNotFoundException(
    message: String = "Recurso no encontrado",
) : ApiException(ApiErrorCode.NOT_FOUND, message, 404)

class DuplicateLoginException(
    message: String = "Ya existe un usuario activo con ese login",
) : ApiException(ApiErrorCode.DUPLICATE_LOGIN, message, 409)

class InvalidCredentialsException(
    message: String = "Usuario o contraseña incorrectos",
) : ApiException(ApiErrorCode.INVALID_CREDENTIALS, message, 401)

class BusinessRuleException(
    message: String,
) : ApiException(ApiErrorCode.BUSINESS_RULE, message, 400)
