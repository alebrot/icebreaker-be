package com.icebreaker.be.db.entity

import com.icebreaker.be.service.model.Gender
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.sql.Date
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*
import kotlin.collections.ArrayList

@Entity
@Table(name = "AK_USER", schema = "kofify")
class AkUserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    var id: Int = 0
    @Basic
    @Column(name = "FIRST_NAME")
    lateinit var firstName: String
    @Basic
    @Column(name = "LAST_NAME")
    var lastName: String? = null
    @Basic
    @Column(name = "EMAIL")
    lateinit var email: String

    @Enumerated(EnumType.ORDINAL)
    @Basic
    @Column(name = "GENDER")
    var gender: Gender? = null

    @Basic
    @Column(name = "BIRTHDAY")
    lateinit var birthday: Date

    @Basic
    @Column(name = "BIO")
    var bio: String? = null

    @Basic
    @Column(name = "IMG_URL")
    var imgUrl: String? = null

    @Basic
    @Column(name = "PASSWORD_HASH")
    var passwordHash: String? = null

    @Basic
    @Column(name = "ACCOUNT_EXPIRED")
    var accountExpired: Boolean = false

    @Basic
    @Column(name = "ACCOUNT_LOCKED")
    var accountLocked: Boolean = false

    @Basic
    @Column(name = "CREDENTIALS_EXPIRED")
    var credentialsExpired: Boolean = false

    @Basic
    @Column(name = "ENABLED")
    var enabled: Boolean = true

    @Basic
    @CreationTimestamp
    @Column(name = "CREATED_AT")
    var createdAt: Timestamp = Timestamp.valueOf(LocalDateTime.now())

    @Basic
    @UpdateTimestamp
    @Column(name = "UPDATED_AT")
    var updatedAt: Timestamp = Timestamp.valueOf(LocalDateTime.now())

    @Basic
    @Column(name = "LAST_SEEN")
    var lastSeen: Timestamp = Timestamp.valueOf(LocalDateTime.now())

    @Basic
    @Column(name = "CREDITS")
    var credits: Int = 5

    @Basic
    @Column(name = "CREDITS_UPDATED_AT")
    var creditsUpdatedAt: Timestamp = Timestamp.valueOf(LocalDateTime.now())

    @Basic
    @Column(name = "ADMOB_COUNT")
    var admobCount: Int = 0

    @Basic
    @Column(name = "ADMOB_UPDATED_AT")
    var admobUpdatedAt: Timestamp = Timestamp.valueOf(LocalDateTime.now())

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "AK_USER_AUTHORITY", joinColumns = [JoinColumn(name = "USER_ID", referencedColumnName = "ID")], inverseJoinColumns = [JoinColumn(name = "AUTHORITY_ID", referencedColumnName = "ID")])
    var authorities: Collection<AkAuthorityEntity> = ArrayList()

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "users")
    var chats: Collection<AkChatEntity> = ArrayList()

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    var images: Collection<AkUserImageEntity> = ArrayList()

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POSITION_ID", referencedColumnName = "ID", nullable = true)
    var position: AkUserPositionEntity? = null

    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "PUSH_ID", referencedColumnName = "ID", nullable = true)
    var push: AkPushEntity? = null

    @Basic
    @Column(name = "INVITED_BY")
    var invitedBy: Int? = null

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
