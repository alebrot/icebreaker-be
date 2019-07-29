package com.icebreaker.be.service.social

import com.icebreaker.be.service.social.impl.SocialUser

interface SocialService {

    fun getUser(token: String): SocialUser
}
