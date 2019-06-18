package com.icebreaker.be.controller.chat

import com.icebreaker.be.controller.chat.dto.*
import org.springframework.web.bind.annotation.*

const val GET_USER_ME_CHATS = "/users/me/chats"
const val GET_CHAT_LINES = "/chats/{chatId}/lines"
const val POST_CHAT_LINES = "/chats/{chatId}/lines"
const val FIND_OR_CREATE_CHAT = "/chats"

interface ChatController {

    @GetMapping(GET_USER_ME_CHATS)
    fun getUserMeChats(): GetUserMeChatsResponse

    @GetMapping(GET_CHAT_LINES)
    fun getChatLines(@PathVariable chatId: Int, @RequestParam("limit") limit: Int?, @RequestParam("offset") offset: Int?): GetChatLinesResponse

    @PostMapping(FIND_OR_CREATE_CHAT)
    fun findOrCreateChat(@RequestBody request: FindOrCreateChatRequest): FindOrCreateChatResponse

    @PostMapping(POST_CHAT_LINES)
    fun sendMessage(@PathVariable chatId: Int, @RequestBody request: SendMessageRequest)


}