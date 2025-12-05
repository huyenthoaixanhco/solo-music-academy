package com.solo.solo_music_academy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // prefix cho những channel FE subscribe
        config.enableSimpleBroker("/topic");

        // prefix cho những endpoint FE SEND lên server
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint để FE connect WebSocket
        registry.addEndpoint("/ws-chat")
                .setAllowedOriginPatterns("*") // dev cho dễ, prod thì lock domain
                .withSockJS();                 // có/không SockJS tuỳ m
    }
}
