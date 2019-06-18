package com.icebreaker.be.db.entity

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "AK_AUTHORITY", schema = "PUBLIC", catalog = "DEFAULT")
class AkAuthorityEntity {
    @get:Id
    @get:GeneratedValue(strategy = GenerationType.IDENTITY)
    @get:Column(name = "ID")
    var id: Int = 0
    @get:Basic
    @get:Column(name = "NAME")
    lateinit var name: String

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as AkAuthorityEntity?
        return id == that!!.id && name == that.name
    }

    override fun hashCode(): Int {
        return Objects.hash(id, name)
    }
}
