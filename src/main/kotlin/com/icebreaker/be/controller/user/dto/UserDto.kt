package com.icebreaker.be.controller.user.dto

import com.icebreaker.be.service.model.Gender
import java.time.LocalDate

data class UserDto(val id: Int,
                   val firstName: String,
                   val lastName: String,
                   val imageUrl: String?,
                   val birthday: LocalDate,
                   val bio: String?,
                   val gender: Gender?)

data class CompleteUserDto(val user: UserDto, val authorities: List<AuthorityDto>, val images: List<String>)