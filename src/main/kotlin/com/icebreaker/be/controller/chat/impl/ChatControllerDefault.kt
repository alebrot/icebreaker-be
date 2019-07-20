package com.icebreaker.be.controller.chat.impl

import com.icebreaker.be.ImageProperties
import com.icebreaker.be.controller.chat.ChatController
import com.icebreaker.be.controller.chat.dto.*
import com.icebreaker.be.controller.core.dto.BaseResponse
import com.icebreaker.be.ext.decodeToInt
import com.icebreaker.be.service.auth.AuthService
import com.icebreaker.be.service.chat.ChatService
import com.icebreaker.be.service.chat.model.MessageType
import com.icebreaker.be.service.chat.model.toDto
import com.icebreaker.be.service.push.PushService
import org.hashids.Hashids
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RestController

@RestController
class ChatControllerDefault(val authService: AuthService,
                            val chatService: ChatService,
                            val pushService: PushService,
                            val imageProperties: ImageProperties,
                            val hashids: Hashids) : ChatController {


    override fun notifyMessageReceived(request: NotifyMessageReceivedRequest): BaseResponse {
        val lineIds = request.lineIds
        if (lineIds.isEmpty()) throw IllegalArgumentException("lineIds can not be empty")
        val userOrFail = authService.getUserOrFail()
        chatService.notifyMessageReceived(userOrFail, lineIds)
        return BaseResponse()
    }

    @Transactional
    override fun createInvitation(request: CreateInvitationRequest): CreateInvitationResponse {
        val userOrFail = authService.getUserOrFail()
        val chat = chatService.findOrCreateChat(userOrFail, request.userIds)
        val sendMessage = chatService.sendMessage(userOrFail, chat.id, request.content, MessageType.INVITATION)
        chat.lastMessage = sendMessage
        chat.users.filter { it.id != userOrFail.id }.forEach { pushService.send(it, request.content, MessageType.INVITATION) }
        return CreateInvitationResponse(chat.toDto(imageProperties.host, hashids))
    }

    override fun sendMessage(chatId: Int, request: SendMessageRequest) {
        val userOrFail = authService.getUserOrFail()
        val chat = chatService.findChatOrFail(chatId)
        chatService.sendMessage(userOrFail, chatId, request.content, MessageType.DEFAULT)
        chat.users.filter { it.id != userOrFail.id }.forEach { pushService.send(it, request.content, MessageType.DEFAULT) }
    }

    override fun getUserMeChats(): GetUserMeChatsResponse {
        val userOrFail = authService.getUserOrFail()
        val chats = chatService.getChatsByUser(userOrFail)
        return GetUserMeChatsResponse(chats.map { it.toDto(imageProperties.host, hashids) })
    }

    override fun getChatLines(chatId: Int, limit: Int?, offset: Int?): GetChatLinesResponse {

        val defaultLimit = 100
        val limitChecked: Int = if ((limit ?: defaultLimit) !in 1..defaultLimit) {
            defaultLimit
        } else {
            (limit ?: defaultLimit)
        }

        val defaultOffset = 0
        val offsetChecked: Int = if ((offset ?: defaultOffset) !in 0..defaultOffset) {
            defaultOffset
        } else {
            (offset ?: defaultOffset)
        }

        val userOrFail = authService.getUserOrFail()
        val chats = chatService.getChatsByUser(userOrFail)
        chats.find { it.id == chatId } ?: throw IllegalArgumentException("user doesn't have chat with id $chatId")
        val chatLinesByChatId = chatService.getChatLinesByChatId(chatId, limitChecked, offsetChecked)
        return GetChatLinesResponse(chatLinesByChatId.map { it.toDto(imageProperties.host, hashids) })
    }

    override fun findOrCreateChat(request: FindOrCreateChatRequest): FindOrCreateChatResponse {
        val userOrFail = authService.getUserOrFail()
        val userIds = request.userIds.map { hashids.decodeToInt(it) }
        val chat = chatService.findOrCreateChat(userOrFail, userIds)
        return FindOrCreateChatResponse(chat.toDto(imageProperties.host, hashids))
    }
}