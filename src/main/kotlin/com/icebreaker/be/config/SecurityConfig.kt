package com.icebreaker.be.config

import com.icebreaker.be.service.user.UserService
import com.icebreaker.be.service.user.impl.UserServiceDefault
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
@EnableWebSecurity
class SecurityConfig(val userService: UserServiceDefault, val passwordEncoder: PasswordEncoder) : WebSecurityConfigurerAdapter() {

    @Bean
    override fun userDetailsService(): UserDetailsService {
        return UserDetailsService(userService::createUserDetails)
    }

    override fun configure(http: HttpSecurity) {
        super.configure(http.csrf().disable())
        // TODO fix
        http.authorizeRequests().antMatchers(HttpMethod.OPTIONS, "/path/to/allow").permitAll()

    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder)
    }

    @Bean
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }
}