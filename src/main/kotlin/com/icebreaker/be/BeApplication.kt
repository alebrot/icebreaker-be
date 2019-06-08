package com.icebreaker.be

import com.icebreaker.be.user.impl.UserServiceDefault
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import java.util.*
import javax.sql.DataSource

@SpringBootApplication
class BeApplication

fun main(args: Array<String>) {
    runApplication<BeApplication>(*args)
}

@Configuration
@EnableResourceServer
class ResourceServerConfiguration : ResourceServerConfigurerAdapter() {

    val resourceId = "resource-server-rest-api"

    val securedReadScope = "#oauth2.hasScope('read')"

    val securedWriteScope = "#oauth2.hasScope('write')"

    val securedPattern = "/**"

    override fun configure(resources: ResourceServerSecurityConfigurer) {
        resources.resourceId(resourceId)
    }

    override fun configure(http: HttpSecurity) {
        http.requestMatchers()
                .antMatchers(securedPattern)
                .and()
                .authorizeRequests()
                .antMatchers("/public/**").permitAll()
                .antMatchers(HttpMethod.POST, securedPattern).access(securedWriteScope)//"#oauth2.hasScope('write')"
                .anyRequest().access(securedReadScope)
    }

}

@Configuration
@EnableWebSecurity
class SecurityConfig(val userService: UserServiceDefault, val passwordEncoder: PasswordEncoder) : WebSecurityConfigurerAdapter() {

    @Bean
    override fun userDetailsService(): UserDetailsService {
        return UserDetailsService(userService::createUserDetails)
    }

    override fun configure(http: HttpSecurity) {
        super.configure(http.csrf().disable())

    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder)
    }

    @Bean
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

}

@Configuration
@EnableAuthorizationServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
class AuthServerOAuth2Config(val userDetailsService: UserDetailsService,
                             val dataSource: DataSource,
                             val authenticationManager: AuthenticationManager) : AuthorizationServerConfigurerAdapter() {
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
    }
}

@Configuration
class CorsConfig {
    @Bean
    fun corsFilterRegistrationBean(): FilterRegistrationBean<*> {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.applyPermitDefaultValues()
        config.allowCredentials = true
        config.allowedOrigins = Arrays.asList("*")
        config.allowedHeaders = Arrays.asList("*")
        config.allowedMethods = Arrays.asList("*")
        config.exposedHeaders = Arrays.asList("content-length")
        config.maxAge = 3600L
        source.registerCorsConfiguration("/**", config)
        val bean = FilterRegistrationBean(CorsFilter(source))
        bean.order = 0
        return bean
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder(8)
    }
}