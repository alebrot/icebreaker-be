package com.icebreaker.be.controller.chat.dto

import com.icebreaker.be.controller.core.dto.BaseRequest
import com.icebreaker.be.controller.core.dto.BaseResponse
import com.icebreaker.be.controller.user.dto.UserDto
import java.time.LocalDateTime

data class GetUserMeChatsResponse(val chats: List<ChatDto>) : BaseResponse()
data class GetChatLinesResponse(val chatLines: List<ChatLineDto>) : BaseResponse()

data class ChatDto(val id: Int, val users: List<UserDto>, val lastMessage: ChatLineDto? = null)
data class ChatLineDto(val id: Int, val user: UserDto, val content: String, val createdAt: LocalDateTime)


data class SendMessageRequest(val content: String) : BaseResponse()
data class FindOrCreateChatRequest(val userIds: List<Int>) : BaseRequest()
data class FindOrCreateChatResponse(val chat: ChatDto) : BaseResponse()