package com.icebreaker.be

import com.icebreaker.be.config.mdc.MdcRequestLoggingFilter
import com.icebreaker.be.extra.LoggingRequestInterceptor
import com.icebreaker.be.service.auth.AuthService
import org.hashids.Hashids
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.client.BufferingClientHttpRequestFactory
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.SchedulingConfigurer
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.scheduling.config.ScheduledTaskRegistrar
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.web.client.RestTemplate
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@EnableScheduling
@EnableAsync
@SpringBootApplication
@EnableConfigurationProperties(FileStorageProperties::class, ImageProperties::class, PushProperties::class, CoreProperties::class)
@EnableTransactionManagement
class BeApplication(val coreProperties: CoreProperties, val authService: AuthService) {
    @Bean
    fun restTemplate(): RestTemplate {
        val restTemplate = RestTemplate(BufferingClientHttpRequestFactory(SimpleClientHttpRequestFactory()))
        restTemplate.interceptors.add(LoggingRequestInterceptor())
        return restTemplate
    }

    @Bean
    fun hashIds(): Hashids {
        return Hashids(coreProperties.idSalt, coreProperties.idMinLength)
    }

    @Bean
    fun requestLoggingFilter(): MdcRequestLoggingFilter {
        val loggingFilter = MdcRequestLoggingFilter(authService)
        loggingFilter.setIncludeClientInfo(true)
        loggingFilter.setIncludeQueryString(true)
        loggingFilter.setIncludePayload(false)
        return loggingFilter
    }

    @Bean
    fun taskExecutor(): Executor {
        return Executors.newFixedThreadPool(10);
    }

    @Bean
    fun scheduledExecutorService(): ScheduledExecutorService {
        return Executors.newScheduledThreadPool(1)
    }

}

fun main(args: Array<String>) {
    runApplication<BeApplication>(*args)
}


@Configuration
class CorsConfig {
//    @Bean
//    @Order(Ordered.HIGHEST_PRECEDENCE)
//    fun corsFilterRegistrationBean(): FilterRegistrationBean<*> {
//        val source = UrlBasedCorsConfigurationSource()
//        val config = CorsConfiguration()
//        config.applyPermitDefaultValues()
//        config.allowCredentials = true
//        config.allowedOrigins = Arrays.asList("*")
//        config.allowedHeaders = Arrays.asList("*")
//        config.allowedMethods = Arrays.asList("*")
//        config.exposedHeaders = Arrays.asList("content-length")
//        config.maxAge = 3600L
//        source.registerCorsConfiguration("/**", config)
//        val bean = FilterRegistrationBean(CorsFilter(source))
////        bean.order = 0
//        return bean
//    }

    @Configuration
    @Order(Ordered.HIGHEST_PRECEDENCE)
    class CORSFilter : Filter {

        private var config: FilterConfig? = null

        override fun destroy() {
        }

        override fun doFilter(req: ServletRequest, resp: ServletResponse,
                              chain: FilterChain) {
            val response = resp as HttpServletResponse
            val request = req as HttpServletRequest
//            response.setHeader("Access-Control-Allow-Origin", "*")
            val origin = req.getHeader("Origin") ?: "*"
            response.setHeader("Access-Control-Allow-Origin", origin)
            response.setHeader("Access-Control-Allow-Credentials", "true")
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE")
            response.setHeader("Access-Control-Max-Age", "3600")
            response.setHeader("Access-Control-Allow-Headers", "x-requested-with, authorization, Content-Type, Authorization, credential, X-XSRF-TOKEN, platforms")

            if ("OPTIONS" == (request.method)) {
                response.status = HttpServletResponse.SC_OK
            } else {
                chain.doFilter(req, resp)
            }
        }

        override fun init(filterConfig: FilterConfig) {
            config = filterConfig
        }
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder(8)
    }
}


@Configuration
@ConfigurationProperties(prefix = "file")
class FileStorageProperties {
    lateinit var uploadDir: String
}

@Configuration
@ConfigurationProperties(prefix = "image")
class ImageProperties {
    var maxWidth: Int = 0
    var maxHeight: Int = 0
    var profileMaxWidth: Int = 0
    var profileMaxHeight: Int = 0
    lateinit var host: String
}

@Configuration
@ConfigurationProperties(prefix = "websocket")
class WebSocketProperties {
    lateinit var relayHost: String
    var relayPort: Int = 61617
    lateinit var clientLogin: String
    lateinit var clientPasscode: String
}

@Configuration
@ConfigurationProperties(prefix = "push")
class PushProperties {
    lateinit var secret: String
    lateinit var appId: String
    lateinit var itInvitationContent: String
    lateinit var enInvitationContent: String
    lateinit var itInvitationTitle: String
    lateinit var enInvitationTitle: String
}

@Configuration
@ConfigurationProperties(prefix = "core")
class CoreProperties {
    lateinit var idSalt: String
    var idMinLength: Int = 8
    var fake: Boolean = false
    lateinit var watchUserEmail: String
    var maxDistance: Int = 5000;
    var rewardAmount: Int = 5
    var rewardDuration: Int = 1440
    var admobRewardAmount: Int = 5
    var admobMax: Int = 3
    var admobRewardDuration: Int = 1440
    var rewardAmountForInvitation: Int = 5
    lateinit var androidInAppPurchaseAccountFilePath: String
    lateinit var mobileAppName: String
    lateinit var mobileAppPackage: String
    lateinit var iosInAppPurchaseValidationUrl: String
}

@Configuration
class SchedulerConfig : SchedulingConfigurer {
    override fun configureTasks(taskRegistrar: ScheduledTaskRegistrar) {
        val threadPoolTaskScheduler = ThreadPoolTaskScheduler()
        threadPoolTaskScheduler.poolSize = 2
        threadPoolTaskScheduler.setThreadNamePrefix("ScheduledTask-")
        threadPoolTaskScheduler.initialize()
        taskRegistrar.setTaskScheduler(threadPoolTaskScheduler)
    }
}