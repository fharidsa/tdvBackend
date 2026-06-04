package org.tdv.tdvbackend.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.tdv.tdvbackend.domain.enums.UsuarioRol

class TdvUserPrincipal(
    val idUsuario: Int,
    val noNusuario: String,
    val coLogin: String,
    val coRol: UsuarioRol,
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> =
        listOf(SimpleGrantedAuthority("ROLE_${coRol.name}"))

    override fun getPassword(): String = ""

    override fun getUsername(): String = coLogin

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true
}
