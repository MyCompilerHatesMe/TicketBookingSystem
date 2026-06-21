package in.gov.cgg.ticketbookingsystem.service;

import in.gov.cgg.ticketbookingsystem.exception.BusNotFoundException;
import in.gov.cgg.ticketbookingsystem.exception.RouteNotFoundException;
import in.gov.cgg.ticketbookingsystem.exception.TooManySeatsException;
import in.gov.cgg.ticketbookingsystem.model.core.Bus;
import in.gov.cgg.ticketbookingsystem.model.core.Route;
import in.gov.cgg.ticketbookingsystem.model.core.Seat;
import in.gov.cgg.ticketbookingsystem.model.dto.request.BusRequest;
import in.gov.cgg.ticketbookingsystem.model.dto.request.RouteRequest;
import in.gov.cgg.ticketbookingsystem.model.dto.request.TripRequest;
import in.gov.cgg.ticketbookingsystem.model.dto.response.BusResponse;
import in.gov.cgg.ticketbookingsystem.model.dto.response.RouteResponse;
import in.gov.cgg.ticketbookingsystem.model.dto.response.TripResponse;
import in.gov.cgg.ticketbookingsystem.model.operations.TripSchedule;
import in.gov.cgg.ticketbookingsystem.repository.BusRepo;
import in.gov.cgg.ticketbookingsystem.repository.RouteRepo;
import in.gov.cgg.ticketbookingsystem.repository.SeatRepo;
import in.gov.cgg.ticketbookingsystem.repository.TripScheduleRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import in.gov.cgg.ticketbookingsystem.utility.DtoMapper;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final BusRepo busRepo;
    private final SeatRepo seatRepo;
    private final RouteRepo routeRepo;
    private final TripScheduleRepo tripScheduleRepo;
    private final DtoMapper dtoMapper;

    public BusResponse addBus (BusRequest busRequest) {

        if (busRequest.totalSeats() > 52)
            throw new TooManySeatsException(String.valueOf(busRequest.totalSeats()));

        Bus bus = dtoMapper.toEntity(busRequest);
        Bus savedBus = busRepo.save(bus);

        List<Seat> seats = new ArrayList<>();
        int seatsPerRow = 2;

        for (int i = 0; i < savedBus.getTotalSeats(); i++) {
            char rowLetter = (char) ('A' + i / seatsPerRow);
            int seatNum = (i % seatsPerRow) + 1;

            String seatNumber = "" + rowLetter + seatNum;

            Seat seat = new Seat();
            seat.setBus(savedBus);
            seat.setSeatNumber(seatNumber);
            seats.add(seat);
        }

        List<Seat> savedSeats = seatRepo.saveAll(seats);

        return dtoMapper.toResponse(savedBus, savedSeats);
    }

    public RouteResponse addRoute (RouteRequest routeRequest) {
        Route route = dtoMapper.toEntity(routeRequest);
        return dtoMapper.toResponse(routeRepo.save(route));
    }

    public TripResponse addTrip (TripRequest tripRequest) {
        Bus bus = busRepo.findById((long) tripRequest.busId())
                .orElseThrow(() -> new BusNotFoundException("Bus not found with ID: " + tripRequest.busId()));

        Route route = routeRepo.findById((long) tripRequest.routeId())
                .orElseThrow(() -> new RouteNotFoundException("Route not found with ID: " + tripRequest.routeId()));

        TripSchedule tripSchedule = new TripSchedule();
        tripSchedule.setBus(bus);
        tripSchedule.setRoute(route);
        tripSchedule.setStartTime(tripRequest.startTime());
        tripSchedule.setArrivalTime(tripRequest.arrivalTime());
        tripSchedule.setFare(tripRequest.fare());

        TripSchedule savedTrip = tripScheduleRepo.save(tripSchedule);

        return dtoMapper.toResponse(savedTrip);
    }
}
