package com.icebreaker.be.service.chat.model

import com.fasterxml.jackson.databind.ObjectMapper
import com.icebreaker.be.controller.chat.dto.ChatDto
import com.icebreaker.be.controller.chat.dto.ChatLineDto
import com.icebreaker.be.controller.user.GET_IMAGE_PATH
import com.icebreaker.be.controller.user.GET_IMAGE_PATH_BLURRED
import com.icebreaker.be.db.entity.AkChatEntity
import com.icebreaker.be.db.entity.AkChatLineEntity
import com.icebreaker.be.db.entity.AkUserEntity
import com.icebreaker.be.service.model.User
import com.icebreaker.be.service.model.fromEntity
import com.icebreaker.be.service.model.toDto
import org.hashids.Hashids
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.time.LocalDateTime

data class Chat(val id: Int, val users: List<User>, var enabled: Boolean?, val imageUrl: String?, var lastMessage: ChatLine? = null, val title: String? = null) {
    companion object {
        fun fromEntity(entity: AkChatEntity, enabled: Boolean?, imageUrl: String?): Chat {
            val users = entity.users.map { u -> User.fromEntity(u) }
            val title: String = entity.title ?: users.joinToString(", ") { it.firstName }
            return Chat(entity.id, users, enabled, imageUrl, null, title)
        }

        fun fromEntity(entity: AkChatEntity, enabled: Boolean?, imageUrl: String?, usersEntity: List<AkUserEntity>): Chat {
            val users = usersEntity.map { u -> User.fromEntity(u) }
            val title: String = entity.title ?: users.joinToString(", ") { it.firstName }
            return Chat(entity.id, users, enabled, imageUrl, null, title)
        }
    }
}


fun Chat.toDto(imageHost: String, hashids: Hashids): ChatDto {
    val users = this.users.map { user -> user.toDto(imageHost, hashids) }

    val image = if (this.imageUrl != null) {
        val getImagePath = if (this.enabled == true) GET_IMAGE_PATH else GET_IMAGE_PATH_BLURRED
        ServletUriComponentsBuilder.fromHttpUrl(imageHost)
                .path(getImagePath)
                .path(this.imageUrl)
                .toUriString()
    } else {
        null
    }

    return ChatDto(this.id, users, this.enabled
            ?: false, image, this.lastMessage?.toDto(imageHost, hashids), this.title)
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


fun ChatLine.toDto(imageHost: String, hashids: Hashids): ChatLineDto {
    return ChatLineDto(this.id, this.user.toDto(imageHost, hashids), this.content, this.readBy, this.createdAt, this.type)
}