package in.gov.cgg.ticketbookingsystem.model.dto.response;

import in.gov.cgg.ticketbookingsystem.model.SeatStatus;

public record TripSeatResponse(
    Long seatId,
    String seatNumber,
    SeatStatus status
) {}
