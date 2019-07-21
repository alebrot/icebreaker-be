package com.icebreaker.be.controller.user.dto

import com.icebreaker.be.controller.core.dto.BaseResponse
import java.math.BigDecimal

data class GetUserMeResponse(val context: UserContextDto) : BaseResponse()
data class GetUserMeUsersResponse(val users: List<UserWithDistanceDto>) : BaseResponse()
data class UserWithDistanceDto(val distance: Int, val user: UserDto): BaseResponse()
data class CreateUserPositionRequest(val latitude: BigDecimal, val longitude: BigDecimal): BaseResponse()
data class UploadUserImageResponse(val url: String): BaseResponse()