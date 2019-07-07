package com.icebreaker.be.service.chat.model

import com.fasterxml.jackson.databind.ObjectMapper
import com.icebreaker.be.controller.chat.dto.ChatDto
import com.icebreaker.be.controller.chat.dto.ChatLineDto
import com.icebreaker.be.db.entity.AkChatEntity
import com.icebreaker.be.db.entity.AkChatLineEntity
import com.icebreaker.be.db.entity.AkUserEntity
import com.icebreaker.be.service.model.User
import com.icebreaker.be.service.model.fromEntity
import com.icebreaker.be.service.model.toDto
import java.time.LocalDateTime

data class Chat(val id: Int, val users: List<User>, var lastMessage: ChatLine? = null, val title: String? = null) {
    companion object {
        fun fromEntity(entity: AkChatEntity): Chat {
            val users = entity.users.map { u -> User.fromEntity(u) }
            val title: String = entity.title ?: users.joinToString(",") { it.fullName }
            return Chat(entity.id, users, null, title)
        }

        fun fromEntity(entity: AkChatEntity, usersEntity: List<AkUserEntity>): Chat {
            val users = usersEntity.map { u -> User.fromEntity(u) }
            val title: String = entity.title ?: users.joinToString(",") { it.fullName }
            return Chat(entity.id, users, null, title)
        }
    }
}


fun Chat.toDto(imageHost: String): ChatDto {
    val users = this.users.map { user -> user.toDto(imageHost) }
    return ChatDto(this.id, users, this.lastMessage?.toDto(imageHost), this.title)
}

data class ChatLine(val id: Int, val user: User, val content: String, val readBy: Set<Int>, val createdAt: LocalDateTime, val updatedAt: LocalDateTime, val type: MessageType = MessageType.DEFAULT) {
    companion object {
        fun fromEntity(entity: AkChatLineEntity, objectMapper: ObjectMapper): ChatLine {
            val user = entity.chatUser?.user ?: throw IllegalStateException("user not retrieved")
//            val chat = entity.chatUser?.chat ?: throw IllegalStateException("chat not retrieved")
            val content = entity.content ?: ""
            val type = entity.type
            val createdAt = entity.createdAt ?: throw IllegalStateException("createdAt is null")
            val updatedAt = entity.updatedAt ?: throw IllegalStateException("updatedAt is null")

            val readBy = entity.getReadBy(objectMapper)

            return ChatLine(entity.id, User.fromEntity(user), content, readBy, createdAt.toLocalDateTime(), updatedAt.toLocalDateTime(), type)
        }
    }
}

enum class MessageType {
    DEFAULT,
    INVITATION
}


fun ChatLine.toDto(imageHost: String): ChatLineDto {
    return ChatLineDto(this.id, this.user.toDto(imageHost), this.content, this.readBy, this.createdAt, this.type)
}