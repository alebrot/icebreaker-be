package com.icebreaker.be.db.entity

import com.icebreaker.be.service.auth.social.SocialType
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.sql.Timestamp
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "AK_SOCIAL", schema = "PUBLIC", catalog = "DEFAULT")
class AkSocialEntity {
    @get:Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @get:Column(name = "ID")
    var id: Int = 0
    @get:Basic
    @get:Column(name = "SOCIAL_ID")
    lateinit var socialId: String

    @get:Enumerated(EnumType.ORDINAL)
    @get:Basic
    @get:Column(name = "SOCIAL_TYPE")
    lateinit var type: SocialType

    @get:Basic
    @get:Column(name = "EMAIL")
    lateinit var email: String

    @get:ManyToOne
    @get:JoinColumn(name = "USER_ID", referencedColumnName = "ID", nullable = false)
    var user: AkUserEntity? = null

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
        val that = other as AkSocialEntity?
        return id == that!!.id &&
                socialId == that.socialId &&
                user == that.user &&
                email == that.email &&
                type == that.type
    }
}