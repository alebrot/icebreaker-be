package com.icebreaker.be.facade.user.impl

import com.icebreaker.be.ImageProperties
import com.icebreaker.be.auth.UserDetailsDefault
import com.icebreaker.be.facade.user.UserFacade
import com.icebreaker.be.service.file.FileFacade
import com.icebreaker.be.service.file.FileService
import com.icebreaker.be.service.model.User
import com.icebreaker.be.service.social.impl.SocialUser
import com.icebreaker.be.service.user.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime

@Service
class UserFacadeDefault(val userService: UserService,
                        val fileService: FileService,
                        val imageProperties: ImageProperties,
                        val fileFacade: FileFacade) : UserFacade {


    val logger: Logger = LoggerFactory.getLogger(UserFacadeDefault::class.java)


    val positionOfFirstPhoto = 1

    @Transactional
    override fun updateUserImages(userOrFail: User, imageIds: List<Int>): List<String> {

        if (imageIds.isEmpty() || imageIds.size > 3 || imageIds.toSet().size != imageIds.size) {
            throw IllegalArgumentException("not valid imageIds")
        } else {

            val imageNames = imageIds.mapNotNull {
                userService.getImageNameByPosition(userOrFail, it)
            }

            if (imageNames.size != imageIds.size) {
                throw IllegalArgumentException("not valid imageIds, not found image names from imageIds")
            }

            imageNames.forEachIndexed { index, imageName ->
                userService.updateImageForUser(userOrFail, index + 1, imageName)
            }


            val imageNameToMakeThumbnail = imageNames[0]

            //remove old profile image
            tryToRemoveOldImage(userOrFail)
            // upload new
            val newProfileImage = fileService.loadFileAsPath(imageNameToMakeThumbnail)
            if (newProfileImage != null) {
                val storeImage = fileService.storeImage(newProfileImage, imageProperties.profileMaxWidth, imageProperties.profileMaxHeight)
                userService.updateUserProfilePhoto(userOrFail, storeImage)
            }

            return userService.getImages(userOrFail)
        }

    }

    @Transactional
    override fun swapUserImage(userOrFail: User, imageId1: Int, imageId2: Int): List<String> {

        val imageName1 = userService.getImageNameByPosition(userOrFail, imageId1)
        val imageName2 = userService.getImageNameByPosition(userOrFail, imageId2)


        if (imageId1 != imageId2 && imageName1 != null && imageName2 != null) {

            userService.updateImageForUser(userOrFail, imageId1, imageName2)
            userService.updateImageForUser(userOrFail, imageId2, imageName1)


            val imageNameToMakeThumbnail = when {
                imageId1 == positionOfFirstPhoto -> imageName2
                imageId2 == positionOfFirstPhoto -> imageName1
                else -> null
            }

            if (imageNameToMakeThumbnail != null) {
                //remove old profile image
                tryToRemoveOldImage(userOrFail)
                // upload new
                val newProfileImage = fileService.loadFileAsPath(imageNameToMakeThumbnail)
                if (newProfileImage != null) {
                    val storeImage = fileService.storeImage(newProfileImage, imageProperties.profileMaxWidth, imageProperties.profileMaxHeight)
                    userService.updateUserProfilePhoto(userOrFail, storeImage)
                }
            }

        } else {
            throw IllegalArgumentException("not valid imageId1 or imageId")
        }
        return userService.getImages(userOrFail)
    }

    private fun tryToRemoveOldImage(userOrFail: User) {
        var userProfileImageName: String? = null
        try {
            userProfileImageName = userService.getUserProfileImageName(userOrFail)
            if (userProfileImageName != null) {
                val loadFileAsPath = fileService.loadFileAsPath(userProfileImageName)
                if (loadFileAsPath != null) {
                    fileService.deleteFile(loadFileAsPath)
                }
            }
        } catch (e: Exception) {
            logger.error("Unable to remove profile image for user id ${userOrFail.id} imageName $userProfileImageName")
        }
    }

    override fun createUserDetailsAndUploadPhoto(socialUser: SocialUser): UserDetails {
        val (userDetails, justCreated) = userService.createUserDetails(socialUser)
        if (justCreated) {
            if (userDetails is UserDetailsDefault) {
                val url = socialUser.imgUrl
                if (url != null) {
                    uploadUserSocialPhoto(url, userDetails.user)
                }
            }

        }
        return userDetails
    }

    //    @Async
    fun uploadUserSocialPhoto(url: String, user: User) {
        val storeProfileImage = fileService.storeImage(url, imageProperties.profileMaxWidth, imageProperties.profileMaxHeight)
        user.imgUrl = storeProfileImage
        userService.updateUserProfilePhoto(user, storeProfileImage)
        val storeImage = fileService.storeImage(url, imageProperties.maxWidth, imageProperties.maxHeight)
        userService.updateImageForUser(user, positionOfFirstPhoto, storeImage)
    }

    @Async
    override fun updateUserLastSeen(user: User) {
        user.lastSeen = LocalDateTime.now()
        userService.updateUser(user)
    }

    override fun updateFirstUserPhotoIfNecessary(file: MultipartFile, user: User) {
        val image = userService.getImageNameByPosition(user, positionOfFirstPhoto)
        if (image == null) {//update first image as well
            val imageName = fileService.storeImage(file, imageProperties.maxWidth, imageProperties.maxHeight)
            userService.updateImageForUser(user, positionOfFirstPhoto, imageName)
        }
    }

    override fun updateUserProfilePhotoIfNecessary(file: MultipartFile, user: User) {
        if (user.imgUrl.isNullOrBlank()) {//update profile image as well
            val profileImageName = fileService.storeImage(file, imageProperties.profileMaxWidth, imageProperties.profileMaxHeight)
            userService.updateUserProfilePhoto(user, profileImageName)
        }
    }

    override fun updateImageForUserAndDeleteOldImage(user: User, position: Int, imageName: String) {
        val imageNameToDeleteFromDisc = userService.getImageNameByPosition(user, position)
        if (imageNameToDeleteFromDisc != null) {
            fileFacade.deleteImageIfExistsAsync(imageNameToDeleteFromDisc)
        }
        userService.updateImageForUser(user, position, imageName)
    }


    override fun updateUserProfilePhotoAndDeleteOldUserProfile(user: User, imageName: String) {
        val userProfileImageNameToDeleteFromDisc = userService.getUserProfileImageName(user)
        if (userProfileImageNameToDeleteFromDisc != null) {
            fileFacade.deleteImageIfExistsAsync(userProfileImageNameToDeleteFromDisc)
        }
        userService.updateUserProfilePhoto(user, imageName)
    }

}