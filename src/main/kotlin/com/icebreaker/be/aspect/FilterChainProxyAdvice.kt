package com.icebreaker.be.aspect

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.web.firewall.RequestRejectedException
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Aspect
@Component
class FilterChainProxyAdvice {

    val logger: Logger = LoggerFactory.getLogger(FilterChainProxyAdvice::class.java)

    @Around("execution(public void org.springframework.security.web.FilterChainProxy.doFilter(..))")
    fun handleRequestRejectedException(pjp: ProceedingJoinPoint) {
        try {
            pjp.proceed();
        } catch (exception: RequestRejectedException) {
            val request = pjp.args[0] as HttpServletRequest
            val response = pjp.args[1] as HttpServletResponse
            logger.warn("${exception.localizedMessage} ${request.method} ${request.requestURI}")
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, exception.localizedMessage);
        }
    }
}