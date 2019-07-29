package com.icebreaker.be.config

import com.icebreaker.be.ImageProperties
import com.icebreaker.be.facade.user.UserFacade
import com.icebreaker.be.service.auth.social.SocialTokenGranter
import com.icebreaker.be.service.file.FileService
import com.icebreaker.be.service.social.SocialService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer
import org.springframework.security.oauth2.provider.ClientDetailsService
import org.springframework.security.oauth2.provider.CompositeTokenGranter
import org.springframework.security.oauth2.provider.OAuth2RequestFactory
import org.springframework.security.oauth2.provider.TokenGranter
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore
import java.util.*
import javax.sql.DataSource

@Configuration
@EnableAuthorizationServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
class AuthServerOAuth2Config(val userDetailsService: UserDetailsService,
                             val dataSource: DataSource,
                             val authenticationManager: AuthenticationManager,
                             val userFacade: UserFacade,
                             val socialService: SocialService,
                             val clientDetailsService: ClientDetailsService,
                             val fileService: FileService,
                             val imageProperties: ImageProperties) : AuthorizationServerConfigurerAdapter() {
    @Bean
    fun oauthAccessDeniedHandler(): OAuth2AccessDeniedHandler {
        return OAuth2AccessDeniedHandler()
    }

    override fun configure(oauthServer: AuthorizationServerSecurityConfigurer) {
        oauthServer.tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()")
                .passwordEncoder(BCryptPasswordEncoder(4))
    }

    @Throws(Exception::class)
    override fun configure(clients: ClientDetailsServiceConfigurer) {
        clients.jdbc(dataSource)
    }

    override fun configure(endpoints: AuthorizationServerEndpointsConfigurer) {
        endpoints.tokenStore(JdbcTokenStore(dataSource))
                .authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService)
        endpoints.tokenGranter(tokenGranter(endpoints))
    }

    private fun tokenGranter(endpoints: AuthorizationServerEndpointsConfigurer): TokenGranter {
        val granters = ArrayList<TokenGranter>(listOf(endpoints.tokenGranter))
        granters.add(SocialTokenGranter(
                socialService,
                endpoints.tokenServices,
                endpoints.clientDetailsService,
                userFacade,
                endpoints.oAuth2RequestFactory))
        return CompositeTokenGranter(granters)
    }

    @Bean
    fun requestFactory(): OAuth2RequestFactory {
        return DefaultOAuth2RequestFactory(clientDetailsService)
    }
}