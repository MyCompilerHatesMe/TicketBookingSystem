package in.gov.cgg.ticketbookingsystem.model.dto.response;

import java.time.LocalDateTime;

public record RouteStopResponse(
    Long routeStopId,
    String stopName,
    String stopType,
    Integer minutesOffset,
    Integer sequence,
    LocalDateTime stopTime
) {}
