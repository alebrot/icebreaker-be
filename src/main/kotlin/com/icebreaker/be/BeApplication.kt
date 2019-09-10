package com.icebreaker.be

import com.icebreaker.be.extra.LoggingRequestInterceptor
import org.hashids.Hashids
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpMethod
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.web.client.RestTemplate
import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.web.filter.CommonsRequestLoggingFilter

@EnableAsync
@SpringBootApplication
@EnableConfigurationProperties(FileStorageProperties::class, ImageProperties::class, PushProperties::class, CoreProperties::class)
@EnableTransactionManagement
class BeApplication(val coreProperties: CoreProperties) {
    @Bean
    fun restTemplate(): RestTemplate {
        val restTemplate = RestTemplate()
        restTemplate.interceptors.add(LoggingRequestInterceptor())
        return restTemplate
    }

    @Bean
    fun hashIds(): Hashids {
        return Hashids(coreProperties.idSalt, coreProperties.idMinLength)
    }

    @Bean
    fun requestLoggingFilter(): CommonsRequestLoggingFilter {
        val loggingFilter = CommonsRequestLoggingFilter()
        loggingFilter.setIncludeClientInfo(true)
        loggingFilter.setIncludeQueryString(true)
        loggingFilter.setIncludePayload(false)
        return loggingFilter
    }

}

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
            response.setHeader("Access-Control-Allow-Headers", "x-requested-with, authorization, Content-Type, Authorization, credential, X-XSRF-TOKEN")

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
}