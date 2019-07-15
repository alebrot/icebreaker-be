package com.icebreaker.be.user.facade.impl

import com.icebreaker.be.ImageProperties
import com.icebreaker.be.auth.UserDetailsDefault
import com.icebreaker.be.service.file.FileFacade
import com.icebreaker.be.service.file.FileService
import com.icebreaker.be.service.model.User
import com.icebreaker.be.user.UserService
import com.icebreaker.be.user.facade.UserFacade
import com.icebreaker.be.user.social.impl.SocialUser
import org.springframework.scheduling.annotation.Async
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class UserFacadeDefault(val userService: UserService,
                        val fileService: FileService,
                        val imageProperties: ImageProperties,
                        val fileFacade: FileFacade) : UserFacade {


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
        val storeImage = fileService.storeImage(url, imageProperties.profileMaxWidth, imageProperties.profileMaxHeight)
        user.imgUrl = storeImage
        userService.updateUserProfilePhoto(user, storeImage)
    }

    @Async
    override fun updateUserLastSeen(user: User) {
        user.lastSeen = LocalDateTime.now()
        userService.updateUser(user)
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