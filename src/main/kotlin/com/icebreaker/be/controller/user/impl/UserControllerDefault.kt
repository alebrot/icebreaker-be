package com.icebreaker.be.controller.user.impl

import com.icebreaker.be.CoreProperties
import com.icebreaker.be.ImageProperties
import com.icebreaker.be.controller.core.dto.BaseResponse
import com.icebreaker.be.controller.user.GET_IMAGE_PATH
import com.icebreaker.be.controller.user.UserController
import com.icebreaker.be.controller.user.dto.*
import com.icebreaker.be.ext.decodeToInt
import com.icebreaker.be.service.auth.AuthService
import com.icebreaker.be.service.file.FileService
import com.icebreaker.be.service.model.User
import com.icebreaker.be.service.model.UserWithDistance
import com.icebreaker.be.service.model.toDto
import com.icebreaker.be.user.UserService
import com.icebreaker.be.user.facade.UserFacade
import org.hashids.Hashids
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
                            val coreProperties: CoreProperties) : UserController {

    override fun swapUserImage(imageId1: Int, imageId2: Int): GetUserMeResponse {
        validateImageId(imageId1)
        validateImageId(imageId2)
        val userOrFail = authService.getUserOrFail()
        val images = userFacade.swapUserImage(userOrFail, imageId1, imageId2)
        //get fresh user with updated thumbnail
        val userById = userService.getUserById(userOrFail.id)
        val authorities = userById.authorities.map { it.toDto() }
        val completeUserDto = CompleteUserDto(userById.toDto(imageProperties.host, hashids), authorities, images)
        return GetUserMeResponse(UserContextDto(completeUserDto))
    }

    override fun logout(principal: OAuth2Authentication): BaseResponse {
        val accessToken = authorizationServerTokenServices.getAccessToken(principal)
        consumerTokenServices.revokeToken(accessToken.value)
        return BaseResponse()
    }

    override fun updateUser(request: UpdateUserRequest): UpdateUserResponse {
        val userOrFail = authService.getUserOrFail()
        if (request.bio != null) {
            userOrFail.bio = request.bio
        }

        if (request.gender != null) {
            userOrFail.gender = request.gender
        }

        val updateUser = userService.updateUser(userOrFail)
        return UpdateUserResponse(updateUser.toDto(imageProperties.host, hashids))
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

    override fun createUserPosition(createUserPositionRequest: CreateUserPositionRequest): BaseResponse {
        val userOrFail = authService.getUserOrFail()
        userService.updateUserPosition(userOrFail, createUserPositionRequest.latitude, createUserPositionRequest.longitude)
        return BaseResponse()
    }

    override fun getUserMeUsers(distance: Int,
                                latitude: BigDecimal?,
                                longitude: BigDecimal?): GetUserMeUsersResponse {
        val userOrFail = authService.getUserOrFail()

        val usersCloseToUser: List<UserWithDistance> = if (latitude != null && longitude != null) {
            userService.getUsersCloseToUserPosition(userOrFail, distance, latitude, longitude)
        } else {
            userService.getUsersCloseToUser(userOrFail, distance)
        }

        val fakeUsers = if (coreProperties.fake) userService.getFakeUsers(distance) else emptyList()

        val mapped = ArrayList(usersCloseToUser).union(fakeUsers).map {
            UserWithDistanceDto(it.distance, it.user.toDto(imageProperties.host, hashids))
        }

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
        val completeUserDto = CompleteUserDtoWithDistance(user.toDto(imageProperties.host, hashids), authorities, images, distanceBetweenUsers)
        return GetUserByIdResponse(completeUserDto)

    }

    @PreAuthorize("hasAuthority('USER')")
    override fun getUserMe(): GetUserMeResponse {
        val userOrFail = authService.getUserOrFail()
        val authorities = userOrFail.authorities.map { it.toDto() }
        val images = userService.getImages(userOrFail)
        val completeUserDto = CompleteUserDto(userOrFail.toDto(imageProperties.host, hashids), authorities, images)
        return GetUserMeResponse(UserContextDto(completeUserDto))
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    override fun getAdminMe(): GetAdminMeResponse {
        val userOrFail = authService.getUserOrFail()
        val authorities = userOrFail.authorities.map { it.toDto() }
        val images = userService.getImages(userOrFail)
        val completeUserDto = CompleteUserDto(userOrFail.toDto(imageProperties.host, hashids), authorities, images)
        return GetAdminMeResponse(AdminContextDto(completeUserDto))
    }
}