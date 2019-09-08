package com.icebreaker.be.service.model

import com.icebreaker.be.controller.user.dto.CreditDto
import java.time.Duration
import java.time.LocalDateTime

data class Credit(val credits: Int,
                  val creditsUpdatedAt: LocalDateTime,
                  val rewardCredits: Int,
                  val rewardPeriod: Duration,
                  val admobCount: Int,
                  val admobUpdatedAt: LocalDateTime,
                  val admobReward: Int
)

fun Credit.toDto(): CreditDto {
    return CreditDto(this.credits, this.creditsUpdatedAt, this.admobCount, this.admobUpdatedAt)
}