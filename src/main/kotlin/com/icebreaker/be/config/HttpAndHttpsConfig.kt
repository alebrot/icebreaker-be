package com.icebreaker.be.config

import com.icebreaker.be.CoreProperties
import org.apache.catalina.Context
import org.apache.catalina.LifecycleException
import org.apache.catalina.LifecycleState
import org.apache.catalina.connector.Connector
import org.apache.catalina.valves.rewrite.RewriteValve
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.boot.web.servlet.server.ServletWebServerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class HttpAndHttpsConfig(val coreProperties: CoreProperties) {


    class PropertyBasedRewriteValve(private val unProcessedRules: String) : RewriteValve() {
        @Synchronized
        override fun startInternal() {
            state = LifecycleState.STARTING
            context = getContainer() is Context
            try {
                this.configuration = unProcessedRules
            } catch (e: Exception) {
                throw LifecycleException(e)
            }

        }
    }

    @Bean
    fun servletWebServerFactory(): ServletWebServerFactory {
        val tomcatServletWebServerFactory = TomcatServletWebServerFactory()
        tomcatServletWebServerFactory.additionalTomcatConnectors.add(httpConnector())
        val unProcessedRules = coreProperties.rewriteRules
        unProcessedRules?.let {
            tomcatServletWebServerFactory.addContextValves(PropertyBasedRewriteValve(unProcessedRules))
        }
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