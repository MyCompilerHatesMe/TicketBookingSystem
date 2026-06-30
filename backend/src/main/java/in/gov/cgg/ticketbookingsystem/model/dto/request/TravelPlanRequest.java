package in.gov.cgg.ticketbookingsystem.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public record TravelPlanRequest(
    @NotBlank(message = "Start city cannot be blank") String startCity,
    LocalDate startDate,
    @NotEmpty(message = "Destinations list cannot be empty") List<DestinationRequest> destinations,
    boolean flexibleOrder
) {}
