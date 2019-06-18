package com.icebreaker.be.service.chat.model

import com.icebreaker.be.controller.chat.dto.ChatDto
import com.icebreaker.be.controller.chat.dto.ChatLineDto
import com.icebreaker.be.db.entity.AkChatEntity
import com.icebreaker.be.db.entity.AkChatLineEntity
import com.icebreaker.be.service.model.User
import com.icebreaker.be.service.model.fromEntity
import com.icebreaker.be.service.model.toDto
import java.time.LocalDateTime

data class Chat(val id: Int, val users: List<User>) {
    companion object {
        fun fromEntity(entity: AkChatEntity): Chat {
            val users = entity.users.map { u -> User.fromEntity(u) }
            return Chat(entity.id, users)
        }
    }
}


fun Chat.toDto(): ChatDto {
    val users = this.users.map { user -> user.toDto() }
    return ChatDto(this.id, users)
}

data class ChatLine(val id: Int, val user: User, val content: String, val createdAt: LocalDateTime, val updatedAt: LocalDateTime) {
    companion object {
        fun fromEntity(entity: AkChatLineEntity): ChatLine {
            val user = entity.user ?: throw IllegalStateException("user not retrieved")
            val chat = entity.chat ?: throw IllegalStateException("chat not retrieved")
            val content = entity.content ?: ""
            val createdAt = entity.createdAt ?: throw IllegalStateException("createdAt is null")
            val updatedAt = entity.updatedAt ?: throw IllegalStateException("updatedAt is null")
            return ChatLine(entity.id, User.fromEntity(user), content, createdAt.toLocalDateTime(), updatedAt.toLocalDateTime())
        }
    }
}


fun ChatLine.toDto(): ChatLineDto {
    return ChatLineDto(this.id, this.user.toDto(), this.content, this.createdAt)
}