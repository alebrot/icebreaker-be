package com.icebreaker.be.service.chat

import com.icebreaker.be.service.chat.model.Chat
import com.icebreaker.be.service.chat.model.ChatLine
import com.icebreaker.be.service.model.User

interface ChatService {
    fun getChatsByUser(user: User): List<Chat>
    fun getChatLinesByChatId(chatId: Int, limit: Int, offset: Int): List<ChatLine>
}
