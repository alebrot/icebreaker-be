package com.icebreaker.be.service.credit

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
//import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.androidpublisher.AndroidPublisher
import com.google.api.services.androidpublisher.AndroidPublisherScopes
import com.google.api.services.androidpublisher.model.ProductPurchase
import com.icebreaker.be.CoreProperties
import com.icebreaker.be.db.entity.AkCreditLogEntity
import com.icebreaker.be.db.entity.AkUserEntity
import com.icebreaker.be.db.repository.CreditLogRepository
import com.icebreaker.be.db.repository.ProductRepository
import com.icebreaker.be.db.repository.UserRepository
import com.icebreaker.be.ext.toKotlinNotOptionalOrFail
import com.icebreaker.be.service.model.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.FileInputStream
import java.sql.Timestamp
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import kotlin.math.max
import com.google.api.client.http.javanet.NetHttpTransport
import com.sun.javafx.application.PlatformImpl.setApplicationName



interface CreditService {
    fun getAvailableCredits(user: User): Credit
    fun addCredits(credits: Int, user: User, store: Store, payload: String): Credit
    fun rewardCredits(user: User): Credit
    fun rewardAdmobCredits(user: User): Credit
    fun rewardCreditsForInvitedPerson(user: User, invitedByUser: User): Credit
    fun getProducts(store: Store): List<Product>
    fun removeCreditsForChatCreation(credits: Int, user: User, store: Store): Credit
    fun removeCreditsForChatDiscovery(credits: Int, user: User, store: Store): Credit
}

@Service
class CreditServiceDefault(val userRepository: UserRepository,
                           val coreProperties: CoreProperties,
                           val productRepository: ProductRepository,
                           val creditLogRepository: CreditLogRepository,
                           val objectMapper: ObjectMapper) : CreditService {

    val log: Logger = LoggerFactory.getLogger(CreditServiceDefault::class.java)


    override fun getProducts(store: Store): List<Product> {
        return productRepository.findAllByStoreOrderById(store).map { Product.fromEntity(it) }
    }

    override fun getAvailableCredits(user: User): Credit {
        val userEntity = userRepository.findById(user.id).toKotlinNotOptionalOrFail()
        return Credit(userEntity.credits, getLastSeenCredit(userEntity), getInviteCredit(), getAdmobCredit(userEntity))
    }


    @Transactional
    override fun removeCreditsForChatCreation(credits: Int, user: User, store: Store): Credit {
        val userEntity = removeCredits(user, credits)
        logCredits(userEntity, credits, CreditType.CREATE_CHAT, CreditOperation.REMOVE, store, "removes $credits credits for chat creation", null)
        return Credit(userEntity.credits, getLastSeenCredit(userEntity), getInviteCredit(), getAdmobCredit(userEntity))
    }

    private fun removeCredits(user: User, credits: Int): AkUserEntity {
        val userEntity = userRepository.findById(user.id).toKotlinNotOptionalOrFail()
        val result = userEntity.credits - credits
        userEntity.credits = max(0, result)
        return userEntity
    }

    @Transactional
    override fun removeCreditsForChatDiscovery(credits: Int, user: User, store: Store): Credit {
        val userEntity = removeCredits(user, credits)
        logCredits(userEntity, credits, CreditType.DISCOVER_CHAT, CreditOperation.REMOVE, store, "removes $credits credits for chat discovery", null)
        return Credit(userEntity.credits, getLastSeenCredit(userEntity), getInviteCredit(), getAdmobCredit(userEntity))
    }

    @Transactional
    override fun addCredits(credits: Int, user: User, store: Store, payload: String): Credit {
        if (credits < 0) {
            throw IllegalArgumentException("credits $credits must be positive number")
        }
        val userEntity = userRepository.findById(user.id).toKotlinNotOptionalOrFail()
        userEntity.credits = userEntity.credits + credits

        logCredits(userEntity, credits, CreditType.PURCHASE, CreditOperation.ADD, store, "bought $credits credits", payload)

        return Credit(userEntity.credits, getLastSeenCredit(userEntity), getInviteCredit(), getAdmobCredit(userEntity))
    }

    @Transactional
    fun purchaseAndroid(user: User, productId: String, userPurchaseToken: String): Credit {
        val amount = 10//get from product
        val applicationName: String = "testing app name"
        val packageName: String = "package.name.test"
        val serviceAccountKeyFilePath = "<service-account-key-file>.json"

        try {
            val credentials: GoogleCredential = GoogleCredential
                    .fromStream(FileInputStream(serviceAccountKeyFilePath))
                    .createScoped(Collections.singleton(AndroidPublisherScopes.ANDROIDPUBLISHER));

//            val credential = GoogleCredential().setAccessToken(accessToken)
//            val plus = Plus.builder(NetHttpTransport(),
//                    JacksonFactory.getDefaultInstance(),
//                    credential)
//                    .setApplicationName("Google-PlusSample/1.0")
//                    .build()

            val pub: AndroidPublisher = AndroidPublisher.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), credentials)
                    .setApplicationName(applicationName)
                    .build();
            val get: AndroidPublisher.Purchases.Products.Get =
                    pub.purchases()
                            .products()
                            .get(packageName, productId, userPurchaseToken);
            val purchase: ProductPurchase = get.execute();
            log.info("Found google purchase item {}", purchase.toPrettyString())
            //https://developers.google.com/android-publisher/api-ref/purchases/products?authuser=1

            val payload = Pair(productId, userPurchaseToken)
            val writeValueAsString = objectMapper.writeValueAsString(payload)
            return addCredits(amount, user, Store.ANDROID, writeValueAsString)
        } catch (exc: Exception) {
            throw IllegalArgumentException("purchase failed")
        }

//        This will give the relevant purchase and throw error if the purchase is not found.
//        Verify the purchase with the corresponding purchase on the client side.
//        Also save it in a database for future validations and checks.
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

            logCredits(userEntity, admobRewardAmount, CreditType.ADMOB, CreditOperation.ADD, null, "rewarded $admobRewardAmount credits", null)

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
            logCredits(userEntity, rewardAmount, CreditType.LAST_SEEN, CreditOperation.ADD, null, "rewarded $rewardAmount credits", null)
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

            logCredits(userEntity, rewardAmountForInvitation, CreditType.INVITED_BY, CreditOperation.ADD, null, "was invited by ${userEntityInvitedBy.id} and rewarded $rewardAmountForInvitation credits", null)
            logCredits(userEntityInvitedBy, rewardAmountForInvitation, CreditType.INVITE, CreditOperation.ADD, null, "invited user ${userEntity.id} and rewarded $rewardAmountForInvitation credits", null)

            return Credit(userEntity.credits, getLastSeenCredit(userEntity), getInviteCredit(), getAdmobCredit(userEntity))

        } else {
            throw IllegalArgumentException("Not valid")
        }
    }

    private fun logCredits(userEntity: AkUserEntity, amount: Int, type: CreditType, operation: CreditOperation, store: Store?, description: String?, payload: String?) {
        val akCreditLogEntity = AkCreditLogEntity()
        akCreditLogEntity.creditType = type
        akCreditLogEntity.user = userEntity
        akCreditLogEntity.description = description
        akCreditLogEntity.creditOperation = operation
        akCreditLogEntity.amount = amount
        akCreditLogEntity.payload = payload
        akCreditLogEntity.store = store
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
