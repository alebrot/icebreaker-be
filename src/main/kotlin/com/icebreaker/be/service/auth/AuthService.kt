package com.icebreaker.be.service.auth

import com.icebreaker.be.service.model.User
import org.springframework.security.oauth2.provider.OAuth2Authentication


interface AuthService {
    fun getUserOrFail(): User
    fun getUserOrFail(oAuth2Authentication: OAuth2Authentication): User
    fun getUser(): User?
}
