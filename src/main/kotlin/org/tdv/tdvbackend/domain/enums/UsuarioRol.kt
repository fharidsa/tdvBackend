package org.tdv.tdvbackend.domain.enums

enum class UsuarioRol {
    ADMIN,
    USER,
    ;

    companion object {
        fun fromWire(value: String): UsuarioRol {
            val normalized = value.trim().uppercase()
            return entries.find { it.name == normalized } ?: USER
        }
    }
}
