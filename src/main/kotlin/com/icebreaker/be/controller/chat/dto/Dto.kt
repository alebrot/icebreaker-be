package com.icebreaker.be.controller.chat.dto

import com.icebreaker.be.controller.user.dto.UserDto
import java.time.LocalDateTime

data class GetUserMeChatsResponse(val chats: List<ChatDto>)
data class GetChatLinesResponse(val chatLines: List<ChatLineDto>)

data class ChatDto(val id: Int, val users: List<UserDto>)
data class ChatLineDto(val id: Int, val user: UserDto, val content: String, val createdAt: LocalDateTime)
