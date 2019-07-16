package com.icebreaker.be.user.facade

import com.icebreaker.be.service.model.User
import com.icebreaker.be.user.social.impl.SocialUser
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.multipart.MultipartFile

interface UserFacade {

    fun createUserDetailsAndUploadPhoto(socialUser: SocialUser): UserDetails
    fun updateUserLastSeen(user: User)
    fun updateImageForUserAndDeleteOldImage(user: User, position: Int, imageName: String)
    fun updateUserProfilePhotoAndDeleteOldUserProfile(user: User, imageName: String)
    fun updateFirstUserPhotoIfNecessary(file: MultipartFile, user: User)
    fun updateUserProfilePhotoIfNecessary(file: MultipartFile, user: User)
}
