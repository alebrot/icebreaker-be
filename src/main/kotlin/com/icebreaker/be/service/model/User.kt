package com.icebreaker.be.service.model

import com.icebreaker.be.controller.user.GET_IMAGE_PATH
import com.icebreaker.be.controller.user.dto.UserDto
import com.icebreaker.be.db.entity.AkUserEntity
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.io.Serializable

data class User(val id: Int,
                val email: String,
                val passwordHash: String?,
                val firstName: String,
                val lastName: String,
                val imgUrl: String?,
                val authorities: List<Authority>,
                val accountExpired: Boolean,
                val accountLocked: Boolean,
                val credentialsExpired: Boolean,
                val enabled: Boolean) : Serializable {
    companion object
}

data class UserWithDistance(val distance: Int, val user: User)

fun User.toDto(): UserDto {
    val image = if (this.imgUrl != null) {
        ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(GET_IMAGE_PATH)
                .path(this.imgUrl)
                .toUriString()
    } else {
        null
    }

    return UserDto(this.id, this.firstName, this.lastName, image)
}

fun User.Companion.fromEntity(userEntity: AkUserEntity): User {
    val authorities = userEntity.authorities.map { Authority(it.id, it.name) }
    return User(userEntity.id,
            userEntity.email,
            userEntity.passwordHash,
            userEntity.firstName,
            userEntity.lastName,
            userEntity.imgUrl,
            authorities,
            userEntity.accountExpired,
            userEntity.accountLocked,
            userEntity.credentialsExpired,
            userEntity.enabled
    )
}