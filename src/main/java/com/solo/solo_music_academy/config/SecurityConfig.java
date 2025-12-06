package com.solo.solo_music_academy.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Dùng CORS config bên dưới
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sess ->
                    sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                // Cho phép preflight
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // ===== PUBLIC ENDPOINTS =====
                .requestMatchers(
                        "/",
                        "/health",
                        "/actuator/health",
                        "/auth/login",
                        "/ws/**"
                ).permitAll()

                // ===== ADMIN ATTENDANCE (ĐIỂM DANH & DẠY BÙ) =====
                .requestMatchers("/admin/attendance/**")
                    .hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_TEACHER")

                // ===== GÓI HỌC / LEADS =====
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

                // ===== PROFILE & CÁC API CÒN LẠI =====
                .requestMatchers("/profile/**").authenticated()
                .anyRequest().authenticated()
            );

        // Thêm JWT filter trước UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);

        // Cho phép Frontend gọi tới Backend
        config.setAllowedOrigins(List.of(
                "http://localhost:5173",                    // Vite dev
                "https://solo-music-frontend.onrender.com"  // URL frontend trên Render
        ));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
