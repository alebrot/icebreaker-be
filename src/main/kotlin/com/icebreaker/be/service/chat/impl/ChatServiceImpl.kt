package com.icebreaker.be.service.chat.impl

import com.ak.be.engine.ext.toKotlinNotOptionalOrFail
import com.icebreaker.be.db.repository.ChatLineRepository
import com.icebreaker.be.db.repository.ChatRepository
import com.icebreaker.be.db.repository.UserRepository
import com.icebreaker.be.service.chat.ChatService
import com.icebreaker.be.service.chat.model.Chat
import com.icebreaker.be.service.chat.model.ChatLine
import com.icebreaker.be.service.model.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChatServiceImpl(val userRepository: UserRepository,
                      val chatRepository: ChatRepository,
                      val chatLineRepository: ChatLineRepository) : ChatService {

    @Transactional
    override fun getChatsByUser(user: User): List<Chat> {
        val userEntity = userRepository.findById(user.id).toKotlinNotOptionalOrFail()
        val chats = userEntity.chats
        return chats.sortedByDescending { akChatEntity -> akChatEntity.createdAt }.map {
            Chat.fromEntity(it)
        }
    }

    @Transactional
    override fun getChatLinesByChatId(chatId: Int, limit: Int, offset: Int): List<ChatLine> {
        val chatLines = chatLineRepository.findByChatId(chatId, limit, offset)
        return chatLines.map { ChatLine.fromEntity(it) }
    }


}