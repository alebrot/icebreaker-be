package com.icebreaker.be.user

import com.icebreaker.be.service.model.User
import org.springframework.security.core.userdetails.UserDetails

interface UserService {
    fun createUserDetails(username: String?): UserDetails
    fun createUser(email: String, password: String, firstName: String, lastName: String): User
}
