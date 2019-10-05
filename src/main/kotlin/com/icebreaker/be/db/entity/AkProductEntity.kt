package com.icebreaker.be.db.entity

import com.icebreaker.be.service.model.Store
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.sql.Timestamp
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "AK_PRODUCT", schema = "kofify")
class AkProductEntity {

    @get:Id
    @get:GeneratedValue(strategy = GenerationType.IDENTITY)
    @get:Column(name = "ID")
    var id: Int = 0
    @get:Basic
    @get:Column(name = "NAME")
    var name: String? = null

    @get:Basic
    @get:Column(name = "PRODUCT_ID", unique = true)
    var productId: String? = null

    @get:Basic
    @get:CreationTimestamp
    @get:Column(name = "CREATED_AT")
    var createdAt: Timestamp? = null

    @get:Basic
    @get:UpdateTimestamp
    @get:Column(name = "UPDATED_AT")
    var updatedAt: Timestamp? = null

    @get:Enumerated(EnumType.ORDINAL)
    @get:Basic
    @get:Column(name = "STORE")
    var store: Store? = null

    @get:Basic
    @get:Column(name = "DESCRIPTION")
    var description: String? = null

    @get:Basic
    @get:Column(name = "CREDITS")
    var credits: Int = 0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as AkProductEntity?
        return id == that!!.id && name == that.name
    }

    override fun hashCode(): Int {
        return Objects.hash(id, name)
    }
}
