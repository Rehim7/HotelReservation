package com.example.hotelreservationsystem.config;

import com.example.hotelreservationsystem.filter.JwtFilters;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // @PreAuthorize istifadəsi üçün vacibdir
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtFilters jwtFilters;
        private final CorsConfigurationSource corsConfigurationSource; // CorsConfig-dən gəlir

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                // CORS konfiqurasiyası - CorsConfig-dən gələn bean istifadə olunur
                                .cors(cors -> cors.configurationSource(corsConfigurationSource))

                                .csrf(AbstractHttpConfigurer::disable)

                                .authorizeHttpRequests(auth -> auth
                                                // Public endpoint-lər
                                                .requestMatchers(
                                                                "/api/hotelReservationSystem/security/login",
                                                                "/api/hotelReservationSystem/security/register",
                                                                "/api/hotelReservationSystem/security/refresh-token",
                                                                "/v3/api-docs/**",
                                                                "/v3/api-docs.yaml",
                                                                "/swagger-ui/**",
                                                                "/swagger-ui.html",
                                                                "/swagger-resources/**",
                                                                "/webjars/**",
                                                                "/frontend/**",
                                                                "/ws/**")
                                                .permitAll()

                                                // Public GET endpoint-lər
                                                .requestMatchers(HttpMethod.GET,
                                                                "/api/hotelReservationSystem/hotel/getAllHotels",
                                                                "/api/hotelReservationSystem/hotel/getHotelByName/**",
                                                                "/api/hotelReservationSystem/room/findUnreservedRooms/**")
                                                .permitAll()

                                                // OPTIONS sorğularına icazə ver (CORS preflight)
                                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                                                // Bütün digər sorğular authentication tələb edir
                                                .anyRequest().authenticated())

                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                .exceptionHandling(exception -> exception
                                                .authenticationEntryPoint((request, response, authException) -> {
                                                        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                                                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

                                                        Map<String, Object> body = new HashMap<>();
                                                        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
                                                        body.put("error", "Unauthorized");
                                                        body.put("message",
                                                                        "Authentication is required to access this resource");
                                                        body.put("path", request.getServletPath());

                                                        new ObjectMapper().writeValue(response.getOutputStream(), body);
                                                })
                                                .accessDeniedHandler((request, response, accessDeniedException) -> {
                                                        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                                                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

                                                        Map<String, Object> body = new HashMap<>();
                                                        body.put("status", HttpServletResponse.SC_FORBIDDEN);
                                                        body.put("error", "Forbidden");
                                                        body.put("message",
                                                                        "You don't have permission to access this resource");
                                                        body.put("path", request.getServletPath());

                                                        new ObjectMapper().writeValue(response.getOutputStream(), body);
                                                }))

                                .addFilterBefore(jwtFilters, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}