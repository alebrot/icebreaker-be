package com.icebreaker.be.service.chat.impl

import com.ak.be.engine.ext.toKotlinNotOptionalOrFail
import com.icebreaker.be.db.repository.ChatRepository
import com.icebreaker.be.db.repository.UserRepository
import com.icebreaker.be.service.chat.ChatService
import com.icebreaker.be.service.chat.model.Chat
import com.icebreaker.be.service.chat.model.ChatLine
import com.icebreaker.be.service.model.User
import com.icebreaker.be.service.model.fromEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChatServiceImpl(val userRepository: UserRepository,
                      val chatRepository: ChatRepository) : ChatService {

    @Transactional
    override fun getChatsByUser(user: User): List<Chat> {
        val userEntity = userRepository.findById(user.id).toKotlinNotOptionalOrFail()
        val chats = userEntity.chats
        val list = chats.map {

            val users = it.users.map { u -> User.fromEntity(u) }
            Chat(it.id, users)
        }
        return list

    }

    override fun getChatLinesByChatId(chatId: Int, limit: Int, offset: Int): List<ChatLine> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}