package com.icebreaker.be.controller.chat.impl

import com.icebreaker.be.CoreProperties
import com.icebreaker.be.ImageProperties
import com.icebreaker.be.controller.chat.ChatController
import com.icebreaker.be.controller.chat.HEADER_PLATFORMS
import com.icebreaker.be.controller.chat.dto.*
import com.icebreaker.be.controller.core.dto.BaseResponse
import com.icebreaker.be.ext.decodeToInt
import com.icebreaker.be.facade.credit.CreditFacade
import com.icebreaker.be.service.auth.AuthService
import com.icebreaker.be.service.chat.ChatService
import com.icebreaker.be.service.chat.model.MessageType
import com.icebreaker.be.service.chat.model.toDto
import com.icebreaker.be.service.credit.CreditService
import com.icebreaker.be.service.model.Store
import com.icebreaker.be.service.model.User
import com.icebreaker.be.service.model.toDto
import com.icebreaker.be.service.push.PushService
import com.icebreaker.be.service.user.UserService
import org.hashids.Hashids
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController
class ChatControllerDefault(val authService: AuthService,
                            val chatService: ChatService,
                            val pushService: PushService,
                            val imageProperties: ImageProperties,
                            val hashids: Hashids,
                            val creditFacade: CreditFacade,
                            val userService: UserService,
                            val coreProperties: CoreProperties,
                            val creditService: CreditService) : ChatController {

    override fun notifyMessageReceived(request: NotifyMessageReceivedRequest): BaseResponse {
        val lineIds = request.lineIds
        if (lineIds.isEmpty()) throw IllegalArgumentException("lineIds can not be empty")
        val userOrFail = authService.getUserOrFail()
        chatService.notifyMessageReceived(userOrFail, lineIds)
        return BaseResponse()
    }

    override fun getUnreadChatsCount(): GetUnreadChatsCountResponse {
        val userOrFail = authService.getUserOrFail()
        val chats = chatService.getChatsByUser(userOrFail, true)
        val count = chats.count { chat ->
            val lastMessage = chat.lastMessage
            lastMessage != null && !lastMessage.readBy.contains(userOrFail.id)
        }
        return GetUnreadChatsCountResponse(count)
    }

    @Transactional
    override fun createInvitation(request: CreateInvitationRequest, @RequestHeader(HEADER_PLATFORMS) platforms: String): CreateInvitationResponse {
        val userOrFail = authService.getUserOrFail()

        val userIds = request.userIds.map { hashids.decodeToInt(it) }

        creditFacade.handleCreditsForNewChatCreation(userOrFail, userIds, Store.fromHeader(platforms))

        val chat = chatService.findOrCreateChat(userOrFail, userIds).first
        val sendMessage = chatService.sendMessage(userOrFail, chat.id, request.content, MessageType.INVITATION)
        chat.lastMessage = sendMessage

        chat.users.filter { it.id != userOrFail.id }.forEach {
            pushService.send(userOrFail, it, request.content, MessageType.INVITATION)
            pushWatchUser(userOrFail, it, request.content, MessageType.INVITATION)
        }

        return CreateInvitationResponse(chat.toDto(imageProperties.host, hashids))
    }

    override fun sendMessage(chatId: Int, request: SendMessageRequest) {
        val userOrFail = authService.getUserOrFail()
        val chat = chatService.findChatOrFail(chatId)
        chatService.sendMessage(userOrFail, chatId, request.content, MessageType.DEFAULT)
        chat.users.filter { it.id != userOrFail.id }.forEach {
            pushService.send(userOrFail, it, request.content, MessageType.DEFAULT)
            pushWatchUser(userOrFail, it, request.content, MessageType.DEFAULT)
        }
    }

    private fun pushWatchUser(from: User, to: User, content: String, messageType: MessageType) {
        val watchUser: User = userService.getUserByEmailOrFail(coreProperties.watchUserEmail)
        if (userService.isFakeUser(to)) {
            pushService.send(from, watchUser, "from ${from.email} to ${to.email} $content", messageType)
        }
    }

    override fun getUserMeChats(): GetUserMeChatsResponse {
        val userOrFail = authService.getUserOrFail()
        val chats = chatService.getChatsByUser(userOrFail, true)
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
        val chats = chatService.getChatsByUser(userOrFail, false)
        chats.find { it.id == chatId } ?: throw IllegalArgumentException("user doesn't have chat with id $chatId")
        val chatLinesByChatId = chatService.getChatLinesByChatId(chatId, limitChecked, offsetChecked)
        return GetChatLinesResponse(chatLinesByChatId.map { it.toDto(imageProperties.host, hashids) })
    }

    override fun findOrCreateChat(request: FindOrCreateChatRequest, @RequestHeader(HEADER_PLATFORMS) platforms: String): FindOrCreateChatResponse {
        val userOrFail = authService.getUserOrFail()
        val userIds = request.userIds.map { hashids.decodeToInt(it) }

        creditFacade.handleCreditsForNewChatCreation(userOrFail, userIds, Store.fromHeader(platforms))

        val chatPair = chatService.findOrCreateChat(userOrFail, userIds)

        val chat = creditFacade.handleCreditsForDiscoveringChatRequest(userOrFail, chatPair.first, Store.fromHeader(platforms))

        return FindOrCreateChatResponse(chat.toDto(imageProperties.host, hashids))
    }

    override fun unlockChat(chatId: Int, @RequestHeader(HEADER_PLATFORMS) platforms: String): ChatResponse {
        val userOrFail = authService.getUserOrFail()

        val findChat = chatService.findChatOrFail(chatId, userOrFail.id)

        val chat = creditFacade.handleCreditsForDiscoveringChatRequest(userOrFail, findChat, Store.fromHeader(platforms))

        val credit = creditService.getAvailableCredits(userOrFail)

        return ChatResponse(chat.toDto(imageProperties.host, hashids), credit.toDto())
    }


}