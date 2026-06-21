package in.gov.cgg.ticketbookingsystem.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record RouteRequest (
        @NotBlank(message = "[TripRequest]: Source City cannot be blank")
        String sourceCity,
        @NotBlank(message = "[TripRequest]: Destination City cannot be blank")
        String destCity,
        @Positive(message = "[TripRequest]: Distance between source and destination must be positive")
        int distanceKm
)
{}
