package com.icebreaker.be.service.credit

//import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
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
import org.springframework.boot.configurationprocessor.json.JSONObject
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate
import java.io.FileInputStream
import java.sql.Timestamp
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import kotlin.math.max


interface CreditService {
    fun getAvailableCredits(user: User): Credit
    fun addCredits(credits: Int, user: User, store: Store, payload: String): Credit
    fun rewardCredits(user: User): Credit
    fun rewardAdmobCredits(user: User): Credit
    fun rewardCreditsForInvitedPerson(user: User, invitedByUser: User): Credit
    fun getProducts(store: Store): List<Product>
    fun removeCreditsForChatCreation(credits: Int, user: User, store: Store): Credit
    fun removeCreditsForChatDiscovery(credits: Int, user: User, store: Store): Credit
    fun purchaseAndroid(user: User, productId: String, userPurchaseToken: String): Credit
    fun purchaseIos(user: User, receiptData: String): Credit
}

@Service
class CreditServiceDefault(val userRepository: UserRepository,
                           val coreProperties: CoreProperties,
                           val productRepository: ProductRepository,
                           val creditLogRepository: CreditLogRepository,
                           val objectMapper: ObjectMapper,
                           val restTemplate: RestTemplate) : CreditService {

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
    override fun purchaseAndroid(user: User, productId: String, userPurchaseToken: String): Credit {
        val product = productRepository.findByProductId(productId)
                ?: throw IllegalArgumentException("Invalid product Id $productId")
        val amount = product.credits//get from product
        val applicationName: String = coreProperties.mobileAppName
        val packageName: String = coreProperties.mobileAppPackage
        val serviceAccountKeyFilePath = coreProperties.androidInAppPurchaseAccountFilePath//.json

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
            throw IllegalArgumentException("Android Purchase failed for user ${user.id}")
        }

//        This will give the relevant purchase and throw error if the purchase is not found.
//        Verify the purchase with the corresponding purchase on the client side.
//        Also save it in a database for future validations and checks.
    }

    //https://developer.apple.com/library/archive/releasenotes/General/ValidateAppStoreReceipt/Chapters/ValidateRemotely.html
    @Transactional
    override fun purchaseIos(user: User, receiptData: String): Credit {
        val password: String? = null//password - Only used for receipts that contain auto-renewable subscriptions. Your app’s shared secret (a hexadecimal string).
        val excludeOldTransactions = true
        val receiptDataEncoded = String(Base64.getEncoder().encode(receiptData.toByteArray()))

        class Body(@get:JsonProperty("receipt-data") val receiptDataEncoded: String,
                   @get:JsonProperty("password") val password: String?,
                   @get:JsonProperty("exclude-old-transactions") val excludeOldTransactions: Boolean)

        val body = Body(receiptDataEncoded, password, excludeOldTransactions)

        val httpHeaders = HttpHeaders()
        httpHeaders.set("Content-Type", "application/json; charset=UTF-8")

        val url = coreProperties.iosInAppPurchaseValidationUrl

        try {
            val response: ResponseEntity<String> = restTemplate.postForEntity(url, HttpEntity(body, httpHeaders), String::class.java)
            if (response.statusCode == HttpStatus.OK) {

                val jsonObject = JSONObject(response.body)
                val status = jsonObject.getInt("status")
                when (status) {
                    0 -> {
                        val receipt = jsonObject.getJSONObject("receipt")

                        val productId = receipt.getString("product_id")
                        val quantity = receipt.getInt("quantity")
                        val transactionId = receipt.getString("transaction_id")
                        val purchaseDate = receipt.getString("purchase_date")
                        val expiresDate = receipt.getString("expires_date")
                        val isTrialPeriod = receipt.getBoolean("is_trial_period")

                        log.info("IOS $status The valid $receipt. Active subscription for ${user.id}")

                        val product = productRepository.findByProductId(productId)
                                ?: throw IllegalArgumentException("Invalid product Id $productId")
                        val amount = product.credits//get from product

//                        data class Payload(val productId: String, val quantity: Int, val transactionId: String, val purchaseDate: String, val expiresDate: String, val isTrialPeriod: Boolean)
//                        val payload = Payload(productId, quantity, transactionId, purchaseDate, expiresDate, isTrialPeriod)

                        val writeValueAsString = objectMapper.writeValueAsString(Pair(receiptData, response.body))

                        return addCredits(amount, user, Store.IOS, writeValueAsString)
                    }
                    21000 -> {
                        log.warn("IOS $status The App Store could not read the JSON object you provided.")
                    }
                    21002 -> {
                        log.warn("IOS $status The data in the receipt-data property was malformed or missing.")
                    }
                    21003 -> {
                        log.warn("IOS $status The receipt could not be authenticated.")
                    }
                    21004 -> {
                        log.warn("IOS $status The shared secret you provided does not match the shared secret on file for your account.")
                    }
                    21005 -> {
                        log.warn("IOS $status The receipt server is not currently available.")
                    }
                    21006 -> {
                        log.warn("IOS $status This receipt is valid but the subscription has expired. When this status code is returned to your server, the receipt data is also decoded and returned as part of the response. Only returned for iOS 6 style transaction receipts for auto-renewable subscriptions.")
                    }
                    21007 -> {
                        log.warn("IOS $status This receipt is from the test environment, but it was sent to the production environment for verification. Send it to the test environment instead.")
                    }
                    21008 -> {
                        log.warn("IOS $status This receipt is from the production environment, but it was sent to the test environment for verification. Send it to the production environment instead.")
                    }
                    21010 -> {
                        log.warn("IOS $status This receipt could not be authorized. Treat this the same as if a purchase was never made.")
                    }
                    else -> {
                        log.warn("IOS $status : Internal data access error.")
                    }
                }

            } else {
                log.error("IOS failed to verify purchase for user ${user.id}")
            }
        } catch (e: Exception) {
            log.error("IOS failed to verify purchase for user ${user.id}")
        }
        throw IllegalArgumentException("Ios Purchase failed for user ${user.id}")
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
