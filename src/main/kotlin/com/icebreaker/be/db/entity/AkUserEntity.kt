package com.icebreaker.be.db.entity

import com.icebreaker.be.service.model.Gender
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.sql.Date
import java.sql.Timestamp
import java.util.*
import javax.persistence.*
import kotlin.collections.ArrayList

@Entity
@Table(name = "AK_USER", schema = "PUBLIC")
class AkUserEntity {
    @get:Id
    @get:GeneratedValue(strategy = GenerationType.IDENTITY)
    @get:Column(name = "ID")
    var id: Int = 0
    @get:Basic
    @get:Column(name = "FIRST_NAME")
    lateinit var firstName: String
    @get:Basic
    @get:Column(name = "LAST_NAME")
    lateinit var lastName: String
    @get:Basic
    @get:Column(name = "EMAIL")
    lateinit var email: String

    @get:Enumerated(EnumType.ORDINAL)
    @get:Basic
    @get:Column(name = "GENDER")
    var gender: Gender? = null

    @get:Basic
    @get:Column(name = "BIRTHDAY")
    lateinit var birthday: Date

    @get:Basic
    @get:Column(name = "BIO")
    var bio: String? = null

    @get:Basic
    @get:Column(name = "IMG_URL")
    var imgUrl: String? = null

    @get:Basic
    @get:Column(name = "PASSWORD_HASH")
    var passwordHash: String? = null

    @get:Basic
    @get:Column(name = "ACCOUNT_EXPIRED")
    var accountExpired: Boolean = false

    @get:Basic
    @get:Column(name = "ACCOUNT_LOCKED")
    var accountLocked: Boolean = false

    @get:Basic
    @get:Column(name = "CREDENTIALS_EXPIRED")
    var credentialsExpired: Boolean = false

    @get:Basic
    @get:Column(name = "ENABLED")
    var enabled: Boolean = true

    @get:Basic
    @get:CreationTimestamp
    @get:Column(name = "CREATED_AT")
    var createdAt: Timestamp? = null
    @get:Basic
    @get:UpdateTimestamp
    @get:Column(name = "UPDATED_AT")
    var updatedAt: Timestamp? = null

    @get:Basic
    @get:Column(name = "LAST_SEEN")
    var lastSeen: Timestamp? = null

    @get:ManyToMany(fetch = FetchType.LAZY)
    @get:JoinTable(name = "AK_USER_AUTHORITY", joinColumns = [JoinColumn(name = "USER_ID", referencedColumnName = "ID")], inverseJoinColumns = [JoinColumn(name = "AUTHORITY_ID", referencedColumnName = "ID")])
    var authorities: Collection<AkAuthorityEntity> = ArrayList()

    @get:ManyToMany(fetch = FetchType.LAZY, mappedBy = "users")
    var chats: Collection<AkChatEntity> = ArrayList()

    @get:OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    var images: Collection<AkUserImageEntity> = ArrayList()

    @get:OneToOne(fetch = FetchType.LAZY)
    @get:JoinColumn(name = "POSITION_ID", referencedColumnName = "ID", nullable = true)
    var position: AkUserPositionEntity? = null


    @get:OneToOne(fetch = FetchType.LAZY)
    @get:JoinColumn(name = "PUSH_ID", referencedColumnName = "ID", nullable = true)
    var push: AkPushEntity? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as AkUserEntity?
        return id == that!!.id &&
                firstName == that.firstName &&
                lastName == that.lastName &&
                email == that.email &&
                createdAt == that.createdAt &&
                updatedAt == that.updatedAt
    }

    override fun hashCode(): Int {
        return Objects.hash(id, firstName, lastName, email, createdAt, updatedAt)
    }
}
