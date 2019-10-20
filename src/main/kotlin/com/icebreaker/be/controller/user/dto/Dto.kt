package com.icebreaker.be.controller.user.dto

import com.icebreaker.be.controller.core.dto.BaseRequest
import com.icebreaker.be.controller.core.dto.BaseResponse
import com.icebreaker.be.service.model.Gender
import java.time.LocalDate
import javax.validation.constraints.Email
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class CreateUserRequest(@Email val email: String,
                             @NotEmpty val firstName: String,
                             @NotEmpty val lastName: String,
                             @NotEmpty val password: String,
                             @NotNull val birthday: LocalDate) : BaseRequest()

data class UpdateUserRequest(val bio: String?, val gender: Gender?, val imageIds: List<Int>?)


class CreateUserResponse(val user: UserDto) : BaseResponse()


data class CreditResponse(val creditDto: CreditDto) : BaseResponse()

data class CreditRequest(val transactionId: String?, val signature: String?, val receipt: String?)

data class GetProductsResponse(val products: List<ProductDto>) : BaseResponse()