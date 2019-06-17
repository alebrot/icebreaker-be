package com.icebreaker.be.controller.chat.dto

import com.icebreaker.be.controller.user.dto.UserDto
import java.time.LocalDateTime

data class GetUserMeChatsResponse(val chats: List<ChatDto>)
data class GetChatLinesResponse(val lines: List<LineDto>)

data class ChatDto(val id: Int, val users: List<UserDto>)
data class LineDto(val id: Int, val user: UserDto, val content: String, val createdAt: LocalDateTime)
