package com.icebreaker.be

import com.icebreaker.be.service.auth.social.SocialTokenGranter
import com.icebreaker.be.user.UserService
import com.icebreaker.be.user.impl.UserServiceDefault
import com.icebreaker.be.user.social.SocialService
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpMethod
import org.springframework.messaging.simp.config.MessageBrokerRegistry
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
import org.springframework.security.oauth2.provider.ClientDetailsService
import org.springframework.security.oauth2.provider.CompositeTokenGranter
import org.springframework.security.oauth2.provider.OAuth2RequestFactory
import org.springframework.security.oauth2.provider.TokenGranter
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer
import java.util.*
import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.sql.DataSource

@SpringBootApplication
@EnableConfigurationProperties(FileStorageProperties::class)
@EnableTransactionManagement
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

@Configuration
@EnableAuthorizationServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
class AuthServerOAuth2Config(val userDetailsService: UserDetailsService,
                             val dataSource: DataSource,
                             val authenticationManager: AuthenticationManager,
                             val userService: UserService,
                             val socialService: SocialService,
                             val clientDetailsService: ClientDetailsService) : AuthorizationServerConfigurerAdapter() {
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
        endpoints.tokenGranter(tokenGranter(endpoints))
    }

    private fun tokenGranter(endpoints: AuthorizationServerEndpointsConfigurer): TokenGranter {
        val granters = ArrayList<TokenGranter>(Arrays.asList(endpoints.tokenGranter))
        granters.add(SocialTokenGranter(socialService, userService, endpoints.tokenServices, endpoints.clientDetailsService, endpoints.oAuth2RequestFactory))
        return CompositeTokenGranter(granters)
    }

    @Bean
    fun requestFactory(): OAuth2RequestFactory {
        return DefaultOAuth2RequestFactory(clientDetailsService)
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

        val CREDENTIALS_NAME = "Access-Control-Allow-Credentials"
        val ORIGIN_NAME = "Access-Control-Allow-Origin"
        val METHODS_NAME = "Access-Control-Allow-Methods"
        val HEADERS_NAME = "Access-Control-Allow-Headers"
        val MAX_AGE_NAME = "Access-Control-Max-Age"

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
@EnableWebSocketMessageBroker
class WebSocketConfig : WebSocketMessageBrokerConfigurer {

    override fun configureMessageBroker(config: MessageBrokerRegistry) {
        config.enableSimpleBroker("/chat")
        config.setApplicationDestinationPrefixes("/app")
//        config.enableSimpleBroker("/topic", "/queue")
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/chat-websocket").setAllowedOrigins("*").withSockJS()
    }
}


