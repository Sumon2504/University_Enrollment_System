package com.cognizant.uams.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.cognizant.uams.security.BearerTokenFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, BearerTokenFilter bearerTokenFilter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {})
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/faculty/**").hasAnyRole("ADMIN", "FACULTY")
                        .requestMatchers("/api/faculty/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/grades/**").hasAnyRole("FACULTY", "STUDENT", "ADMIN")
                        .requestMatchers("/api/grades/**").hasRole("FACULTY")
                        .requestMatchers(HttpMethod.POST, "/api/courses/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/courses/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/courses/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/students/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/students/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/students/**").hasAnyRole("ADMIN", "FACULTY", "STUDENT")
                        .requestMatchers(HttpMethod.PUT, "/api/students/**").hasAnyRole("ADMIN", "STUDENT")
                        .requestMatchers("/api/enrollments/**").hasAnyRole("ADMIN", "STUDENT", "FACULTY")
                        .requestMatchers(HttpMethod.GET, "/api/courses/**").authenticated()
                        .requestMatchers("/api/transcripts/**").hasAnyRole("STUDENT", "FACULTY", "ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(bearerTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
