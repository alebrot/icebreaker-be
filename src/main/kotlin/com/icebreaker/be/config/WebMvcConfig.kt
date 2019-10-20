package com.icebreaker.be.config

import com.icebreaker.be.facade.user.UserFacade
import com.icebreaker.be.service.auth.AuthService
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Configuration
class WebMvcConfig(val authService: AuthService, val userFacade: UserFacade) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(object : HandlerInterceptor {
            override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
                if (request.method == "GET") {
                    val user = authService.getUser()
                    if (user != null) {
                        userFacade.updateUserLastSeen(user)
                    }
                }

                return super.preHandle(request, response, handler)
            }
        });
    }
}