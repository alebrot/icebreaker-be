package com.icebreaker.be.controller.push

import com.icebreaker.be.controller.core.dto.BaseRequest


class SubscribeRequest(val id:String, val pushToken:String): BaseRequest()