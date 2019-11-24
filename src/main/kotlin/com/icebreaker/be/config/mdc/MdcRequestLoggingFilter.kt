package com.icebreaker.be.config.mdc

import com.icebreaker.be.service.auth.AuthService
import org.apache.commons.codec.digest.DigestUtils
import org.slf4j.MDC
import org.springframework.web.filter.AbstractRequestLoggingFilter
import javax.servlet.http.HttpServletRequest

class MdcRequestLoggingFilter(val authService: AuthService) : AbstractRequestLoggingFilter() {
    override fun shouldLog(request: HttpServletRequest): Boolean {
        return logger.isInfoEnabled
    }

    override fun beforeRequest(request: HttpServletRequest, message: String) {

        val header: String? = request.getHeader("Authorization")
        val encodedHeader = if (header != null && header.contains("Bearer")) DigestUtils.sha256Hex(header) else ""
        MDC.put("userSessionId", encodedHeader)

        val user = authService.getUser()
        if (user != null) {
            MDC.put("userId", user.id.toString())
        }

        logger.info(message)
    }

    override fun afterRequest(request: HttpServletRequest, message: String) {
        logger.info(message)
        MDC.clear()
    }
}