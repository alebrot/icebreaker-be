package com.icebreaker.be.service.push

import com.fasterxml.jackson.annotation.JsonProperty
import com.icebreaker.be.PushProperties
import com.icebreaker.be.db.entity.AkPushEntity
import com.icebreaker.be.db.repository.PushRepository
import com.icebreaker.be.db.repository.UserRepository
import com.icebreaker.be.ext.toKotlinNotOptionalOrFail
import com.icebreaker.be.service.chat.model.MessageType
import com.icebreaker.be.service.model.User
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate
import java.text.MessageFormat


interface PushService {
    fun subscribe(user: User, id: String, pushToken: String)

    fun send(fromUser: User, toUser: User, message: String, type: MessageType = MessageType.DEFAULT): Boolean
}

@Service
class NotificationServiceDefault(val userRepository: UserRepository,
                                 val pushRepository: PushRepository,
                                 val pushProperties: PushProperties,
                                 val restTemplate: RestTemplate) : PushService {

    val logger: Logger = LoggerFactory.getLogger(NotificationServiceDefault::class.java)


    override fun send(fromUser: User, toUser: User, message: String, type: MessageType): Boolean {

        val userEntity = userRepository.findById(toUser.id).toKotlinNotOptionalOrFail()
        val userId = userEntity.push?.userId

        if (userId != null) {

            val appId = pushProperties.appId

            data class Contents(val en: String,
                                val it: String)

            val headings = Contents(pushProperties.enInvitationTitle, pushProperties.itInvitationTitle)

            val contents = if (message.isBlank() && type == MessageType.INVITATION) {
                val contentEn = pushProperties.enInvitationContent
                val contentEnFormatted = MessageFormat.format(contentEn, fromUser.firstName)
                val contentIt = pushProperties.itInvitationContent
                val contentItFormatted = MessageFormat.format(contentIt, fromUser.firstName)

                Contents(contentEnFormatted, contentItFormatted)
            } else {
                Contents(message, message)
            }

            data class Data(val fee: String, val foo: String)

            class Body(@get:JsonProperty("app_id") val appId: String,
                       @get:JsonProperty("include_player_ids") val includePlayerIds: List<String>,
                       val data: Data,
                       val contents: Contents,
                       val headings: Contents)


            val body = Body(appId, listOf(userId), Data("fee", "foo"), contents, headings)

//            val restTemplate = RestTemplate()
//            restTemplate.interceptors.add(LoggingRequestInterceptor())

            val httpHeaders = HttpHeaders()
            httpHeaders.set("Authorization", pushProperties.secret)
            httpHeaders.set("Content-Type", "application/json; charset=UTF-8")

            val url = "https://onesignal.com/api/v1/notifications"

            val response: ResponseEntity<String>
            try {
                response = restTemplate.postForEntity(url, HttpEntity(body, httpHeaders), String::class.java)
                if (response.statusCode == HttpStatus.OK) {
                    return true
                } else {
                    logger.error("failed to send notification to user ${toUser.id}")
                }
            } catch (e: Exception) {
                logger.error("failed to send notification to user ${toUser.id}, Exception: ${e.localizedMessage}")
            }

        } else {
            logger.info("failed to send notification to user ${toUser.id} not subscribed")
        }
        return false
    }


    @Transactional
    override fun subscribe(user: User, id: String, pushToken: String) {

        val userEntity = userRepository.findById(user.id).toKotlinNotOptionalOrFail()
        var push = userEntity.push
        if (push != null) {
            push.userId = id
            push.pushToken = pushToken
            pushRepository.save(push)
        } else {
            push = AkPushEntity()
            push.userId = id
            push.pushToken = pushToken
            pushRepository.save(push)

            userEntity.push = push
            userRepository.save(userEntity)
        }
    }


}