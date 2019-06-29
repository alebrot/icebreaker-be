package com.icebreaker.be.db.entity

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "AK_USER_IMAGE", schema = "PUBLIC", catalog = "DEFAULT")
class AkUserImageEntity {
    @get:Id
    @get:GeneratedValue(strategy = GenerationType.IDENTITY)
    @get:Column(name = "ID")
    var id: Int = 0
    @get:Basic
    @get:Column(name = "IMAGE_NAME")
    var imageName: String? = null

    @get:Basic
    @get:Column(name = "POSITION")
    var position: Int = 0

    @get:ManyToOne
    @get:JoinColumn(name = "USER_ID", referencedColumnName = "ID", nullable = false)
    var user: AkUserEntity? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as AkUserImageEntity?
        return id == that!!.id && imageName == that.imageName && position == that.position
    }

    override fun hashCode(): Int {
        return Objects.hash(id, imageName, position)
    }
}
