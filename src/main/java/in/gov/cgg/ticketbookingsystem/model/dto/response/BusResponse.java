package in.gov.cgg.ticketbookingsystem.model.dto.response;

import in.gov.cgg.ticketbookingsystem.model.core.Seat;

import java.util.List;

public record BusResponse (
        String busNumber,
        String busName,
        String busType,
        int totalSeats,
        List<Seat> seats
) {}
