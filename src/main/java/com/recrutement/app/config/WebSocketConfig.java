package com.recrutement.app.config;

import com.recrutement.app.config.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private JwtUtils jwtUtils;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-notifications")
                .setAllowedOriginPatterns("*")
                .withSockJS();
        registry.addEndpoint("/ws-messagerie")
                .setAllowedOriginPatterns("*")
                .withSockJS();
        registry.addEndpoint("/ws-candidatures")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Configurer les destinations où les messages seront publiés
        registry.enableSimpleBroker(
                "/topic/",  // Utiliser un préfixe plus générique
                "/queue/",  // Ajouter ce préfixe pour les messages privés
                "/user/"    // Conserver ce préfixe pour les messages utilisateur
        );
        
        // Configurer les préfixes pour les destinations où les clients envoient des messages
        registry.setApplicationDestinationPrefixes("/app", "/msg", "/candidature");
        
        // Configurer le préfixe pour les messages utilisateur
        registry.setUserDestinationPrefix("/user");
    }
    
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                
                if (accessor != null) {
                    // Vérifier l'authentification pour toutes les commandes, pas seulement CONNECT
                    if (StompCommand.CONNECT.equals(accessor.getCommand()) || 
                        StompCommand.SUBSCRIBE.equals(accessor.getCommand()) ||
                        StompCommand.SEND.equals(accessor.getCommand())) {
                        
                        String authHeader = accessor.getFirstNativeHeader("Authorization");
                        if (authHeader != null && authHeader.startsWith("Bearer ")) {
                            String token = authHeader.substring(7);
                            
                            if (jwtUtils.validateJwtToken(token)) {
                                String username = jwtUtils.getUserNameFromJwtToken(token);
                                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                                
                                Authentication authentication = new UsernamePasswordAuthenticationToken(
                                        userDetails, null, userDetails.getAuthorities());
                                
                                accessor.setUser(authentication);
                                System.out.println("WebSocket authentifié pour l'utilisateur: " + username);
                            }
                        }
                    }
                }
                
                return message;
            }
        });
    }
}
