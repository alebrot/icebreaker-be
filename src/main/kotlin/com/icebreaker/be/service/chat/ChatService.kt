package com.icebreaker.be.service.chat

import com.icebreaker.be.service.chat.model.Chat
import com.icebreaker.be.service.chat.model.ChatLine
import com.icebreaker.be.service.chat.model.MessageType
import com.icebreaker.be.service.model.User

interface ChatService {
    fun getChatsByUser(user: User, excludeEmptyChats: Boolean): List<Chat>
    fun getChatLinesByChatId(chatId: Int, limit: Int, offset: Int): List<ChatLine>
    fun sendMessage(user: User, chatId: Int, content: String, type: MessageType): ChatLine
    fun findOrCreateChat(user: User, userIds: List<Int>): Pair<Chat, Boolean>
    fun findChat(chatId: Int): Chat?
    fun findChatOrFail(chatId: Int): Chat
    fun notifyMessageReceived(user: User, lineIds: List<Int>)
    fun isNewChat(user: User, userIds: List<Int>): Boolean
}
