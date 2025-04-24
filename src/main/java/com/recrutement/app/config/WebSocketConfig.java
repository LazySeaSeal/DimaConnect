package com.recrutement.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Points d'accès existants
        registry.addEndpoint("/ws-notifications")
                .setAllowedOriginPatterns("*")
                .withSockJS();
        
        registry.addEndpoint("/ws-messagerie")
                .setAllowedOriginPatterns("*")
                .withSockJS();
                
        // Nouveau point d'accès pour les candidatures
        registry.addEndpoint("/ws-candidatures")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Configuration existante
        registry.enableSimpleBroker(
            "/topic/notifications",
            "/topic/conversations",
            // Nouveau topic pour les candidatures
            "/topic/candidatures",
            "/topic/entreprise-notifications"
        );
        
        registry.setApplicationDestinationPrefixes(
            "/app",
            "/msg",
            // Nouveau préfixe pour les candidatures
            "/candidature"
        );
    }
}