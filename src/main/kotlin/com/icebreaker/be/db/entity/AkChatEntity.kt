package com.icebreaker.be.db.entity

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.sql.Timestamp
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "AK_CHAT", schema = "PUBLIC")
class AkChatEntity {

    @get:Id
    @get:GeneratedValue(strategy = GenerationType.IDENTITY)
    @get:Column(name = "ID")
    var id: Int = 0
    @get:Basic
    @get:Column(name = "TITLE")
    var title: String? = null

    @get:Basic
    @get:CreationTimestamp
    @get:Column(name = "CREATED_AT")
    var createdAt: Timestamp? = null

    @get:Basic
    @get:UpdateTimestamp
    @get:Column(name = "UPDATED_AT")
    var updatedAt: Timestamp? = null

    @get:ManyToMany(fetch = FetchType.LAZY)
    @get:JoinTable(name = "AK_CHAT_USER", joinColumns = [JoinColumn(name = "CHAT_ID", referencedColumnName = "ID")], inverseJoinColumns = [JoinColumn(name = "USER_ID", referencedColumnName = "ID")])
    var users: Collection<AkUserEntity> = ArrayList()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as AkChatEntity?
        return id == that!!.id && title == that.title
    }

    override fun hashCode(): Int {
        return Objects.hash(id, title)
    }
}
