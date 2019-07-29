package com.icebreaker.be.service.credit

import com.icebreaker.be.CoreProperties
import com.icebreaker.be.db.repository.UserRepository
import com.icebreaker.be.ext.toKotlinNotOptionalOrFail
import com.icebreaker.be.service.model.Credit
import com.icebreaker.be.service.model.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp
import java.time.Duration
import java.time.LocalDateTime
import kotlin.math.max

interface CreditService {
    fun getAvailableCredits(user: User): Credit
    fun removeCredits(credits: Int, user: User): Credit
    fun addCredits(credits: Int, user: User): Credit
    fun rewardCredits(user: User): Credit
}

@Service
class CreditServiceDefault(val userRepository: UserRepository, val coreProperties: CoreProperties) : CreditService {

    override fun getAvailableCredits(user: User): Credit {
        val userEntity = userRepository.findById(user.id).toKotlinNotOptionalOrFail()
        val rewardDuration = Duration.ofMinutes(coreProperties.rewardDuration.toLong())
        val rewardAmount = coreProperties.rewardAmount
        return Credit(userEntity.credits, userEntity.creditsUpdatedAt.toLocalDateTime(), rewardAmount, rewardDuration)
    }

    @Transactional
    override fun removeCredits(credits: Int, user: User): Credit {
        val userEntity = userRepository.findById(user.id).toKotlinNotOptionalOrFail()
        val rewardDuration = Duration.ofMinutes(coreProperties.rewardDuration.toLong())
        val rewardAmount = coreProperties.rewardAmount
        val result = userEntity.credits - credits
        userEntity.credits = max(0, result)
        return Credit(userEntity.credits, userEntity.creditsUpdatedAt.toLocalDateTime(), rewardAmount, rewardDuration)
    }

    @Transactional
    override fun addCredits(credits: Int, user: User): Credit {
        if (credits < 0) {
            throw IllegalArgumentException("credits $credits must be positive number")
        }
        val userEntity = userRepository.findById(user.id).toKotlinNotOptionalOrFail()

        val rewardDuration = Duration.ofMinutes(coreProperties.rewardDuration.toLong())
        val rewardAmount = coreProperties.rewardAmount

        userEntity.credits = userEntity.credits + credits
        return Credit(userEntity.credits, userEntity.creditsUpdatedAt.toLocalDateTime(), rewardAmount, rewardDuration)
    }

    @Transactional
    override fun rewardCredits(user: User): Credit {
        val rewardDuration = Duration.ofMinutes(coreProperties.rewardDuration.toLong())
        val rewardAmount = coreProperties.rewardAmount

        val userEntity = userRepository.findById(user.id).toKotlinNotOptionalOrFail()

        if (userEntity.credits < rewardAmount) {

            val toLocalDateTime = userEntity.creditsUpdatedAt.toLocalDateTime()
            val localDateTimeToGetReward = toLocalDateTime.plus(rewardDuration)

            if (localDateTimeToGetReward.isBefore(LocalDateTime.now())) {//get reward
                userEntity.credits = rewardAmount
                userEntity.creditsUpdatedAt = Timestamp.valueOf(LocalDateTime.now())
            }
        }
        return Credit(userEntity.credits, userEntity.creditsUpdatedAt.toLocalDateTime(), rewardAmount, rewardDuration)
    }

}