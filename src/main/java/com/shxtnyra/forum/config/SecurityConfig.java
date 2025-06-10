package com.shxtnyra.forum.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class  SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized");
                        })
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v1/auth/**").permitAll()
                        // Users
                        .requestMatchers(
                                "/v1/users/me**"  // Все /me/... пути требуют авторизации
                        ).authenticated()
                        .requestMatchers(
                                "/v1/users/list",
                                "/v1/users",
                                "/v1/users/{id}",
                                "/v1/users/find"
                        ).permitAll()

                        // Test
                        .requestMatchers("/v1/test/public").permitAll()
                        .requestMatchers("v1/test/authenticated").authenticated()
                        .requestMatchers("/v1/test/moderator").hasRole("MODERATOR")

                        .requestMatchers(HttpMethod.GET, "/v1/posts/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/v1/posts").hasAnyRole("USER", "MODERATOR", "ADMIN")

                        .requestMatchers(HttpMethod.GET, "/v1/comments/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/v1/comments/add").authenticated()

                        .requestMatchers("/v1/administration/**").hasAnyRole("ADMIN", "MODERATOR")
                        .requestMatchers("/v1/moderation/**").hasAnyRole("MODERATOR", "ADMIN")
                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}