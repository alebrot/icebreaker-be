package com.icebreaker.be.controller.user.dto

import com.icebreaker.be.service.model.Gender
import java.time.Duration
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
                   val credits: CreditDto?,
                   val invitedBy: String?)

data class AdmobCreditDto(val count: Int,
                          val countMax: Int,
                          val updatedAt: LocalDateTime,
                          val rewardDuration: Duration,
                          val reward: Int)

data class InviteCreditDto(val rewardCredits: Int)
data class LastSeenCreditDto(val rewardCredits: Int,
                             val rewardDuration: Duration,
                             val creditsUpdatedAt: LocalDateTime)

data class CreditDto(val credits: Int, val lastSeenCredit: LastSeenCreditDto, val inviteCredit: InviteCreditDto, val admobCredit: AdmobCreditDto)


data class CompleteUserDto(val user: UserDto, val authorities: List<AuthorityDto>, val images: List<String>)

data class CompleteUserDtoWithDistance(val user: UserDto, val authorities: List<AuthorityDto>, val images: List<String>, val distance: Int?)

data class ProductDto(val id: Int, val productId: String, val name: String, val description: String?)
