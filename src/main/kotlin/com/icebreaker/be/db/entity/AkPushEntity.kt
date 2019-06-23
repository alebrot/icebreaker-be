package com.icebreaker.be.db.entity

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "AK_PUSH", schema = "PUBLIC", catalog = "DEFAULT")
class AkPushEntity {
    @get:Id
    @get:GeneratedValue(strategy = GenerationType.IDENTITY)
    @get:Column(name = "ID")
    var id: Int = 0

    @get:Basic
    @get:Column(name = "USER_ID")
    lateinit var userId: String

    @get:Basic
    @get:Column(name = "PUSH_TOKEN")
    lateinit var pushToken: String

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
        val that = other as AkPushEntity?
        return id == that!!.id &&
                userId == that.userId &&
                pushToken == that.pushToken
    }
}