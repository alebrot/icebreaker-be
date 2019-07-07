package com.icebreaker.be.service.chat.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.icebreaker.be.db.entity.AkChatEntity
import com.icebreaker.be.db.entity.AkChatLineEntity
import com.icebreaker.be.db.entity.AkChatUserEntity
import com.icebreaker.be.db.entity.AkUserEntity
import com.icebreaker.be.db.repository.ChatLineRepository
import com.icebreaker.be.db.repository.ChatRepository
import com.icebreaker.be.db.repository.ChatUserRepository
import com.icebreaker.be.db.repository.UserRepository
import com.icebreaker.be.ext.toKotlinNotOptionalOrFail
import com.icebreaker.be.ext.toKotlinOptional
import com.icebreaker.be.service.chat.ChatService
import com.icebreaker.be.service.chat.model.Chat
import com.icebreaker.be.service.chat.model.ChatLine
import com.icebreaker.be.service.chat.model.MessageType
import com.icebreaker.be.service.model.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChatServiceImpl(val userRepository: UserRepository,
                      val chatRepository: ChatRepository,
                      val chatLineRepository: ChatLineRepository,
                      val chatUserRepository: ChatUserRepository,
                      val objectMapper: ObjectMapper) : ChatService {


    @Transactional
    override fun notifyMessageReceived(user: User, lineIds: List<Int>) {
        val ids = HashSet(lineIds.toList())
        ids.forEach {
            val findById = chatLineRepository.findById(it)
            findById.ifPresent { chatLineEntity: AkChatLineEntity ->

                val akChatUserEntity = chatLineEntity.chatUser ?: throw IllegalStateException()
                val akChatEntity = akChatUserEntity.chat ?: throw IllegalStateException()
                if (!akChatEntity.users.map(AkUserEntity::id).contains(user.id)) {
                    throw IllegalArgumentException("User ${user.id} doesn't belong to chat ${akChatEntity.id}")
                }

                val readBy = chatLineEntity.getReadBy(objectMapper)
                val toMutableSet = readBy.toMutableSet()
                toMutableSet.add(user.id)
                chatLineEntity.setReadBy(objectMapper, toMutableSet)
                chatLineRepository.save(chatLineEntity)
            }
        }
    }

    @Transactional
    override fun findChat(chatId: Int): Chat? {
        val chatEntity = chatRepository.findById(chatId).toKotlinOptional()
        if (chatEntity != null) {
            return Chat.fromEntity(chatEntity)
        }
        return null
    }

    @Transactional
    override fun findChatOrFail(chatId: Int): Chat {
        val chatEntity = chatRepository.findById(chatId).toKotlinNotOptionalOrFail()
        return Chat.fromEntity(chatEntity)
    }

    @Transactional
    override fun sendMessage(user: User, chatId: Int, content: String, type: MessageType): ChatLine {
        val chatUserEntity = chatUserRepository.findByChatIdAndUserId(chatId, user.id)
                ?: throw IllegalArgumentException("chat not found by userId ${user.id} and chatId $chatId")
        val akChatLineEntity = AkChatLineEntity()
        akChatLineEntity.content = content
        akChatLineEntity.type = type
        akChatLineEntity.chatUser = chatUserEntity
        akChatLineEntity.setReadBy(objectMapper, setOf(user.id))
        chatLineRepository.save(akChatLineEntity)
        return ChatLine.fromEntity(akChatLineEntity, objectMapper)
    }

    @Transactional
    override fun findOrCreateChat(user: User, userIds: List<Int>): Chat {
        val ids = HashSet(userIds.toList())
        ids.add(user.id)

        if (ids.size == 1) {
            throw IllegalArgumentException("invalid input")
        }

        val toSortedSet = ids.toSortedSet()

        val found = chatRepository.findByUserIds(toSortedSet.joinToString(","))
        if (found == null) {

            val akChatEntity = AkChatEntity()
            chatRepository.save(akChatEntity)

            val users = ArrayList<AkUserEntity>()
            ids.forEach {
                val akUserEntity = userRepository.findById(it).toKotlinNotOptionalOrFail()
                users.add(akUserEntity)
                val akChatUserEntity = AkChatUserEntity()
                akChatUserEntity.user = akUserEntity
                akChatUserEntity.chat = akChatEntity
                chatUserRepository.save(akChatUserEntity)
            }
            return Chat.fromEntity(akChatEntity, users)

        } else {
            val lastLine = chatLineRepository.findByChatId(found.id, 1, 0).firstOrNull()
            val chat = Chat.fromEntity(found)
            if (lastLine != null) {
                chat.lastMessage = ChatLine.fromEntity(lastLine, objectMapper)
            }
            return chat
        }
    }

    @Transactional
    override fun getChatsByUser(user: User): List<Chat> {
        val userEntity = userRepository.findById(user.id).toKotlinNotOptionalOrFail()
        val chats = userEntity.chats
        return chats.sortedByDescending { akChatEntity -> akChatEntity.createdAt }.mapNotNull {
            val lastLine = chatLineRepository.findByChatId(it.id, 1, 0).firstOrNull()
            if (lastLine != null) {
                val chat = Chat.fromEntity(it, it.users.filter { akUserEntity -> akUserEntity.id != user.id })
                chat.lastMessage = ChatLine.fromEntity(lastLine, objectMapper)
                chat
            } else {
                null
            }
        }
    }

    @Transactional
    override fun getChatLinesByChatId(chatId: Int, limit: Int, offset: Int): List<ChatLine> {
        val chatLines = chatLineRepository.findByChatId(chatId, limit, offset).sortedBy { akChatLineEntity -> akChatLineEntity.createdAt }
        return chatLines.map { ChatLine.fromEntity(it, objectMapper) }
    }


}