package com.luxora.hotel.dto;

import java.util.List;

import com.luxora.hotel.entity.Customer;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class CustomerDto {

    public record CreateRequest(
            @NotBlank(message = "First name is required") String firstName,
            @NotBlank(message = "Last name is required") String lastName,
            @NotBlank(message = "Email is required") @Email(message = "Email should be valid") String email,
            String phone,
            String address
    ) {
    }

    public record UpdateRequest(
            @NotBlank(message = "First name is required") String firstName,
            @NotBlank(message = "Last name is required") String lastName,
            @NotBlank(message = "Email is required") @Email(message = "Email should be valid") String email,
            String phone,
            String address
    ) {
    }

    public record Response(
            Long id,
            String firstName,
            String lastName,
            String email,
            String phone,
            String address,
            List<Long> bookingIds,
            List<Long> orderIds
    ) {
        public static Response fromEntity(Customer customer) {
            if (customer == null) {
                return null;
            }

            List<Long> bookingIds = customer.getBookings() == null
                    ? List.of()
                    : customer.getBookings().stream().filter(b -> b != null && b.getId() != null).map(b -> b.getId()).toList();

            List<Long> orderIds = customer.getOrders() == null
                    ? List.of()
                    : customer.getOrders().stream().filter(o -> o != null && o.getId() != null).map(o -> o.getId()).toList();

            return new Response(
                    customer.getId(),
                    customer.getFirstName(),
                    customer.getLastName(),
                    customer.getEmail(),
                    customer.getPhone(),
                    customer.getAddress(),
                    bookingIds,
                    orderIds
            );
        }
    }
}
