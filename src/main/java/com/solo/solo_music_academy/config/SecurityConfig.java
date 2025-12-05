package com.solo.solo_music_academy.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sess ->
                    sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                // Allow Preflight (CORS)
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Public endpoints
                .requestMatchers("/health", "/auth/login", "/ws/**").permitAll()

                // ===== ADMIN ATTENDANCE (ĐIỂM DANH & DẠY BÙ) =====
                // Dùng hasAuthority để khớp 100% với DB: ROLE_SUPER_ADMIN, ROLE_TEACHER
                .requestMatchers("/admin/attendance/**")
                    .hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_TEACHER")

                // ===== GÓI HỌC =====
                .requestMatchers("/admin/packages/**")
                    .hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_SUPPORT")
                .requestMatchers("/admin/leads/**")
                    .hasAnyAuthority("ROLE_SUPER_ADMIN")

                // ===== CÁC ADMIN KHÁC =====
                .requestMatchers("/admin/**")
                    .hasAuthority("ROLE_SUPER_ADMIN")

                // ===== SUPPORT =====
                .requestMatchers("/support/**")
                    .hasAnyAuthority("ROLE_SUPPORT", "ROLE_SUPER_ADMIN")

                // ===== STUDENT =====
                .requestMatchers("/student/**")
                    .hasAuthority("ROLE_STUDENT")

                // ===== PROFILE & CÒN LẠI =====
                .requestMatchers("/profile/**").authenticated()
                .anyRequest().authenticated()
            );

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173")); // Frontend URL
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}