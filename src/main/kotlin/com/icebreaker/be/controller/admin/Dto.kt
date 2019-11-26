package com.icebreaker.be.controller.admin

import com.icebreaker.be.controller.core.dto.BaseResponse


data class UserByAvailablePointDto(val userCount: Int, val points: Int)
data class CountUsersByAvailablePointsResponse(val list: List<UserByAvailablePointDto>) : BaseResponse()