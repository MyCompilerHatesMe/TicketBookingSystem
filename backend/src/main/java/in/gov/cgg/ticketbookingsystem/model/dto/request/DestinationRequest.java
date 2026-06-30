package in.gov.cgg.ticketbookingsystem.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record DestinationRequest(
    @NotBlank(message = "City name cannot be blank") String cityName,
    LocalDate arrivalDate
) {}
