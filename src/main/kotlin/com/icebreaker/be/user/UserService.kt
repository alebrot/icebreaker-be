package com.icebreaker.be.user

import com.icebreaker.be.service.model.User
import com.icebreaker.be.service.model.UserWithDistance
import com.icebreaker.be.user.social.impl.SocialUser
import org.springframework.security.core.userdetails.UserDetails
import java.math.BigDecimal
import java.time.LocalDate

interface UserService {
    fun createUserDetails(username: String?): UserDetails
    fun createUser(email: String, password: String, firstName: String, lastName: String, birthday: LocalDate): User
    fun createUserDetails(socialUser: SocialUser): Pair<UserDetails, Boolean>
    fun getUsersCloseToUser(user: User, distanceInMeters: Int): List<UserWithDistance>
    fun getUsersCloseToUserPosition(user: User, distanceInMeters: Int, latitude: BigDecimal, longitude: BigDecimal): List<UserWithDistance>
    fun updateUserPosition(user: User, latitude: BigDecimal, longitude: BigDecimal)
    fun updateUserProfilePhoto(user: User, imageName: String)
    fun updateUser(user: User): User
    fun getImages(user: User): List<String>
    fun getUserById(userId: Int): User
    fun updateImageForUser(user: User, position: Int, imageName: String)
    fun getDistanceBetweenUsers(user1: User, user2: User): Int?
    fun getImageNameByPosition(user: User, position: Int): String?
    fun getUserProfileImageName(user: User): String?
    fun getFakeUsers(distanceInMeters: Int): List<UserWithDistance>
}
