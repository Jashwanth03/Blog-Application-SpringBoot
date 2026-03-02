package com.jash.blog.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET,"/api/v1/categories").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/v1/categories/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/v1/tags/**").permitAll()
                        .anyRequest().authenticated()
                )
                /*
                        | `GET /categories → permitAll()` | Lobby is open to everyone |
                        | `GET /tags → permitAll()` | Reading room is open to everyone |
                        | `anyRequest().authenticated()` | All other rooms need a key card |
                        So anyone can **read** categories/tags, but creating/deleting requires login.
                */
                .csrf(csrf -> csrf.disable())
                // CSRF is a web attack where evil-site.com tricks
                // your browser into making requests to your-bank.com
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        //encrypts password --> Uses BCrypt default
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) {
        return config.getAuthenticationManager();
    }

    /*
    Request comes in
      ↓
    SecurityFilterChain checks:
      ↓
    Is it GET /categories or /tags?
    → YES → Let them in (permitAll)
    → NO  → Are they authenticated?
                → YES → Let them in
                → NO  → 401 Unauthorized
     */

}
