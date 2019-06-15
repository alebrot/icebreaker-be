package com.icebreaker.be.controller.user.impl

import com.icebreaker.be.controller.user.UserController
import com.icebreaker.be.controller.user.dto.*
import com.icebreaker.be.service.auth.AuthService
import com.icebreaker.be.service.model.User
import com.icebreaker.be.service.model.toDto
import com.icebreaker.be.user.UserService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
class UserControllerDefault(val authService: AuthService,
                            val userService: UserService) : UserController {


    override fun getUserMeUsers(distance: Int): GetUserMeUsersResponse {
        val userOrFail = authService.getUserOrFail()

        val usersCloseToUser = userService.getUsersCloseToUser(userOrFail, distance)
        val mapped = usersCloseToUser.map {
            UserWithDistanceDto(it.distance, it.user.toDto())
        }
        return GetUserMeUsersResponse(mapped)
    }

    @Transactional
    override fun createUser(@Valid @RequestBody request: CreateUserRequest): CreateUserResponse {
        val user: User = userService.createUser(
                request.email,
                request.password,
                request.firstName,
                request.lastName)

        return CreateUserResponse(user.toDto())
    }

    override fun getUserById(userId: Int): GetUserByIdResponse {
        val userOrFail = authService.getUserOrFail()
        val authorities = userOrFail.authorities.map { it.toDto() }
        val completeUserDto = CompleteUserDto(userOrFail.toDto(), authorities)
        return GetUserByIdResponse(completeUserDto)

    }

    @PreAuthorize("hasAuthority('USER')")
    override fun getUserMe(): GetUserMeResponse {
        val userOrFail = authService.getUserOrFail()
        val authorities = userOrFail.authorities.map { it.toDto() }
        val completeUserDto = CompleteUserDto(userOrFail.toDto(), authorities)
        return GetUserMeResponse(UserContextDto(completeUserDto))
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    override fun getAdminMe(): GetAdminMeResponse {
        val userOrFail = authService.getUserOrFail()
        val authorities = userOrFail.authorities.map { it.toDto() }
        val completeUserDto = CompleteUserDto(userOrFail.toDto(), authorities)
        return GetAdminMeResponse(AdminContextDto(completeUserDto))
    }
}