package com.icebreaker.be.controller.user.impl

import com.icebreaker.be.ImageProperties
import com.icebreaker.be.controller.core.dto.BaseResponse
import com.icebreaker.be.controller.user.GET_IMAGE_PATH
import com.icebreaker.be.controller.user.GET_IMAGE_PATH_BLURRED
import com.icebreaker.be.controller.user.UserController
import com.icebreaker.be.controller.user.dto.*
import com.icebreaker.be.ext.decodeToInt
import com.icebreaker.be.facade.user.UserFacade
import com.icebreaker.be.service.auth.AuthService
import com.icebreaker.be.service.credit.CreditService
import com.icebreaker.be.service.file.FileService
import com.icebreaker.be.service.model.*
import com.icebreaker.be.service.user.UserService
import org.hashids.Hashids
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.io.IOException
import java.math.BigDecimal
import java.nio.file.Path
import javax.imageio.ImageIO
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid


@RestController
class UserControllerDefault(val authService: AuthService,
                            val userService: UserService,
                            val fileService: FileService,
                            val imageProperties: ImageProperties,
                            val authorizationServerTokenServices: AuthorizationServerTokenServices,
                            val consumerTokenServices: ConsumerTokenServices,
                            val userFacade: UserFacade,
                            val hashids: Hashids,
                            val creditService: CreditService
) : UserController {

    val log: Logger = LoggerFactory.getLogger(UserControllerDefault::class.java)


    override fun deleteUser(request: DeleteUserRequest): BaseResponse {
        val userOrFail = authService.getUserOrFail()
        userFacade.deleteAllUserImages(userOrFail)
        userService.deleteUser(userOrFail, request.reason)
        return BaseResponse()
    }

    override fun getProducts(platforms: String): GetProductsResponse {
        val store = Store.fromHeader(platforms)
        val products = creditService.getProducts(store)
        val productsDto = products.map { it.toDto() }
        return GetProductsResponse(productsDto)
    }

    override fun buyReward(request: CreditRequest, platforms: String): CreditResponse {
        log.info("buyReward $request")
        val userOrFail = authService.getUserOrFail()
        val store = Store.fromHeader(platforms)

        val receipt = request.receipt ?: throw IllegalArgumentException("receipt must not be null")


        val credit: Credit = when (store) {
            Store.ANDROID -> {
                val token = request.signature ?: throw IllegalArgumentException("signature must not be null")
                val transactionId = request.transactionId
                        ?: throw IllegalArgumentException("transactionId must not be null")

                creditService.purchaseAndroid(userOrFail, transactionId, token, receipt)

            }
            Store.IOS -> {
                creditService.purchaseIos(userOrFail, receipt)
            }
        }

        return CreditResponse(credit.toDto())
    }


    override fun inviteReward(@PathVariable code: String): CreditResponse {
        val userOrFail = authService.getUserOrFail()
        val invitedByUserId = hashids.decodeToInt(code)
        val invitedBy = userService.getUserById(invitedByUserId)
        val rewardCreditsForInvitedPerson = creditService.rewardCreditsForInvitedPerson(userOrFail, invitedBy)
        return CreditResponse(rewardCreditsForInvitedPerson.toDto())
    }

    override fun getCredit(): CreditResponse {
        val userOrFail = authService.getUserOrFail()
        val credits = creditService.getAvailableCredits(userOrFail)
        return CreditResponse(credits.toDto())
    }

    override fun admobReward(): CreditResponse {
        val userOrFail = authService.getUserOrFail()
        val rewardAdmobCredits = creditService.rewardAdmobCredits(userOrFail)
        return CreditResponse(rewardAdmobCredits.toDto())
    }

    override fun swapUserImage(imageId1: Int, imageId2: Int): GetUserMeResponse {
        validateImageId(imageId1)
        validateImageId(imageId2)
        val userOrFail = authService.getUserOrFail()
        val images = userFacade.swapUserImage(userOrFail, imageId1, imageId2)
        //get fresh user with updated thumbnail
        val userById = userService.getUserById(userOrFail.id)
        val authorities = userById.authorities.map { it.toDto() }
        val completeUserDto = CompleteUserDto(userById.toDto(imageProperties.host, hashids), authorities, images.map { it.toDto() })
        return GetUserMeResponse(UserContextDto(completeUserDto))
    }

    override fun logout(principal: OAuth2Authentication): BaseResponse {
        val accessToken = authorizationServerTokenServices.getAccessToken(principal)
        consumerTokenServices.revokeToken(accessToken.value)
        return BaseResponse()
    }

    override fun deleteUserImage(imageId: Int): GetUserMeResponse {
        validateImageId(imageId)
        val userOrFail = authService.getUserOrFail()
        userFacade.deleteUserImage(userOrFail, imageId)

        if (imageId == 1) {
            userFacade.deleteProfileImage(userOrFail)
        }

        val user = userService.getUserById(userOrFail.id)

        val authorities = user.authorities.map { it.toDto() }
        val images = userService.getImages(user)
        val completeUserDto = CompleteUserDto(user.toDto(imageProperties.host, hashids), authorities, images.map { it.toDto() })
        return GetUserMeResponse(UserContextDto(completeUserDto))
    }

    override fun updateUser(request: UpdateUserRequest): GetUserMeResponse {
        val userOrFail = authService.getUserOrFail()
        val bio = request.bio
        if (bio != null) {
            userOrFail.bio = bio
        }

        val gender = request.gender
        if (gender != null) {
            userOrFail.gender = gender
        }

        if (bio != null || gender != null) {
            userService.updateUser(userOrFail)
        }

        val imageIds = request.imageIds

        val images = if (imageIds != null) {
            userFacade.updateUserImages(userOrFail, imageIds)
        } else {
            userService.getImages(userOrFail)
        }

        //get fresh user with updated thumbnail
        val userById = userService.getUserById(userOrFail.id)
        val authorities = userById.authorities.map { it.toDto() }
        val completeUserDto = CompleteUserDto(userById.toDto(imageProperties.host, hashids), authorities, images.map { it.toDto() })

        return GetUserMeResponse(UserContextDto(completeUserDto))
    }

    @Transactional
    override fun uploadUserProfileImage(file: MultipartFile): UploadUserImageResponse {
        val userOrFail = authService.getUserOrFail()

        val fileName = fileService.storeImage(file, imageProperties.profileMaxWidth, imageProperties.profileMaxHeight)

        userFacade.updateUserProfilePhotoAndDeleteOldUserProfile(userOrFail, fileName)

        val fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(GET_IMAGE_PATH)
                .path(fileName)
                .toUriString()

        userFacade.updateFirstUserPhotoIfNecessary(file, userOrFail)

        return UploadUserImageResponse(fileDownloadUri)
    }

    @Transactional
    override fun uploadUserImage(imageId: Int, file: MultipartFile): UploadUserImageResponse {
        val userOrFail = authService.getUserOrFail()

        validateImageId(imageId)

        val fileName = fileService.storeImage(file, imageProperties.maxWidth, imageProperties.maxHeight)

        userFacade.updateImageForUserAndDeleteOldImage(userOrFail, imageId, fileName)
        val fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(GET_IMAGE_PATH)
                .path(fileName)
                .toUriString()

        userFacade.updateUserProfilePhotoIfNecessary(file, userOrFail)

        return UploadUserImageResponse(fileDownloadUri)
    }

    private fun validateImageId(imageId: Int) {
        if (imageId !in 1..3) {
            throw IllegalArgumentException("wrong image id, allowed values [1,2,3]")
        }
    }


    @GetMapping("$GET_IMAGE_PATH{fileName:.+}")
    fun downloadFile(@PathVariable fileName: String, request: HttpServletRequest): ResponseEntity<Resource> {
        val resource = fileService.loadFileAsResource(fileName)
        val contentType: String = try {
            request.servletContext.getMimeType(resource.file.absolutePath)
        } catch (ex: IOException) {
            "application/octet-stream"
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource)
    }


    @GetMapping("$GET_IMAGE_PATH_BLURRED{fileName:.+}")
    fun downloadBlurredImage(@PathVariable fileName: String, request: HttpServletRequest): ResponseEntity<Resource> {

        val path: Path = fileService.loadFileAsPath(fileName)
                ?: throw IllegalArgumentException("Not valid fileName $fileName")

        val image = ImageIO.read(path.toFile())

        val blur = fileService.blur(image)

        val contentType: String = try {
            request.servletContext.getMimeType(path.toAbsolutePath().toString())
        } catch (ex: IOException) {
            "application/octet-stream"
        }

        val bao = fileService.toByteArrayOutputStream(fileName, blur)

        val toByteArray = bao.toByteArray()
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(ByteArrayResource(toByteArray))
    }


    override fun createUserPosition(createUserPositionRequest: CreateUserPositionRequest): BaseResponse {
        val userOrFail = authService.getUserOrFail()
        userService.updateUserPosition(userOrFail, createUserPositionRequest.latitude, createUserPositionRequest.longitude)
        return BaseResponse()
    }

    override fun getUserMeUsers(distance: Int,
                                latitude: BigDecimal?,
                                longitude: BigDecimal?,
                                gender: Gender?,
                                online: Boolean?,
                                ageMin: Int?,
                                ageMax: Int?,
                                limit: Int?,
                                offset: Int?): GetUserMeUsersResponse {

        val age = IntRange(ageMin ?: 18, ageMax ?: 122)

        val defaultLimit = 10;
        val defaultOffset = 0;

        val limitSafe: Int = if ((limit ?: defaultLimit) !in 0..defaultLimit) defaultLimit else (limit ?: defaultLimit)
        val offsetSafe: Int = if (offset ?: defaultOffset < 0) defaultOffset else offset ?: defaultOffset

        val userOrFail = authService.getUserOrFail()

        val usersCloseToUser: List<UserWithDistance> = if (latitude != null && longitude != null) {
            userService.getUsersCloseToUserPosition(userOrFail, distance, latitude, longitude, age, gender, online, limitSafe, offsetSafe)
        } else {
            userService.getUsersCloseToUser(userOrFail, distance, age, gender, online, limitSafe, offsetSafe)
        }

        val mapped = usersCloseToUser.map {
            UserWithDistanceDto(it.distance, it.user.toDto(imageProperties.host, hashids))
        }.shuffled()

        return GetUserMeUsersResponse(mapped.size, mapped)
    }

    @Transactional
    override fun createUser(@Valid @RequestBody request: CreateUserRequest): CreateUserResponse {
        val user: User = userService.createUser(
                request.email,
                request.password,
                request.firstName,
                request.lastName,
                request.birthday)

        return CreateUserResponse(user.toDto(imageProperties.host, hashids))
    }

    override fun getUserById(userId: String): GetUserByIdResponse {
        val decodedUserId = hashids.decodeToInt(userId)

        val userOrFail = authService.getUserOrFail()
        val user = userService.getUserById(decodedUserId)
        val authorities = user.authorities.map { it.toDto() }
        val images = userService.getImages(user)
        val distanceBetweenUsers = userService.getDistanceBetweenUsers(userOrFail, user)
        val completeUserDto = CompleteUserDtoWithDistance(user.toDto(imageProperties.host, hashids), authorities, images.map { it.toDto() }, distanceBetweenUsers)
        return GetUserByIdResponse(completeUserDto)

    }

    @PreAuthorize("hasAuthority('USER')")
    override fun getUserMe(): GetUserMeResponse {
        val userOrFail = authService.getUserOrFail()

        val user = userService.getUserById(userOrFail.id)

        val authorities = user.authorities.map { it.toDto() }
        val images = userService.getImages(user)

        if (user.imgUrl == null) {
            val userProfileImageName = userService.getUserProfileImageName(user)
            user.imgUrl = userProfileImageName
        }

        creditService.rewardCredits(user)

        val availableCredits = creditService.getAvailableCredits(user)
        user.credits = availableCredits

        val completeUserDto = CompleteUserDto(user.toDto(imageProperties.host, hashids), authorities, images.map { it.toDto() })

        return GetUserMeResponse(UserContextDto(completeUserDto))
    }
}