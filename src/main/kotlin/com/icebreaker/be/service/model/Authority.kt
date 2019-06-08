package com.icebreaker.be.service.model

import com.icebreaker.be.controller.user.dto.AuthorityDto
import java.io.Serializable

data class Authority(val id: Int, val name: String) : Serializable

fun Authority.toDto(): AuthorityDto {
    return AuthorityDto(this.id, this.name)
}