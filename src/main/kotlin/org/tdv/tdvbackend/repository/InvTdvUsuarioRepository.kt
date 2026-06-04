package org.tdv.tdvbackend.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.tdv.tdvbackend.domain.entity.InvTdvUsuario

interface InvTdvUsuarioRepository : JpaRepository<InvTdvUsuario, Int> {
    fun findAllByFlBeliminadoFalseOrderByNoNusuarioAsc(): List<InvTdvUsuario>

    fun findByIdUsuarioAndFlBeliminadoFalse(idUsuario: Int): InvTdvUsuario?

    fun findByCoLoginIgnoreCaseAndFlBeliminadoFalse(coLogin: String): InvTdvUsuario?

    fun existsByCoLoginIgnoreCaseAndFlBeliminadoFalse(coLogin: String): Boolean

    fun existsByCoLoginIgnoreCaseAndFlBeliminadoFalseAndIdUsuarioNot(
        coLogin: String,
        idUsuario: Int,
    ): Boolean
}
