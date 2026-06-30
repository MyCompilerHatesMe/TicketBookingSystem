package in.gov.cgg.ticketbookingsystem.model.dto.response;

import in.gov.cgg.ticketbookingsystem.model.SimpleStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record BookingResponse(
    Long bookingId,
    UUID uuid,
    BigDecimal totalAmount,
    SimpleStatus status,
    LocalDateTime expiryTime,
    String boardingStopName,
    String droppingStopName
) {}
