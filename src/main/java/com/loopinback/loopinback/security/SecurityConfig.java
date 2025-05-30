package com.loopinback.loopinback.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.loopinback.loopinback.security.filter.JwtAuthenticationFilter;
import com.loopinback.loopinback.security.filter.JwtAuthorizationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        private final UserDetailsService userDetailsService;

        public SecurityConfig(UserDetailsService userDetailsService) {
                this.userDetailsService = userDetailsService;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationConfiguration authConfig)
                        throws Exception {
                return http
                                .csrf(csrf -> csrf.disable())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth

                                                .requestMatchers("/api/users/register", "/api/login").permitAll()
                                                // Permitir solo GET para consultas de eventos públicas
                                                .requestMatchers(HttpMethod.GET, "/api/events", "/api/events/{id}")
                                                .permitAll()

                                                .requestMatchers(HttpMethod.POST, "/api/events/create").authenticated()
                                                .requestMatchers(HttpMethod.PUT, "/api/events/{id}/edit")
                                                .authenticated()
                                                .requestMatchers(HttpMethod.DELETE, "/api/events/{id}/delete")
                                                .authenticated()

                                                .requestMatchers("/api/attendances/**").authenticated()
                                                .requestMatchers("/api/users/me/**").authenticated()

                                                .anyRequest().authenticated())
                                .addFilterBefore(new JwtAuthenticationFilter(authenticationManager(authConfig)),
                                                UsernamePasswordAuthenticationFilter.class)
                                .addFilterBefore(new JwtAuthorizationFilter(userDetailsService),
                                                UsernamePasswordAuthenticationFilter.class)
                                .build();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig)
                        throws Exception {
                return authConfig.getAuthenticationManager();
        }

        @Bean
        @Primary
        public BCryptPasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}