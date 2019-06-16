package com.icebreaker.be.user

import com.icebreaker.be.service.model.User
import com.icebreaker.be.service.model.UserWithDistance
import com.icebreaker.be.user.social.impl.SocialUser
import org.springframework.security.core.userdetails.UserDetails
import java.math.BigDecimal

interface UserService {
    fun createUserDetails(username: String?): UserDetails
    fun createUser(email: String, password: String, firstName: String, lastName: String): User
    fun createUserDetails(socialUser: SocialUser): UserDetails
    fun getUsersCloseToUser(user: User, distanceInMeters: Int): List<UserWithDistance>
    fun getUsersCloseToUserPosition(user: User, distanceInMeters: Int, latitude: BigDecimal, longitude: BigDecimal): List<UserWithDistance>
    fun updateUserPosition(user: User, latitude: BigDecimal, longitude: BigDecimal)
}
