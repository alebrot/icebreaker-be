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
class ChatServiceDefault(val userRepository: UserRepository,
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

    private fun assertUserBelongsToChat(userId: Int, chat: AkChatEntity) {
        if (!chat.users.map(AkUserEntity::id).contains(userId)) {
            throw IllegalArgumentException("User ${userId} doesn't belong to chat ${chat.id}")
        }
    }

    @Transactional
    override fun findChat(chatId: Int): Chat? {
        val chatEntity = chatRepository.findById(chatId).toKotlinOptional()
        if (chatEntity != null) {
            return Chat.fromEntity(chatEntity, null, null)
        }
        return null
    }

    @Transactional
    override fun findChatOrFail(chatId: Int): Chat {
        val chatEntity = chatRepository.findById(chatId).toKotlinNotOptionalOrFail()
        return Chat.fromEntity(chatEntity, null, null)
    }

    @Transactional
    override fun findChatOrFail(chatId: Int, userId: Int): Chat {
        val chatEntity = chatRepository.findById(chatId).toKotlinNotOptionalOrFail()
        assertUserBelongsToChat(userId, chatEntity)

        val lastLine = chatLineRepository.findByChatId(chatId, 1, 0).firstOrNull()

        val chatUserEntity = chatUserRepository.findByChatIdAndUserId(chatId, userId)
                ?: throw IllegalStateException("not found chat user entity by chat id $chatId user id $userId")

        val usersEntity = chatEntity.users.filter { akUserEntity -> akUserEntity.id != userId }
        val chatImageUrl = if (usersEntity.isNotEmpty()) {
            val imgUrl = usersEntity[0].imgUrl
            imgUrl
        } else {
            null
        }

        val chat = Chat.fromEntity(chatEntity, chatUserEntity.enabled, chatImageUrl, usersEntity)
        if (lastLine != null) {
            chat.lastMessage = ChatLine.fromEntity(lastLine, objectMapper)
        }
        return chat
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

    private fun findChat(user: User, userIds: List<Int>): Pair<AkChatEntity?, Set<Int>> {
        val ids = HashSet(userIds.toList())
        ids.add(user.id)
        if (ids.size == 1) {
            throw IllegalArgumentException("invalid input")
        }
        val toSortedSet = ids.toSortedSet()
        return Pair(chatRepository.findByUserIds(toSortedSet.joinToString(",")), ids)
    }

    override fun isNewChat(user: User, userIds: List<Int>): Boolean {
        return findChat(user, userIds).first == null
    }

    /**
     * returns true if created new chat
     */
    @Transactional
    override fun findOrCreateChat(user: User, userIds: List<Int>): Pair<Chat, Boolean> {

        val found = findChat(user, userIds).first
        if (found == null) {
            val akChatEntity = AkChatEntity()
            chatRepository.save(akChatEntity)

            val users = ArrayList<AkUserEntity>()
            findChat(user, userIds).second.forEach {
                val akUserEntity = userRepository.findById(it).toKotlinNotOptionalOrFail()
                users.add(akUserEntity)
                val akChatUserEntity = AkChatUserEntity()
                akChatUserEntity.user = akUserEntity
                akChatUserEntity.chat = akChatEntity
                akChatUserEntity.enabled = user.id == akUserEntity.id
                chatUserRepository.save(akChatUserEntity)
            }
            return Pair(Chat.fromEntity(akChatEntity, true, null, users.filter { akUserEntity -> akUserEntity.id != user.id }), true)

        } else {
            val chatUserEntity = chatUserRepository.findByChatIdAndUserId(found.id, user.id)
                    ?: throw IllegalStateException("not found chat user entity by chat id ${found.id} user id ${user.id}")

            val lastLine = chatLineRepository.findByChatId(found.id, 1, 0).firstOrNull()
            val usersWithoutMe = found.users.filter { akUserEntity -> akUserEntity.id != user.id }
            val chat = Chat.fromEntity(found, chatUserEntity.enabled, null, usersWithoutMe)
            if (lastLine != null) {
                chat.lastMessage = ChatLine.fromEntity(lastLine, objectMapper)
            }
            return Pair(chat, false)
        }
    }

    @Transactional
    override fun getChatsByUser(user: User, excludeEmptyChats: Boolean): List<Chat> {
        val userEntity = userRepository.findById(user.id).toKotlinNotOptionalOrFail()
        val chats = userEntity.chats
        return chats.sortedByDescending { akChatEntity -> akChatEntity.createdAt }.mapNotNull {
            val lastLine = chatLineRepository.findByChatId(it.id, 1, 0).firstOrNull()

            val chatUserEntity = chatUserRepository.findByChatIdAndUserId(it.id, user.id)
                    ?: throw IllegalStateException("not found chat user entity by chat id ${it.id} user id ${user.id}")

            val usersEntity = it.users.filter { akUserEntity -> akUserEntity.id != user.id }
            val chatImageUrl = if (usersEntity.isNotEmpty()) {
                val imgUrl = usersEntity[0].imgUrl
                imgUrl
            } else {
                null
            }

            val chat = Chat.fromEntity(it, chatUserEntity.enabled, chatImageUrl, usersEntity)
            if (lastLine != null) {
                chat.lastMessage = ChatLine.fromEntity(lastLine, objectMapper)
                chat
            } else {
                if (excludeEmptyChats) {
                    null
                } else {
                    chat
                }
            }
        }.sortedByDescending { chat ->
            chat.lastMessage?.createdAt
        }
    }


    override fun enableChat(chat: Chat, user: User): Chat {
        val chatUserEntity = chatUserRepository.findByChatIdAndUserId(chat.id, user.id)
                ?: throw IllegalStateException("not found chat user entity by chat id ${chat.id} user id ${user.id}")
        if (!chatUserEntity.enabled) {
            chatUserEntity.enabled = true
            chatUserRepository.save(chatUserEntity)
            chat.enabled = true
        }

        return chat
    }

    @Transactional
    override fun getChatLinesByChatId(chatId: Int, limit: Int, offset: Int): List<ChatLine> {
        val chatLines = chatLineRepository.findByChatId(chatId, limit, offset).sortedBy { akChatLineEntity -> akChatLineEntity.createdAt }
        return chatLines.map { ChatLine.fromEntity(it, objectMapper) }
    }


}