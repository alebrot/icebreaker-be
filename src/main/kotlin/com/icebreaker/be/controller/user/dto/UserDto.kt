package com.icebreaker.be.controller.user.dto

import com.icebreaker.be.service.model.Gender
import java.time.LocalDate
import java.time.LocalDateTime

data class UserDto(val id: String,
                   val firstName: String,
                   val lastName: String,
                   val imageUrl: String?,
                   val birthday: LocalDate,
                   val lastSeen: LocalDateTime,
                   val createdAt: LocalDateTime,
                   val bio: String?,
                   val gender: Gender?,
                   val credits: CreditDto,
                   val invitedBy: String?)

data class CreditDto(val credits: Int, val creditsUpdatedAt: LocalDateTime, val admobCount: Int, val admobUpdatedAt: LocalDateTime)

data class CompleteUserDto(val user: UserDto, val authorities: List<AuthorityDto>, val images: List<String>)
data class CompleteUserDtoWithDistance(val user: UserDto, val authorities: List<AuthorityDto>, val images: List<String>, val distance: Int?)