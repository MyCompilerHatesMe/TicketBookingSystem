package in.gov.cgg.ticketbookingsystem.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RouteStopRequest(
    @NotBlank(message = "Stop name is required")
    String stopName,

    @NotBlank(message = "Stop type is required")
    String stopType,

    @NotNull(message = "Minutes offset is required")
    Integer minutesOffset,

    @NotNull(message = "Sequence is required")
    Integer sequence
) {}
