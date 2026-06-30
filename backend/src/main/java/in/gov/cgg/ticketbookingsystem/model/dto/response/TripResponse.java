package in.gov.cgg.ticketbookingsystem.model.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record TripResponse (
        Long tripId,
        Long busId,
        String busNumber,
        String busName,
        String busType,
        Long routeId,
        String sourceCity,
        String destinationCity,
        LocalDateTime startTime,
        LocalDateTime arrivalTime,
        BigDecimal fare,
        List<RouteStopResponse> stops
)
{}
