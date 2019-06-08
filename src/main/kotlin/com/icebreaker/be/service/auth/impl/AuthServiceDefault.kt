package com.icebreaker.be.service.auth.impl

import com.icebreaker.be.auth.UserDetailsDefault
import com.icebreaker.be.service.auth.AuthService
import com.icebreaker.be.service.model.User
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class AuthServiceDefault : AuthService {
    override fun getUserOrFail(): User {
        val authentication = (SecurityContextHolder.getContext().authentication
                ?: throw IllegalStateException("authentication is null, probably user is not logged in"))

        val userDetails = authentication.principal as? UserDetailsDefault
                ?: throw IllegalStateException("authentication.principal is not of type UserDetailsDefault")
        return userDetails.user
    }
}