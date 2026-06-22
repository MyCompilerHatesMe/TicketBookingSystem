package in.gov.cgg.ticketbookingsystem.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TripRequest (
        @Positive(message = "[TripRequest]: busId must be positive")
        int busId,
        @Positive(message = "[TripRequest]: routeId must be positive")
        int routeId,
        @NotNull(message = "[TripRequest]: startTime must not be null")
        LocalDateTime startTime,
        @NotNull(message = "[TripRequest]: arrivalTime must not be null")
        LocalDateTime arrivalTime,
        @Positive(message = "[TripRequest]: fare must be positive")
        BigDecimal fare
)
{}
