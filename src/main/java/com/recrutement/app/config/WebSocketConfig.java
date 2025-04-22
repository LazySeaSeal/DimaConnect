package com.recrutement.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint pour SockJS
        registry.addEndpoint("/ws-notifications")
                .setAllowedOriginPatterns("*")  // accepte toutes les origines
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Pour recevoir des messages côté client
        registry.enableSimpleBroker("/topic");

        // Pour envoyer des messages côté serveur
        registry.setApplicationDestinationPrefixes("/app");
    }
}
