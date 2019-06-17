package com.icebreaker.be.controller.chat.impl

import com.icebreaker.be.controller.chat.ChatController
import com.icebreaker.be.controller.chat.dto.GetChatLinesResponse
import com.icebreaker.be.controller.chat.dto.GetUserMeChatsResponse
import com.icebreaker.be.service.auth.AuthService
import com.icebreaker.be.service.chat.ChatService
import org.springframework.web.bind.annotation.RestController

@RestController
class ChatControllerDefault(val authService: AuthService, val chatService: ChatService) : ChatController {
    override fun getUserMeChats(): GetUserMeChatsResponse {

        val userOrFail = authService.getUserOrFail()

        val chats = chatService.getChatsByUser(userOrFail)
        return GetUserMeChatsResponse(ArrayList())
    }

    override fun getChatLines(chatId: Int, limit: Int?, offset: Int?): GetChatLinesResponse {
        TODO("not implemented")
    }

}