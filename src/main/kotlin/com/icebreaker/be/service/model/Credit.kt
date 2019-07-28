package com.icebreaker.be.service.model

import java.time.Duration
import java.time.LocalDateTime

data class Credit(val credits: Int, val creditsUpdatedAt: LocalDateTime, val rewardCredits: Int, val rewardPeriod: Duration)