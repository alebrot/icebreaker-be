package com.icebreaker.be.controller

import com.icebreaker.be.service.auth.AuthService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.messaging.simp.annotation.SendToUser
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.stereotype.Controller
import org.springframework.web.util.HtmlUtils


@Controller
class SocketController(val authService: AuthService, val simpMessagingTemplate: SimpMessageSendingOperations) {

    val logger: Logger = LoggerFactory.getLogger(SocketController::class.java)

    @MessageMapping("/hello")
    @SendToUser("/topic/orders")
    @Throws(Exception::class)
    fun greeting(principal: OAuth2Authentication, message: String): String {
        val userOrFail = authService.getUserOrFail(principal)
        Thread.sleep(1000) // simulated delay
        simpMessagingTemplate.convertAndSendToUser("email1@email.com", "/topic/orders", "{\"content\":\"Hello, " + HtmlUtils.htmlEscape(principal.name) + "\"}")
        simpMessagingTemplate.convertAndSendToUser("email2@email.com", "/topic/orders", "{\"content\":\"Hello, " + HtmlUtils.htmlEscape(principal.name) + "\"}")
        return "{\"content\":\"Hello, " + HtmlUtils.htmlEscape(message) + "\"}"
    }
}