package com.icebreaker.be.controller.admin

import com.icebreaker.be.controller.core.dto.BaseResponse
import java.time.LocalDate


data class UserByAvailablePointDto(val userCount: Int, val points: Int)
data class UserByDateDto(val userCount: Int, val date: LocalDate)
data class CountUsersByAvailablePointsResponse(val list: List<UserByAvailablePointDto>) : BaseResponse()
data class CountOnlineUsersByDateResponse(val list: List<UserByDateDto>) : BaseResponse()