package com.icebreaker.be.controller

import com.icebreaker.be.service.auth.AuthService
import com.icebreaker.be.service.chat.ChatService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.stereotype.Controller


@Controller
class SocketController(val authService: AuthService,
                       val simpMessagingTemplate: SimpMessageSendingOperations,
                       val chatService: ChatService
) {

    val logger: Logger = LoggerFactory.getLogger(SocketController::class.java)

    @MessageMapping("/{chatId}")//complete path from client app/{chatId}
    @Throws(Exception::class)
    fun sendMessageByClient(principal: OAuth2Authentication, message: Message, @DestinationVariable chatId: Int) {
        val userOrFail = authService.getUserOrFail(principal)
        val chat = chatService.findChatOrFail(chatId)
        //sendMessage performs validation
        val chatLine = chatService.sendMessage(userOrFail, chatId, message.name)
        chat.users.forEach {
            simpMessagingTemplate.convertAndSendToUser(it.email, "/chat/$chatId", chatLine)
        }
    }
}

data class Message(val name: String)