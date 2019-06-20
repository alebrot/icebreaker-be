package com.icebreaker.be.user.facade

import com.icebreaker.be.user.social.impl.SocialUser
import org.springframework.security.core.userdetails.UserDetails

interface UserFacade {

    fun createUserDetailsAndUploadPhoto(socialUser: SocialUser): UserDetails
}
