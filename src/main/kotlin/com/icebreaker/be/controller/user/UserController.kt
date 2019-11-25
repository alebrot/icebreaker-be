package com.icebreaker.be.controller.user

import com.icebreaker.be.controller.chat.HEADER_PLATFORMS
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
const val DELETE_USER_ME = "/users/me"
const val GET_PRODUCTS = "/users/me/products"
const val UPLOAD_USER_IMAGE = "/users/me/images/{imageId}"
const val SWAP_USER_IMAGE = "/users/me/images/from/{imageId1}/to/{imageId2}"
const val UPLOAD_USER_PROFILE_IMAGE = "/users/me/image"
const val GET_USER_ME_USERS = "/users/me/users"
const val CREATE_USER_POSITION = "/users/position"
const val BUY_REWARD = "/users/me/buy"
const val ADMOB_REWARD = "/users/me/admob"
const val INVITE_REWARD = "/users/me/invite/{code}"
const val CREATE_USER = "/public/users"
const val GET_IMAGE_PATH = "/public/images/"
const val GET_IMAGE_PATH_BLURRED = "/public/images/th/"


interface UserController {


    @GetMapping(GET_PRODUCTS)
    fun getProducts(@RequestHeader(HEADER_PLATFORMS) platforms: String): GetProductsResponse

    @PostMapping(BUY_REWARD)
    fun buyReward(@RequestBody request: CreditRequest, @RequestHeader(HEADER_PLATFORMS) platforms: String): CreditResponse

    @PostMapping(ADMOB_REWARD)
    fun admobReward(): CreditResponse

    @PostMapping(INVITE_REWARD)
    fun inviteReward(@PathVariable code: String): CreditResponse

    @PostMapping(UPLOAD_USER_IMAGE)
    fun uploadUserImage(@PathVariable imageId: Int, @RequestParam("image") file: MultipartFile): UploadUserImageResponse

    @PostMapping(SWAP_USER_IMAGE)
    fun swapUserImage(@PathVariable imageId1: Int, @PathVariable imageId2: Int): GetUserMeResponse

    @PostMapping(UPLOAD_USER_PROFILE_IMAGE)
    fun uploadUserProfileImage(@RequestParam("image") file: MultipartFile): UploadUserImageResponse

    @PostMapping(CREATE_USER_POSITION)
    fun createUserPosition(@RequestBody createUserPositionRequest: CreateUserPositionRequest): BaseResponse

    @GetMapping(GET_USER_BY_ID)
    fun getUserById(@PathVariable userId: String): GetUserByIdResponse

    @GetMapping(GET_USER_ME)
    fun getUserMe(): GetUserMeResponse

    @GetMapping(GET_USER_ME_USERS)
    fun getUserMeUsers(@RequestParam("distance") distance: Int,
                       @RequestParam("latitude") latitude: BigDecimal?,
                       @RequestParam("longitude") longitude: BigDecimal?,
                       limit: Int?,
                       offset: Int?): GetUserMeUsersResponse

    @PostMapping(CREATE_USER)
    fun createUser(@Valid @RequestBody request: CreateUserRequest): CreateUserResponse

    @DeleteMapping(DELETE_USER_ME)
    fun deleteUser(@RequestBody request: DeleteUserRequest): BaseResponse

    @PostMapping(POST_USER_ME)
    fun updateUser(@Valid @RequestBody request: UpdateUserRequest): GetUserMeResponse

    @PostMapping(POST_LOGOUT)
    fun logout(principal: OAuth2Authentication): BaseResponse
}
