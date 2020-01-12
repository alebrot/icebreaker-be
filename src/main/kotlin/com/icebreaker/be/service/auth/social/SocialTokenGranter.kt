package com.icebreaker.be.service.auth.social

import com.icebreaker.be.facade.user.UserFacade
import com.icebreaker.be.service.social.SocialService
import org.springframework.security.oauth2.provider.*
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices

const val GRANT_TYPE = "social"


open class SocialTokenGranter(private val socialService: SocialService,
                              tokenServices: AuthorizationServerTokenServices,
                              clientDetailsService: ClientDetailsService,
                              private val userFacade: UserFacade,
                              requestFactory: OAuth2RequestFactory) : AbstractTokenGranter(tokenServices, clientDetailsService, requestFactory, GRANT_TYPE) {


    override fun getOAuth2Authentication(client: ClientDetails?, tokenRequest: TokenRequest): OAuth2Authentication {
        val storedOAuth2Request = requestFactory.createOAuth2Request(client, tokenRequest)

        val token = tokenRequest.requestParameters["access_token"]
                ?: throw IllegalArgumentException("access_token not found")

        val socialTypeString = tokenRequest.requestParameters["network"]
                ?: throw IllegalArgumentException("network not found")

        val socialType = SocialType.valueOf(socialTypeString.toUpperCase())
        if (socialType == SocialType.FACEBOOK) {
        } else {
            throw IllegalArgumentException("unsupported network $socialType")
        }

        val socialUser = socialService.getUser(token)
        val createUserDetails = userFacade.createUserDetailsAndUploadPhoto(socialUser)


        val socialAuthentication = SocialAuthentication(createUserDetails)
        socialAuthentication.isAuthenticated = true
        return OAuth2Authentication(storedOAuth2Request, socialAuthentication)
    }
}
