package com.icebreaker.be.service.auth

import com.icebreaker.be.service.model.User


interface AuthService {
    fun getUserOrFail(): User
}
