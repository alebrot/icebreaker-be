package com.icebreaker.be.db.entity

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.math.BigDecimal
import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "AK_POSITION", schema = "PUBLIC")
class AkUserPositionEntity {
    @get:Id
    @get:GeneratedValue(strategy = GenerationType.IDENTITY)
    @get:Column(name = "ID")
    var id: Int = 0

    @get:Basic
    @get:Column(name = "LAT")
    lateinit var latitude: BigDecimal

    @get:Basic
    @get:Column(name = "LON")
    lateinit var longitude: BigDecimal

    @get:Basic
    @get:CreationTimestamp
    @get:Column(name = "CREATED_AT")
    var createdAt: Timestamp? = null

    @get:Basic
    @get:UpdateTimestamp
    @get:Column(name = "UPDATED_AT")
    var updatedAt: Timestamp? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as AkUserPositionEntity?
        return id == that!!.id &&
                latitude == that.latitude &&
                longitude == that.longitude
    }
}