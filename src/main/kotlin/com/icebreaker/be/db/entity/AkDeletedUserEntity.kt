package com.icebreaker.be.db.entity

import com.icebreaker.be.service.model.Gender
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.sql.Date
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "AK_DELETED_USER", schema = "kofify")
class AkDeletedUserEntity {
    @get:Id
    @get:GeneratedValue(strategy = GenerationType.IDENTITY)
    @get:Column(name = "ID")
    var id: Int = 0
    @get:Basic
    @get:Column(name = "FIRST_NAME")
    lateinit var firstName: String
    @get:Basic
    @get:Column(name = "LAST_NAME")
    var lastName: String? = null
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
    @get:CreationTimestamp
    @get:Column(name = "CREATED_AT")
    var createdAt: Timestamp = Timestamp.valueOf(LocalDateTime.now())

    @get:Basic
    @get:UpdateTimestamp
    @get:Column(name = "UPDATED_AT")
    var updatedAt: Timestamp = Timestamp.valueOf(LocalDateTime.now())

    @get:Basic
    @get:Column(name = "USER_CREATED_AT")
    var userCreatedAt: Timestamp = Timestamp.valueOf(LocalDateTime.now())

    @get:Basic
    @get:Column(name = "USER_UPDATED_AT")
    var userUpdatedAt: Timestamp = Timestamp.valueOf(LocalDateTime.now())

    @get:Basic
    @get:Column(name = "CREDITS")
    var credits: Int = 0

    @get:Basic
    @get:Column(name = "CREDITS_UPDATED_AT")
    var creditsUpdatedAt: Timestamp = Timestamp.valueOf(LocalDateTime.now())

    @get:Basic
    @get:Column(name = "INVITED_BY")
    var invitedBy: Int? = null

    @get:Basic
    @get:Column(name = "REASON")
    var reason: String? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as AkDeletedUserEntity?
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
