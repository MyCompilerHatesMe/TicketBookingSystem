package in.gov.cgg.ticketbookingsystem.service;

import in.gov.cgg.ticketbookingsystem.model.dto.response.TripResponse;
import in.gov.cgg.ticketbookingsystem.model.dto.response.TripSeatResponse;
import in.gov.cgg.ticketbookingsystem.repository.TripScheduleRepo;
import in.gov.cgg.ticketbookingsystem.repository.TripSeatRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TripService {

    private final TripScheduleRepo tripScheduleRepo;
    private final TripSeatRepo tripSeatRepo;

    public List<TripResponse> searchTrips(String sourceCity, String destinationCity, LocalDate date) {
        // Only calculate date boundaries if a date is actually provided
        LocalDateTime startOfDay = (date != null) ? date.atStartOfDay() : null;
        LocalDateTime endOfDay = (date != null) ? startOfDay.plusDays(1) : null;

        return tripScheduleRepo.findTripsWithOptionalFilters(sourceCity, destinationCity, startOfDay, endOfDay)
                .stream()
                .map(trip -> new TripResponse(
                        trip.getTripId(),
                        trip.getBus().getBusId(),
                        trip.getBus().getBusNumber(),
                        trip.getBus().getBusName(),
                        trip.getBus().getBusType(),
                        trip.getRoute().getRouteId(),
                        trip.getRoute().getSourceCity(),
                        trip.getRoute().getDestinationCity(),
                        trip.getStartTime(),
                        trip.getArrivalTime(),
                        trip.getFare()
                ))
                .toList();
    }

    public List<TripSeatResponse> getSeatsForTrip(Long tripId) {
        return tripSeatRepo.findByTripScheduleTripId(tripId)
                .stream()
                .map(tripSeat -> new TripSeatResponse(
                        tripSeat.getSeat().getSeatId(),
                        tripSeat.getSeat().getSeatNumber(),
                        tripSeat.getStatus()
                ))
                .toList();
    }
}
