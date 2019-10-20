package com.icebreaker.be.service.model

import com.icebreaker.be.controller.user.dto.*
import com.icebreaker.be.db.entity.AkProductEntity
import java.time.Duration
import java.time.LocalDateTime


data class Product(val id: Int, val productId: String, val name: String, val description: String?) {
    companion object
}

enum class Store {
    ANDROID,
    IOS;

    companion object {
        fun fromHeader(platforms: String): Store {
            return if (platforms.contains("android")) Store.ANDROID else Store.IOS
        }
    }
}

fun Product.toDto(): ProductDto {
    return ProductDto(this.id, this.productId, this.name, this.description)
}

fun Product.Companion.fromEntity(entity: AkProductEntity): Product {
    return Product(
            entity.id,
            entity.productId ?: throw IllegalStateException("product id can not be null"),
            entity.name ?: throw IllegalStateException("product name can not be null"),
            entity.description
    )
}

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

enum class CreditType {
    PURCHASE,
    INVITE,
    INVITED_BY,
    LAST_SEEN,
    ADMOB,
    DISCOVER_CHAT,
    CREATE_CHAT
}

enum class CreditOperation {
    ADD,
    REMOVE
}


fun AdmobCredit.toDto(): AdmobCreditDto {
    return AdmobCreditDto(this.count, this.countMax,
            this.updatedAt,
            this.rewardDuration,
            this.reward)
}

fun LastSeenCredit.toDto(): LastSeenCreditDto {
    return LastSeenCreditDto(this.rewardCredits, this.rewardDuration, this.creditsUpdatedAt)
}

fun InviteCredit.toDto(): InviteCreditDto {
    return InviteCreditDto(this.rewardCredits)
}