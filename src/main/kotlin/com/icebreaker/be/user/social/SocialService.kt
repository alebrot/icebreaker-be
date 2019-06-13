package com.icebreaker.be.user.social

import com.icebreaker.be.user.social.impl.SocialUser

interface SocialService {

    fun getUser(token: String): SocialUser
}
