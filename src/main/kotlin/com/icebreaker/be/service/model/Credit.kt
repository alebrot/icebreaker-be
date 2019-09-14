package com.icebreaker.be.service.model

import com.icebreaker.be.controller.user.dto.AdmobCreditDto
import com.icebreaker.be.controller.user.dto.CreditDto
import com.icebreaker.be.controller.user.dto.InviteCreditDto
import com.icebreaker.be.controller.user.dto.LastSeenCreditDto
import java.time.Duration
import java.time.LocalDateTime

data class AdmobCredit(
        val count: Int,
        val countMax: Int,
        val updatedAt: LocalDateTime,
        val rewardDuration: Duration,
        val reward: Int
)

data class InviteCredit(val rewardCredits: Int)
data class LastSeenCredit(val rewardCredits: Int,
                          val rewardDuration: Duration,
                          val creditsUpdatedAt: LocalDateTime
)


data class Credit(val credits: Int, val lastSeenCredit: LastSeenCredit, val inviteCredit: InviteCredit, val admobCredit: AdmobCredit)

fun Credit.toDto(): CreditDto {
    return CreditDto(this.credits, this.lastSeenCredit.toDto(), this.inviteCredit.toDto(), this.admobCredit.toDto())
}

fun AdmobCredit.toDto(): AdmobCreditDto {
    return AdmobCreditDto(this.count, this.countMax,
            this.updatedAt,
            this.rewardDuration,
            this.reward)
}

fun LastSeenCredit.toDto(): LastSeenCreditDto {
    return LastSeenCreditDto(this.rewardCredits, this.creditsUpdatedAt)
}

fun InviteCredit.toDto(): InviteCreditDto {
    return InviteCreditDto(this.rewardCredits)
}