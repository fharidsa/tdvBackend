package org.tdv.tdvbackend.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "inv_tdv_ica", schema = "dbo")
class InvTdvIca(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ica")
    var id: Long? = null,

    @Column(name = "co_cica", length = 1000)
    var coCica: String? = null,

    @Column(name = "nu_ncantidad")
    var nuNCantidad: Int? = null,
)
