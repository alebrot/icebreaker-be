package com.icebreaker.be.controller.chat.dto

import com.icebreaker.be.controller.core.dto.BaseRequest
import com.icebreaker.be.controller.core.dto.BaseResponse
import com.icebreaker.be.controller.user.dto.UserDto
import com.icebreaker.be.service.chat.model.MessageType
import java.time.LocalDateTime
import javax.validation.constraints.NotNull

data class GetUserMeChatsResponse(val chats: List<ChatDto>) : BaseResponse()
data class GetChatLinesResponse(val chatLines: List<ChatLineDto>) : BaseResponse()

data class ChatDto(val id: Int, val users: List<UserDto>, val lastMessage: ChatLineDto? = null, val title: String?)
data class ChatLineDto(val id: Int, val user: UserDto, val content: String, val readBy: Set<Int>, val createdAt: LocalDateTime, val type: MessageType)

data class NotifyMessageReceivedRequest(@NotNull val lineIds: List<Int>) : BaseRequest()

data class SendMessageRequest(val content: String) : BaseResponse()
data class FindOrCreateChatRequest(@NotNull val userIds: List<String>) : BaseRequest()
data class FindOrCreateChatResponse(val chat: ChatDto) : BaseResponse()

data class CreateInvitationRequest(@NotNull val userIds: List<Int>, val content: String = "") : BaseRequest()
data class CreateInvitationResponse(val chat: ChatDto) : BaseResponse()