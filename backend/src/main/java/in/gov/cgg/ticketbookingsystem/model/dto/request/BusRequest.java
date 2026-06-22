package in.gov.cgg.ticketbookingsystem.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record BusRequest (
        @NotBlank(message = "[Bus Request]: bus number must not be blank")
        String busNumber,
        @NotBlank(message = "[Bus Request]: bus name must not be blank")
        String busName,
        @NotBlank(message = "[Bus Request]: Bus type must not be blank")
        String busType,
        @Positive(message = "[Bus Request]: total seats must be a positive integer")
        int totalSeats
) {}
