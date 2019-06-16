package com.icebreaker.be.controller.user.dto

data class UserDto(val id: Int, val firstName: String, val lastName: String, val imageUrl: String?)
data class CompleteUserDto(val user: UserDto, val authorities: List<AuthorityDto>, val images: List<String>)