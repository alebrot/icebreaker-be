package com.icebreaker.be.service.chat.model

import com.icebreaker.be.service.model.User
import java.time.LocalDateTime

data class Chat(val id: Int, val users: List<User>)
data class ChatLine(val id: Int, val user: User, val content: String, val createdAt: LocalDateTime, val updatedAt: LocalDateTime)