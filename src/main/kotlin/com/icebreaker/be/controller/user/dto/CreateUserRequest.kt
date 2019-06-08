package com.icebreaker.be.controller.user.dto

import com.icebreaker.be.controller.core.dto.BaseRequest
import javax.validation.constraints.Email
import javax.validation.constraints.NotEmpty

data class CreateUserRequest(@Email val email: String,
                             @NotEmpty val firstName: String,
                             @NotEmpty val lastName: String,
                             @NotEmpty val password: String) : BaseRequest()
