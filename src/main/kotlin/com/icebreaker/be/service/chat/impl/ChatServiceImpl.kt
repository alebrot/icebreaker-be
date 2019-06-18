package com.icebreaker.be.service.chat.impl

import com.icebreaker.be.db.entity.AkChatEntity
import com.icebreaker.be.db.entity.AkChatLineEntity
import com.icebreaker.be.db.entity.AkChatUserEntity
import com.icebreaker.be.db.entity.AkUserEntity
import com.icebreaker.be.db.repository.ChatLineRepository
import com.icebreaker.be.db.repository.ChatRepository
import com.icebreaker.be.db.repository.ChatUserRepository
import com.icebreaker.be.db.repository.UserRepository
import com.icebreaker.be.ext.toKotlinNotOptionalOrFail
import com.icebreaker.be.service.chat.ChatService
import com.icebreaker.be.service.chat.model.Chat
import com.icebreaker.be.service.chat.model.ChatLine
import com.icebreaker.be.service.model.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChatServiceImpl(val userRepository: UserRepository,
                      val chatRepository: ChatRepository,
                      val chatLineRepository: ChatLineRepository,
                      val chatUserRepository: ChatUserRepository) : ChatService {

    @Transactional
    override fun sendMessage(user: User, chatId: Int, content: String) {
        val chatUserEntity = chatUserRepository.findByChatIdAndUserId(chatId, user.id)
                ?: throw IllegalArgumentException("chat not found not found by userId ${user.id} and chatId $chatId")
        val akChatLineEntity = AkChatLineEntity()
        akChatLineEntity.content = content
        akChatLineEntity.chatUser = chatUserEntity
        chatLineRepository.save(akChatLineEntity)
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
                chat.lastMessage = ChatLine.fromEntity(lastLine)
            }
            return chat
        }
    }

    @Transactional
    override fun getChatsByUser(user: User): List<Chat> {
        val userEntity = userRepository.findById(user.id).toKotlinNotOptionalOrFail()
        val chats = userEntity.chats
        return chats.sortedByDescending { akChatEntity -> akChatEntity.createdAt }.map {
            val lastLine = chatLineRepository.findByChatId(it.id, 1, 0).firstOrNull()
            val chat = Chat.fromEntity(it)
            if (lastLine != null) {
                chat.lastMessage = ChatLine.fromEntity(lastLine)
            }
            chat
        }
    }

    @Transactional
    override fun getChatLinesByChatId(chatId: Int, limit: Int, offset: Int): List<ChatLine> {
        val chatLines = chatLineRepository.findByChatId(chatId, limit, offset)
        return chatLines.map { ChatLine.fromEntity(it) }
    }


}