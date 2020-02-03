package com.icebreaker.be.service.user

import com.icebreaker.be.service.model.Gender
import com.icebreaker.be.service.model.User
import com.icebreaker.be.service.model.UserImage
import com.icebreaker.be.service.model.UserWithDistance
import com.icebreaker.be.service.social.impl.SocialUser
import org.springframework.security.core.userdetails.UserDetails
import java.math.BigDecimal
import java.time.LocalDate

interface UserService {
    fun createUserDetails(username: String?): UserDetails
    fun createUser(email: String, password: String, firstName: String, lastName: String?, birthday: LocalDate): User
    fun createUserDetails(socialUser: SocialUser): Pair<UserDetails, Boolean>
    fun getUsersCloseToUser(user: User, distanceInMeters: Int, age: IntRange, gender: Gender?, online: Boolean?, limit: Int, offset: Int): List<UserWithDistance>
    fun getUsersCloseToUserPosition(user: User, distanceInMeters: Int, latitude: BigDecimal, longitude: BigDecimal, age: IntRange, gender: Gender?, online: Boolean?, limit: Int, offset: Int): List<UserWithDistance>
    fun updateUserPosition(user: User, latitude: BigDecimal, longitude: BigDecimal)
    fun updateUserProfilePhoto(user: User, imageName: String?)
    fun updateLastSeen(user: User)
    fun updateUser(user: User): User
    fun getImages(user: User): List<UserImage>
    fun getUserById(userId: Int): User
    fun updateImageForUser(user: User, position: Int, imageName: String)
    fun getDistanceBetweenUsers(user1: User, user2: User): Int?
    fun getImageNameByPosition(user: User, position: Int): String?
    fun getUserProfileImageName(user: User): String?
    fun getFakeUsers(distanceInMeters: Int, limit: Int, offset: Int): List<UserWithDistance>
    fun isFakeUser(user: User): Boolean
    fun getUserByEmail(email: String): User?
    fun getUserByEmailOrFail(email: String): User
    fun deleteUser(user: User, reason: String?)
    fun getRealUsersOnlineCount(): Int
    fun getFakeUsers(limit: Int, offset: Int): List<User>
    fun deleteImageByPosition(user: User, position: Int)
    fun getRealUsersOnline(): List<User>
    fun getRealUsersOnlineWithLimitedAmountOfCreditsAndNoChatsWithFakeUsers(): List<User>
    fun getFakeUsersByGender(gender: Gender, limit: Int, offset: Int): List<User>
    fun getRealUsersOnlineAndNoChatsWithFakeUsers(): List<User>
}
