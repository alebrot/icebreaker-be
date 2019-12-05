package com.icebreaker.be.config

import org.apache.catalina.connector.Connector
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.boot.web.servlet.server.ServletWebServerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class HttpAndHttpsConfig {

    @Bean
    fun servletWebServerFactory(): ServletWebServerFactory {
        val tomcatServletWebServerFactory = TomcatServletWebServerFactory()
        tomcatServletWebServerFactory.additionalTomcatConnectors.add(httpConnector())
        return tomcatServletWebServerFactory
    }

    private fun httpConnector(): Connector? {
        val connector = Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL)
        connector.scheme = "http"
        connector.port = 8080
        connector.secure = false
        return connector
    }
}