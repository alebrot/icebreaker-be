package com.icebreaker.be.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@Profile("prod")
@EnableWebSocketMessageBroker
class WebSocketConfig : WebSocketMessageBrokerConfigurer {
    override fun configureMessageBroker(config: MessageBrokerRegistry) {
        config.enableStompBrokerRelay("/chat")
                .setRelayHost("localhost")
                .setRelayPort(61613)
                .setClientLogin("admin")
                .setClientPasscode("admin");
        config.setApplicationDestinationPrefixes("/app");
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/chat-websocket").setAllowedOrigins("*").withSockJS()
    }
}

@Configuration
@Profile("!prod")
@EnableWebSocketMessageBroker
class WebSocketConfigInMemory : WebSocketMessageBrokerConfigurer {
    override fun configureMessageBroker(config: MessageBrokerRegistry) {
        config.enableSimpleBroker("/chat")
        config.setApplicationDestinationPrefixes("/app")
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/chat-websocket").setAllowedOrigins("*").withSockJS()
    }
}