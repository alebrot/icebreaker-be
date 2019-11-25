package com.icebreaker.be.config

import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer

@Configuration
@EnableResourceServer
class ResourceServerConfiguration : ResourceServerConfigurerAdapter() {

    val resourceId = "resource-server-rest-api"

    val securedReadScope = "#oauth2.hasScope('read')"

    val securedActuatorScope = "#oauth2.hasScope('actuator')"

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
                .antMatchers("/actuator/**").access(securedActuatorScope)
                .antMatchers("/public/**").permitAll()
                .antMatchers("/webjars/**").permitAll()

                .antMatchers("/").permitAll()
                .antMatchers("/index.*").permitAll()
                .antMatchers("/*.js").permitAll()
                .antMatchers("/*.css").permitAll()

                .antMatchers(HttpMethod.POST, securedPattern).access(securedWriteScope)
                .antMatchers(HttpMethod.PUT, securedPattern).access(securedWriteScope)
                .antMatchers(HttpMethod.DELETE, securedPattern).access(securedWriteScope)
                .antMatchers(HttpMethod.PATCH, securedPattern).access(securedWriteScope)
                .anyRequest().access(securedReadScope)
    }

}