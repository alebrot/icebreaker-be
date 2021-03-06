package com.icebreaker.be.facade.user

import com.icebreaker.be.service.model.User
import com.icebreaker.be.service.model.UserImage
import com.icebreaker.be.service.social.impl.SocialUser
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.multipart.MultipartFile

interface UserFacade {
    fun createUserDetailsAndUploadPhoto(socialUser: SocialUser): UserDetails
    fun updateUserLastSeen(user: User)
    fun updateUserLastSeenSync(user: User)
    fun updateImageForUserAndDeleteOldImage(user: User, position: Int, imageName: String)
    fun updateUserProfilePhotoAndDeleteOldUserProfile(user: User, imageName: String)
    fun updateFirstUserPhotoIfNecessary(file: MultipartFile, user: User)
    fun updateUserProfilePhotoIfNecessary(file: MultipartFile, user: User)
    fun swapUserImage(userOrFail: User, imageId1: Int, imageId2: Int): List<UserImage>
    fun updateUserImages(userOrFail: User, imageIds: List<Int>): List<UserImage>
    fun deleteAllUserImages(user: User)
    fun updateUserLastSeenForFakeUsers()
    fun deleteUserImage(user: User, imageId: Int)
    fun deleteProfileImage(user: User)
    fun sendInvitationTo(users: List<User>)
}
