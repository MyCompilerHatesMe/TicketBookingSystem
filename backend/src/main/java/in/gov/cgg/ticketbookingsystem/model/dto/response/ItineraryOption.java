package in.gov.cgg.ticketbookingsystem.model.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record ItineraryOption(
    List<TripResponse> trips,
    BigDecimal totalFare,
    int totalDistance
) {}
