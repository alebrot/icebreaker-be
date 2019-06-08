package com.icebreaker.be.controller.user.dto

import com.icebreaker.be.controller.core.dto.BaseResponse

data class GetUserByIdResponse(val user: CompleteUserDto) : BaseResponse()
