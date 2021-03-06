package com.icebreaker.be.db.entity

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.icebreaker.be.service.chat.model.MessageType
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.sql.Timestamp
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "AK_CHAT_LINE", schema = "kofify")
class AkChatLineEntity {

    @get:Id
    @get:GeneratedValue(strategy = GenerationType.IDENTITY)
    @get:Column(name = "ID")
    var id: Int = 0

    @get:Basic
    @get:Column(name = "CONTENT")
    var content: String? = null

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
    @get:Column(name = "TYPE")
    lateinit var type: MessageType

    @get:Basic
    @get:Column(name = "READ_BY")
    var readBy: String = "[]"

//    @get:OneToOne(fetch = FetchType.LAZY)
//    @get:JoinColumn(name = "USER_ID", referencedColumnName = "ID", nullable = false)
//    var user: AkUserEntity? = null
//
//    @get:OneToOne(fetch = FetchType.LAZY)
//    @get:JoinColumn(name = "CHAT_ID", referencedColumnName = "ID", nullable = false)
//    var chat: AkChatEntity? = null

    @get:OneToOne(fetch = FetchType.LAZY)
    @get:JoinColumn(name = "CHAT_USER_ID", referencedColumnName = "ID", nullable = false)
    var chatUser: AkChatUserEntity? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as AkChatLineEntity?
        return id == that!!.id && content == that.content
    }

    override fun hashCode(): Int {
        return Objects.hash(id, content)
    }

    fun setReadBy(objectMapper: ObjectMapper, userIds: Set<Int>) {
        val toMutableSet = getReadBy(objectMapper).toMutableSet()
        toMutableSet.addAll(userIds)
        this.readBy = objectMapper.writeValueAsString(toMutableSet)
    }

    fun getReadBy(objectMapper: ObjectMapper): Set<Int> {
        return objectMapper.readValue(this.readBy, object : TypeReference<Set<Int>>() {})
    }

}
