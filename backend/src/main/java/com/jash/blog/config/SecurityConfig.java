package com.jash.blog.config;


import com.jash.blog.domain.entities.User;
import com.jash.blog.repository.UserRepository;
import com.jash.blog.security.BlogUserDetailService;
import com.jash.blog.security.JwtAuthenticationFilter;
import com.jash.blog.service.AuthenticationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(AuthenticationService authenticationService) {
        return new JwtAuthenticationFilter(authenticationService);
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository  userRepository) {
        BlogUserDetailService blogUserDetailService = new BlogUserDetailService(userRepository);

        String email = "user@test.com";
        userRepository.findByEmail(email).orElseGet(()->
        {
            User newUser = User.builder()
                    .name("Test user")
                    .email(email)
                    .password(passwordEncoder().encode("password"))
                    .build();
            return userRepository.save(newUser);
        });
        return blogUserDetailService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/posts/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/categories/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/tags/**").permitAll()
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
                ).addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

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
