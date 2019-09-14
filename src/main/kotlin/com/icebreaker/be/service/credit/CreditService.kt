package com.icebreaker.be.service.credit

import com.icebreaker.be.CoreProperties
import com.icebreaker.be.db.entity.AkCreditLogEntity
import com.icebreaker.be.db.entity.AkUserEntity
import com.icebreaker.be.db.repository.CreditLogRepository
import com.icebreaker.be.db.repository.ProductRepository
import com.icebreaker.be.db.repository.UserRepository
import com.icebreaker.be.ext.toKotlinNotOptionalOrFail
import com.icebreaker.be.service.model.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp
import java.time.Duration
import java.time.LocalDateTime
import kotlin.math.max

interface CreditService {
    fun getAvailableCredits(user: User): Credit
    fun addCredits(credits: Int, user: User): Credit
    fun rewardCredits(user: User): Credit
    fun rewardAdmobCredits(user: User): Credit
    fun rewardCreditsForInvitedPerson(user: User, invitedByUser: User): Credit
    fun getProducts(store: Store): List<Product>
    fun removeCreditsForChatCreation(credits: Int, user: User): Credit
    fun removeCreditsForChatDiscovery(credits: Int, user: User): Credit
}

@Service
class CreditServiceDefault(val userRepository: UserRepository,
                           val coreProperties: CoreProperties,
                           val productRepository: ProductRepository,
                           val creditLogRepository: CreditLogRepository) : CreditService {

    override fun getProducts(store: Store): List<Product> {
        return productRepository.findAllByStoreOrderById(store).map { Product.fromEntity(it) }
    }

    override fun getAvailableCredits(user: User): Credit {
        val userEntity = userRepository.findById(user.id).toKotlinNotOptionalOrFail()
        return Credit(userEntity.credits, getLastSeenCredit(userEntity), getInviteCredit(), getAdmobCredit(userEntity))
    }


    @Transactional
    override fun removeCreditsForChatCreation(credits: Int, user: User): Credit {
        val userEntity = removeCredits(user, credits)
        logCredits(userEntity, CreditType.CREATE_CHAT, "removes $credits credits for chat creation")
        return Credit(userEntity.credits, getLastSeenCredit(userEntity), getInviteCredit(), getAdmobCredit(userEntity))
    }

    private fun removeCredits(user: User, credits: Int): AkUserEntity {
        val userEntity = userRepository.findById(user.id).toKotlinNotOptionalOrFail()
        val result = userEntity.credits - credits
        userEntity.credits = max(0, result)
        return userEntity
    }

    @Transactional
    override fun removeCreditsForChatDiscovery(credits: Int, user: User): Credit {
        val userEntity = removeCredits(user, credits)
        logCredits(userEntity, CreditType.DISCOVER_CHAT, "removes $credits credits for chat discovery")
        return Credit(userEntity.credits, getLastSeenCredit(userEntity), getInviteCredit(), getAdmobCredit(userEntity))
    }

    @Transactional
    override fun addCredits(credits: Int, user: User): Credit {
        if (credits < 0) {
            throw IllegalArgumentException("credits $credits must be positive number")
        }
        val userEntity = userRepository.findById(user.id).toKotlinNotOptionalOrFail()
        userEntity.credits = userEntity.credits + credits

        logCredits(userEntity, CreditType.PURCHASE, "bought $credits credits")

        return Credit(userEntity.credits, getLastSeenCredit(userEntity), getInviteCredit(), getAdmobCredit(userEntity))
    }

    @Transactional
    override fun rewardAdmobCredits(user: User): Credit {
        val rewardDuration = Duration.ofMinutes(coreProperties.admobRewardDuration.toLong())
        val admobRewardAmount = coreProperties.admobRewardAmount
        val admobMax = coreProperties.admobMax
        val userEntity = userRepository.findById(user.id).toKotlinNotOptionalOrFail()

        val toLocalDateTime = userEntity.admobUpdatedAt.toLocalDateTime()
        val localDateTimeToGetReward = toLocalDateTime.plus(rewardDuration)
        if (userEntity.admobCount < admobMax || localDateTimeToGetReward.isBefore(LocalDateTime.now())) {//get reward
            userEntity.credits = userEntity.credits + admobRewardAmount
            userEntity.admobUpdatedAt = Timestamp.valueOf(LocalDateTime.now())

            if (localDateTimeToGetReward.isBefore(LocalDateTime.now())) {
                userEntity.admobCount = 0
            } else {
                userEntity.admobCount = userEntity.admobCount + 1
            }

            logCredits(userEntity, CreditType.ADMOB, "rewarded $admobRewardAmount credits")

        } else {
            throw IllegalArgumentException("Not allowed to admob reward, exceeds limit $admobMax")
        }
        return Credit(userEntity.credits, getLastSeenCredit(userEntity), getInviteCredit(), getAdmobCredit(userEntity))
    }

    @Transactional
    override fun rewardCredits(user: User): Credit {

        val rewardDuration = Duration.ofMinutes(coreProperties.rewardDuration.toLong())
        val rewardAmount = coreProperties.rewardAmount

        val userEntity = userRepository.findById(user.id).toKotlinNotOptionalOrFail()

        val toLocalDateTime = userEntity.creditsUpdatedAt.toLocalDateTime()
        val localDateTimeToGetReward = toLocalDateTime.plus(rewardDuration)

        if (localDateTimeToGetReward.isBefore(LocalDateTime.now())) {//get reward
            userEntity.credits = userEntity.credits + rewardAmount
            userEntity.creditsUpdatedAt = Timestamp.valueOf(LocalDateTime.now())
            logCredits(userEntity, CreditType.LAST_SEEN, "rewarded $rewardAmount credits")
        }

        val admobCredit = getAdmobCredit(userEntity)
        val lastSeenCredit = getLastSeenCredit(userEntity)
        val inviteCredit = getInviteCredit()

        return Credit(
                userEntity.credits,
                lastSeenCredit,
                inviteCredit,
                admobCredit)
    }

    @Transactional
    override fun rewardCreditsForInvitedPerson(user: User, invitedByUser: User): Credit {
        if (user.invitedBy == null && invitedByUser.id < user.id) {
            val userEntity = userRepository.findById(user.id).toKotlinNotOptionalOrFail()
            val userEntityInvitedBy = userRepository.findById(invitedByUser.id).toKotlinNotOptionalOrFail()
            val rewardAmountForInvitation = coreProperties.rewardAmountForInvitation

            userEntity.credits = userEntity.credits + rewardAmountForInvitation
            userEntityInvitedBy.credits = userEntity.credits + rewardAmountForInvitation

            logCredits(userEntity, CreditType.INVITE, "rewarded $rewardAmountForInvitation credits thanks to ${userEntityInvitedBy.id}")
            logCredits(userEntityInvitedBy, CreditType.INVITE, "rewarded $rewardAmountForInvitation credits for user ${userEntity.id} invitation")

            return Credit(userEntity.credits, getLastSeenCredit(userEntity), getInviteCredit(), getAdmobCredit(userEntity))

        } else {
            throw IllegalArgumentException("Not valid")
        }
    }

    private fun logCredits(userEntity: AkUserEntity, type: CreditType, description: String?) {
        val akCreditLogEntity = AkCreditLogEntity()
        akCreditLogEntity.creditType = type
        akCreditLogEntity.user = userEntity
        akCreditLogEntity.description = description
        creditLogRepository.save(akCreditLogEntity)
    }

    private fun getInviteCredit(): InviteCredit {
        val rewardAmountForInvitation = coreProperties.rewardAmountForInvitation
        return InviteCredit(rewardAmountForInvitation)
    }

    private fun getLastSeenCredit(userEntity: AkUserEntity): LastSeenCredit {
        val rewardDuration = Duration.ofMinutes(coreProperties.rewardDuration.toLong())
        val rewardAmount = coreProperties.rewardAmount
        return LastSeenCredit(rewardAmount, rewardDuration, userEntity.creditsUpdatedAt.toLocalDateTime())
    }

    private fun getAdmobCredit(userEntity: AkUserEntity): AdmobCredit {
        val admobRewardDuration = Duration.ofMinutes(coreProperties.admobRewardDuration.toLong())
        val admobRewardAmount = coreProperties.admobRewardAmount
        val admobMax = coreProperties.admobMax
        return AdmobCredit(userEntity.admobCount, admobMax, userEntity.admobUpdatedAt.toLocalDateTime(), admobRewardDuration, admobRewardAmount)
    }

}
