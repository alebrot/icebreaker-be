package com.icebreaker.be.service.model

import com.icebreaker.be.controller.user.dto.ImageDto

data class UserImage(val id: Int, val url: String)

fun UserImage.toDto(): ImageDto {
    return ImageDto(this.id, this.url)
}