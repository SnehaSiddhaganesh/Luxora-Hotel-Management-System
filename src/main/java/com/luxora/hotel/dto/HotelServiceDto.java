package com.luxora.hotel.dto;

import java.math.BigDecimal;

import com.luxora.hotel.entity.HotelService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class HotelServiceDto {

    public record CreateRequest(
            @NotBlank(message = "Service name is required") String name,
            String description,
            @NotNull(message = "Base price is required") @Positive(message = "Base price must be greater than zero") BigDecimal basePrice
    ) {
    }

    public record UpdateRequest(
            @NotBlank(message = "Service name is required") String name,
            String description,
            @NotNull(message = "Base price is required") @Positive(message = "Base price must be greater than zero") BigDecimal basePrice
    ) {
    }

    public record Response(
            Long id,
            String name,
            String description,
            BigDecimal basePrice
    ) {
        public static Response fromEntity(HotelService hotelService) {
            if (hotelService == null) {
                return null;
            }

            return new Response(
                    hotelService.getId(),
                    hotelService.getName(),
                    hotelService.getDescription(),
                    hotelService.getBasePrice()
            );
        }
    }
}
