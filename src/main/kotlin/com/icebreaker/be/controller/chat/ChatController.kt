package com.icebreaker.be.controller.chat

import com.icebreaker.be.controller.chat.dto.*
import com.icebreaker.be.controller.core.dto.BaseResponse
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

const val GET_USER_ME_CHATS = "/users/me/chats"
const val GET_CHAT_LINES = "/chats/{chatId}/lines"
const val POST_CHAT_LINES = "/chats/{chatId}/lines"
const val FIND_OR_CREATE_CHAT = "/chats"
const val CREATE_INVITATION = "/invitations"
const val POST_NOTIFY_MESSAGE_RECEIVED = "/lines"

@Validated
interface ChatController {

    @GetMapping(GET_USER_ME_CHATS)
    fun getUserMeChats(): GetUserMeChatsResponse

    @GetMapping(GET_CHAT_LINES)
    fun getChatLines(@PathVariable chatId: Int, @RequestParam("limit") limit: Int?, @RequestParam("offset") offset: Int?): GetChatLinesResponse

    @PostMapping(FIND_OR_CREATE_CHAT)
    fun findOrCreateChat(@Valid @RequestBody request: FindOrCreateChatRequest): FindOrCreateChatResponse

    @PostMapping(CREATE_INVITATION)
    fun createInvitation(@Valid @RequestBody request: CreateInvitationRequest): CreateInvitationResponse

    @PostMapping(POST_CHAT_LINES)
    fun sendMessage(@PathVariable chatId: Int, @RequestBody request: SendMessageRequest)

    @PostMapping(POST_NOTIFY_MESSAGE_RECEIVED)
    fun notifyMessageReceived(@RequestBody request: NotifyMessageReceivedRequest): BaseResponse

}