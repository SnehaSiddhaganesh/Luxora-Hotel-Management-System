package com.luxora.hotel.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.session.ChangeSessionIdAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/index.html", "/login.html", "/contact.html", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/api/auth/csrf", "/api/auth/register", "/api/auth/login").permitAll()
                        .requestMatchers("/admin", "/admin.html").hasAnyRole("ADMIN", "RECEPTIONIST")
                        .requestMatchers("/dashboard", "/dashboard.html").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.GET, "/api/rooms/**", "/api/food-items/**", "/api/hotel-services/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/bookings").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.GET, "/api/bookings/mine").hasRole("CUSTOMER")
                        .requestMatchers("/api/customers/**").hasAnyRole("ADMIN", "RECEPTIONIST")
                        .requestMatchers(HttpMethod.GET, "/api/bookings").hasAnyRole("ADMIN", "RECEPTIONIST")
                        .requestMatchers(HttpMethod.PATCH, "/api/bookings/*/status").hasAnyRole("ADMIN", "RECEPTIONIST")
                        .requestMatchers(HttpMethod.DELETE, "/api/bookings/**").hasAnyRole("ADMIN", "RECEPTIONIST")
                        .requestMatchers(HttpMethod.POST, "/api/rooms/**", "/api/food-items/**", "/api/hotel-services/**").hasAnyRole("ADMIN", "RECEPTIONIST")
                        .requestMatchers(HttpMethod.PUT, "/api/rooms/**", "/api/food-items/**", "/api/hotel-services/**").hasAnyRole("ADMIN", "RECEPTIONIST")
                        .requestMatchers(HttpMethod.PATCH, "/api/rooms/**").hasAnyRole("ADMIN", "RECEPTIONIST")
                        .requestMatchers(HttpMethod.DELETE, "/api/rooms/**", "/api/food-items/**", "/api/hotel-services/**").hasAnyRole("ADMIN", "RECEPTIONIST")
                        .requestMatchers(HttpMethod.GET, "/api/payments").hasAnyRole("ADMIN", "RECEPTIONIST")
                        .requestMatchers(HttpMethod.PATCH, "/api/payments/**").hasAnyRole("ADMIN", "RECEPTIONIST")
                        .requestMatchers(HttpMethod.DELETE, "/api/payments/**").hasAnyRole("ADMIN", "RECEPTIONIST")
                        .requestMatchers(HttpMethod.PATCH, "/api/orders/*/status").hasAnyRole("ADMIN", "RECEPTIONIST")
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll())
                    .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .sessionFixation(fixation -> fixation.changeSessionId()))
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessHandler((request, response, authentication) -> response.setStatus(204)))
                .securityContext(context -> context.securityContextRepository(securityContextRepository()));
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new ChangeSessionIdAuthenticationStrategy();
    }
}