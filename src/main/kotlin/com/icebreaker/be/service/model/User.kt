package com.icebreaker.be.service.model

import com.icebreaker.be.controller.user.GET_IMAGE_PATH
import com.icebreaker.be.controller.user.dto.UserDto
import com.icebreaker.be.db.entity.AkUserEntity
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.io.Serializable
import java.time.LocalDate

data class User(val id: Int,
                val email: String,
                val passwordHash: String?,
                val firstName: String,
                val lastName: String,
                var imgUrl: String?,
                val authorities: List<Authority>,
                val accountExpired: Boolean,
                val accountLocked: Boolean,
                val credentialsExpired: Boolean,
                val birthday: LocalDate,
                var bio: String?,
                var gender: Gender?,
                val enabled: Boolean) : Serializable {
    companion object

    val fullName: String = "$firstName $lastName"
}

data class UserWithDistance(val distance: Int, val user: User)

fun User.toDto(imageHost: String): UserDto {
    val url = this.imgUrl
    val image = if (url != null) {
        ServletUriComponentsBuilder.fromHttpUrl(imageHost)
                .path(GET_IMAGE_PATH)
                .path(url)
                .toUriString()
    } else {
        null
    }

    return UserDto(this.id, this.firstName, this.lastName, image, this.birthday, this.bio, this.gender)
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
            userEntity.birthday.toLocalDate(),
            userEntity.bio,
            userEntity.gender,
            userEntity.enabled
    )
}

enum class Gender {
    FEMALE,
    MALE
}