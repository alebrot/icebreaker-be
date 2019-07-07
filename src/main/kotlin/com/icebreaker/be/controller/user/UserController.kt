package com.icebreaker.be.controller.user

import com.icebreaker.be.controller.core.dto.BaseResponse
import com.icebreaker.be.controller.user.dto.*
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.math.BigDecimal
import javax.validation.Valid

const val GET_USER_BY_ID = "/users/{userId}"
const val POST_LOGOUT = "/users/me/logout"
const val GET_USER_ME = "/users/me"
const val POST_USER_ME = "/users/me"
const val UPLOAD_USER_IMAGE = "/users/me/images/{imageId}"
const val UPLOAD_USER_PROFILE_IMAGE = "/users/me/image"
const val GET_ADMIN_ME = "/admins/me"
const val GET_USER_ME_USERS = "/users/me/users"
const val CREATE_USER_POSITION = "/users/position"

const val CREATE_USER = "/public/users"
const val GET_IMAGE_PATH = "/public/images/"

interface UserController {

    @PostMapping(UPLOAD_USER_IMAGE)
    fun uploadUserImage(@PathVariable imageId: Int, @RequestParam("image") file: MultipartFile): UploadUserImageResponse

    @PostMapping(UPLOAD_USER_PROFILE_IMAGE)
    fun uploadUserProfileImage(@RequestParam("image") file: MultipartFile): UploadUserImageResponse

    @PostMapping(CREATE_USER_POSITION)
    fun createUserPosition(@RequestBody createUserPositionRequest: CreateUserPositionRequest): BaseResponse

    @GetMapping(GET_USER_BY_ID)
    fun getUserById(@PathVariable userId: Int): GetUserByIdResponse

    @GetMapping(GET_USER_ME)
    fun getUserMe(): GetUserMeResponse

    @GetMapping(GET_USER_ME_USERS)
    fun getUserMeUsers(@RequestParam("distance") distance: Int,
                       @RequestParam("latitude") latitude: BigDecimal?,
                       @RequestParam("longitude") longitude: BigDecimal?): GetUserMeUsersResponse

    @GetMapping(GET_ADMIN_ME)
    fun getAdminMe(): GetAdminMeResponse

    @PostMapping(CREATE_USER)
    fun createUser(@Valid @RequestBody request: CreateUserRequest): CreateUserResponse

    @PostMapping(POST_USER_ME)
    fun updateUser(@Valid @RequestBody request: UpdateUserRequest): UpdateUserResponse

    @PostMapping(POST_LOGOUT)
    fun logout(principal: OAuth2Authentication): BaseResponse
}
