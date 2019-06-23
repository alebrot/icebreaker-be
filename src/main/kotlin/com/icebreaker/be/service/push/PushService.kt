package com.icebreaker.be.service.push

import com.fasterxml.jackson.annotation.JsonProperty
import com.icebreaker.be.PushProperties
import com.icebreaker.be.db.entity.AkPushEntity
import com.icebreaker.be.db.repository.PushRepository
import com.icebreaker.be.db.repository.UserRepository
import com.icebreaker.be.ext.toKotlinNotOptionalOrFail
import com.icebreaker.be.service.model.User
import com.sun.org.apache.xpath.internal.operations.Bool
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate
import java.net.URI


interface PushService {
    fun subscribe(user: User, id: String, pushToken: String)

    fun send(user: User, message: String): Boolean
}

@Service
class NotificationServiceDefault(val userRepository: UserRepository,
                                 val pushRepository: PushRepository,
                                 val pushProperties: PushProperties) : PushService {

    val logger: Logger = LoggerFactory.getLogger(NotificationServiceDefault::class.java)


    override fun send(user: User, message: String): Boolean {

        val userEntity = userRepository.findById(user.id).toKotlinNotOptionalOrFail()
        val userId = userEntity.push?.id

        if (userId != null) {
            val appId = pushProperties.appId

            class Body(@get:JsonProperty("app_id") val appId: String,
                       @get:JsonProperty("include_player_ids") val includePlayerIds: List<String>,
                       val data: Any,
                       val contents: Map<String, String>)

            val restTemplate = RestTemplate()

            val httpHeaders = HttpHeaders()
            httpHeaders.set("Authorization", pushProperties.secret)
            httpHeaders.set("Content-Type", "application/json; charset=UTF-8")

            val body = ("{"
                    + "\"app_id\": \"" + appId + "\","
                    + "\"include_player_ids\": [\"" + userId + "\"],"
                    + "\"data\": {\"foo\": \"bar\"},"
                    + "\"contents\": {\"en\": \"" + message + "\"}"
                    + "}")

            val url = "https://onesignal.com/api/v1/notifications"

            val response: ResponseEntity<String>
            try {
                response = restTemplate.postForEntity(URI(url), HttpEntity(body, httpHeaders), String::class.java)
                if (response.statusCode == HttpStatus.OK) {
                    return true
                } else {
                    logger.error("failed to send notification to user ${user.id}")
                }
            } catch (e: Exception) {
                logger.error("failed to send notification to user ${user.id}")
            }

        } else {
            logger.error("failed to send notification to user ${user.id} not subscribed")
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
        send(user, "ddddd")

    }


}