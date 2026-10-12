package com.luxora.hotel.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.luxora.hotel.dto.AuthDto;
import com.luxora.hotel.entity.Customer;
import com.luxora.hotel.model.UserRole;
import com.luxora.hotel.repository.CustomerRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;
    private final SessionAuthenticationStrategy sessionAuthenticationStrategy;

    public AuthController(CustomerRepository customerRepository,
                          PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager,
                          SecurityContextRepository securityContextRepository,
                          SessionAuthenticationStrategy sessionAuthenticationStrategy) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.securityContextRepository = securityContextRepository;
        this.sessionAuthenticationStrategy = sessionAuthenticationStrategy;
    }

    @GetMapping("/csrf")
    public AuthDto.CsrfResponse csrf(CsrfToken token) {
        return new AuthDto.CsrfResponse(token.getToken(), token.getHeaderName());
    }

    @PostMapping("/register")
    public ResponseEntity<AuthDto.UserResponse> register(@Valid @RequestBody AuthDto.RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (customerRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Customer customer = new Customer(request.firstName().trim(), request.lastName().trim(), email,
                request.phone(), request.address());
        customer.setPasswordHash(passwordEncoder.encode(request.password()));
        customer.setRole(UserRole.CUSTOMER);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AuthDto.UserResponse.from(customerRepository.save(customer)));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthDto.LoginRequest request,
                                   HttpServletRequest servletRequest,
                                   HttpServletResponse servletResponse) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email().trim().toLowerCase(), request.password()));
            sessionAuthenticationStrategy.onAuthentication(authentication, servletRequest, servletResponse);
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, servletRequest, servletResponse);

            Customer customer = customerRepository.findByEmail(authentication.getName()).orElseThrow();
            return ResponseEntity.ok(AuthDto.UserResponse.from(customer));
        } catch (AuthenticationException exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid email or password"));
        }
    }

    @GetMapping("/me")
    public AuthDto.UserResponse currentUser(Authentication authentication) {
        Customer customer = customerRepository.findByEmail(authentication.getName()).orElseThrow();
        return AuthDto.UserResponse.from(customer);
    }
}