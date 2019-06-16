package com.icebreaker.be.controller.user.impl

import com.icebreaker.be.controller.core.dto.BaseResponse
import com.icebreaker.be.controller.user.UserController
import com.icebreaker.be.controller.user.dto.*
import com.icebreaker.be.service.auth.AuthService
import com.icebreaker.be.service.file.FileService
import com.icebreaker.be.service.model.User
import com.icebreaker.be.service.model.UserWithDistance
import com.icebreaker.be.service.model.toDto
import com.icebreaker.be.user.UserService
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
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
                            val fileService: FileService) : UserController {
    @Transactional
    override fun uploadUserProfileImage(file: MultipartFile): UploadUserImageResponse {
        val userOrFail = authService.getUserOrFail()

        val fileName = fileService.storeFile(file, 100, 100)

        userService.updateUserProfilePhoto(userOrFail, fileName)

        val fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/public/images/")
                .path(fileName)
                .toUriString()
        return UploadUserImageResponse(fileDownloadUri)
    }

    override fun uploadUserImage(imageId: Int, file: MultipartFile): UploadUserImageResponse {
        val userOrFail = authService.getUserOrFail()

        val fileName = fileService.storeFile(file, 100, 200)
        val fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/public/images/")
                .path(fileName)
                .toUriString()
        return UploadUserImageResponse(fileDownloadUri)
    }


    @GetMapping("/public/images/{fileName:.+}")
    fun downloadFile(@PathVariable fileName: String, request: HttpServletRequest): ResponseEntity<Resource> {
        val resource = fileService.loadFileAsResource(fileName)
        val contentType: String = try {
            request.servletContext.getMimeType(resource.file.absolutePath)
        } catch (ex: IOException) {
            "application/octet-stream"
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body<Resource>(resource)
    }

    override fun createUserPosition(request: CreateUserPositionRequest): BaseResponse {
        val userOrFail = authService.getUserOrFail()
        userService.updateUserPosition(userOrFail, request.latitude, request.longitude)
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