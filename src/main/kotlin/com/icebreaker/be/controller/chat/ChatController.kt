package com.icebreaker.be.controller.chat

import com.icebreaker.be.controller.chat.dto.GetChatLinesResponse
import com.icebreaker.be.controller.chat.dto.GetUserMeChatsResponse
import com.icebreaker.be.controller.chat.dto.SendMessageRequest
import org.springframework.web.bind.annotation.*

const val GET_USER_ME_CHATS = "/users/me/chats"
const val GET_CHAT_LINES = "/chats/{chatId}/lines"
const val POST_CHAT_LINES = "/chats/{chatId}/lines"

interface ChatController {

    @GetMapping(GET_USER_ME_CHATS)
    fun getUserMeChats(): GetUserMeChatsResponse

    @GetMapping(GET_CHAT_LINES)
    fun getChatLines(@PathVariable chatId: Int, @RequestParam("limit") limit: Int?, @RequestParam("offset") offset: Int?): GetChatLinesResponse

    @PostMapping(POST_CHAT_LINES)
    fun sendMessage(@PathVariable chatId: Int, @RequestBody request: SendMessageRequest)

}