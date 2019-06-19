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

    @MessageMapping("/hello/{chatId}")
//    @SendToUser("/topic/orders/{chatId}")
    @Throws(Exception::class)
    fun greeting(principal: OAuth2Authentication, message: Message, @DestinationVariable chatId: Int) {
        val userOrFail = authService.getUserOrFail(principal)
        val chat = chatService.findChatOrFail(chatId)
        //sendMessage performs validation
        val chatLine = chatService.sendMessage(userOrFail, chatId, message.name)
        chat.users.forEach {
            simpMessagingTemplate.convertAndSendToUser(it.email, "/topic/orders/$chatId", chatLine)
        }

//        simpMessagingTemplate.convertAndSendToUser("email1@email.com", "/topic/orders/$chatId", "{\"content\":\"Hello, " + HtmlUtils.htmlEscape("email1@email.com") + "\"}")
//        simpMessagingTemplate.convertAndSendToUser("email2@email.com", "/topic/orders/$chatId", "{\"content\":\"Hello, " + HtmlUtils.htmlEscape("email2@email.com") + "\"}")
//        return "{\"content\":\"Hello111, " + HtmlUtils.htmlEscape(message.name) + "\"}"
    }
}

data class Message(val name: String)