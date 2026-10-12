package com.luxora.hotel.dto;

import com.luxora.hotel.entity.Customer;
import com.luxora.hotel.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class AuthDto {
    private AuthDto() {
    }

    public record RegisterRequest(
            @NotBlank @Size(max = 100) String firstName,
            @NotBlank @Size(max = 100) String lastName,
            @NotBlank @Email @Size(max = 150) String email,
            @Size(max = 30) String phone,
            @Size(max = 255) String address,
            @NotBlank @Size(min = 12, max = 72) String password
    ) {
    }

    public record LoginRequest(
            @NotBlank @Email @Size(max = 150) String email,
            @NotBlank @Size(max = 72) String password
    ) {
    }

    public record UserResponse(Long id, String firstName, String lastName, String email, UserRole role) {
        public static UserResponse from(Customer customer) {
            return new UserResponse(customer.getId(), customer.getFirstName(), customer.getLastName(), customer.getEmail(), customer.getRole());
        }
    }

    public record CsrfResponse(String token, String headerName) {
    }
}