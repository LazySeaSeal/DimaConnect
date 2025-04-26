package com.recrutement.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.recrutement.app.security.CustomUserDetailsService;
import com.recrutement.app.security.JwtAuthEntryPoint;
import com.recrutement.app.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtAuthEntryPoint authEntryPoint;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(authEntryPoint)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                // Swagger UI access
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/swagger-resources/**").permitAll()
                .requestMatchers("/webjars/**").permitAll()

                // Public routes
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/entreprises/inscription").permitAll()
                .requestMatchers("/api/entreprises").permitAll()
                .requestMatchers("/api/entreprises/{id}").permitAll()
                .requestMatchers("/api/offres/recherche", "/api/offres/{offreId}").permitAll()
                .requestMatchers("/api/invitations/inscription").permitAll()

                // Ajouter ces routes pour les opérations d'administration d'entreprise
                .requestMatchers("/api/admin/entreprises/{id}/verification").hasRole("ADMIN")
                .requestMatchers("/api/admin/entreprises/{id}/premium").hasRole("ADMIN")
                .requestMatchers("/api/admin/entreprises/{id}/profil").hasRole("ADMIN")

                // Autres routes d'administration
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/entreprises/{id}/verifier").hasRole("ADMIN")

                // Routes existantes...
                .requestMatchers("/api/offres").hasAnyRole("RESPONSABLE_RH", "CHEF_PROJET", "ADMIN")
                .requestMatchers("/api/offres/en-attente").hasAnyRole("RESPONSABLE_RH", "ADMIN")
                .requestMatchers("/api/offres/{offreId}/valider").hasAnyRole("RESPONSABLE_RH", "ADMIN")
                .requestMatchers("/api/offres/{offreId}/rejeter").hasAnyRole("RESPONSABLE_RH", "ADMIN")
                .requestMatchers("/api/invitations").authenticated()
                .requestMatchers("/api/invitations/entreprise/{entrepriseId}").authenticated()

                // Any other request requires authentication
                .anyRequest().authenticated();

        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}