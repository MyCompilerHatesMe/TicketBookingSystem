package in.gov.cgg.ticketbookingsystem.model.dto.request;

import java.util.List;

public record BookingRequest(
    Long tripId,
    List<Long> seatIds,
    Boolean isGuest,
    GuestInfo guestInfo,
    Long userId,
    Boolean bypassAccountCheck
) {}
