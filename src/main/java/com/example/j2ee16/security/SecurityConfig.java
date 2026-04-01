package com.example.j2ee16.security;

import com.example.j2ee16.constants.ErrorCodeConstants;
import com.example.j2ee16.dto.response.ApiErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ObjectMapper objectMapper;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, ObjectMapper objectMapper) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.objectMapper = objectMapper;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(auth -> auth
                // Public static & auth
                .requestMatchers("/", "/index.html", "/app.js", "/styles.css", "/favicon.ico").permitAll()
                .requestMatchers("/api/v1/auth/register", "/api/v1/auth/login", "/api/v1/auth/refresh").permitAll()
                .requestMatchers("/api/v1/stations", "/api/v1/routes",
                        "/api/v1/trips/*/stops", "/api/v1/trips", "/api/v1/trips/*/seats",
                        "/api/v1/bookings", "/api/v1/payments",
                        "/api/v1/payments/callback", "/api/v1/payments/vnpay/callback",
                        "/api/v1/payments/momo/callback", "/api/v1/provinces").permitAll()
                // Booking actions
                .requestMatchers("/api/v1/bookings/*/confirm-cash").hasAnyRole("STAFF", "ADMIN")
                .requestMatchers("/api/v1/bookings/*/cancel").authenticated()
                // Check-in & passengers (STAFF + DRIVER + ADMIN)
                .requestMatchers("/api/v1/tickets/*/check-in").hasAnyRole("STAFF", "DRIVER", "ADMIN")
                .requestMatchers("/api/v1/trips/*/passengers").hasAnyRole("STAFF", "DRIVER", "ADMIN")
                // STAFF & DRIVER: read-only access to specific admin endpoints
                .requestMatchers(HttpMethod.GET, "/api/v1/admin/trips", "/api/v1/admin/trips/**").hasAnyRole("STAFF", "DRIVER", "ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/v1/admin/trips/*/status").hasAnyRole("STAFF", "DRIVER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/v1/admin/buses", "/api/v1/admin/buses/**").hasAnyRole("STAFF", "DRIVER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/v1/admin/users", "/api/v1/admin/users/**").hasAnyRole("STAFF", "ADMIN")
                // Swagger
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                // All other /admin/** requires ADMIN only
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated());

        http.exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint((request, response, authException) -> {
                    ApiErrorResponse body = new ApiErrorResponse(
                            ErrorCodeConstants.UNAUTHORIZED,
                            HttpStatus.UNAUTHORIZED.value(),
                            "Unauthorized");
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                    response.getWriter().write(objectMapper.writeValueAsString(body));
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    ApiErrorResponse body = new ApiErrorResponse(
                            ErrorCodeConstants.FORBIDDEN,
                            HttpStatus.FORBIDDEN.value(),
                            "Forbidden");
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                    response.getWriter().write(objectMapper.writeValueAsString(body));
                }));

        http.httpBasic(Customizer.withDefaults());
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
