package com.icebreaker.be.controller.push

import com.icebreaker.be.controller.core.dto.BaseResponse
import com.icebreaker.be.service.auth.AuthService
import com.icebreaker.be.service.push.PushService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

const val SUBSCRIBE = "/notifications"

interface PushNotificationController {
    @PostMapping(SUBSCRIBE)
    fun subscribe(@RequestBody subscribeRequest: SubscribeRequest): BaseResponse
}

@RestController
class OneSignalPushNotificationController(val authService: AuthService,
                                          val pushService: PushService) : PushNotificationController {
    override fun subscribe(subscribeRequest: SubscribeRequest): BaseResponse {
        val userOrFail = authService.getUserOrFail()
        pushService.subscribe(userOrFail, subscribeRequest.id, subscribeRequest.pushToken)
        return BaseResponse()
    }
}