package in.gov.cgg.ticketbookingsystem.model.dto.response;

import in.gov.cgg.ticketbookingsystem.model.SimpleStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record BookingHistoryResponse(
    Long bookingId,
    UUID uuid,
    BigDecimal totalAmount,
    SimpleStatus status,
    LocalDateTime bookingTime,
    LocalDateTime expiryTime,
    Long tripId,
    String sourceCity,
    String destinationCity,
    LocalDateTime startTime,
    LocalDateTime arrivalTime,
    String busName,
    String busNumber,
    List<String> seatNumbers,
    String boardingStopName,
    String droppingStopName
) {}
