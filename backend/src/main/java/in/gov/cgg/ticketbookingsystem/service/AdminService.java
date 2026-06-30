package in.gov.cgg.ticketbookingsystem.service;

import in.gov.cgg.ticketbookingsystem.exception.BusNotFoundException;
import in.gov.cgg.ticketbookingsystem.exception.RouteNotFoundException;
import in.gov.cgg.ticketbookingsystem.exception.TooManySeatsException;
import in.gov.cgg.ticketbookingsystem.model.SeatStatus;
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
import in.gov.cgg.ticketbookingsystem.model.operations.TripSeat;
import in.gov.cgg.ticketbookingsystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import in.gov.cgg.ticketbookingsystem.utility.DtoMapper;
import in.gov.cgg.ticketbookingsystem.model.core.RouteStop;
import in.gov.cgg.ticketbookingsystem.model.dto.request.RouteStopRequest;
import in.gov.cgg.ticketbookingsystem.model.dto.response.RouteStopResponse;
import org.springframework.transaction.annotation.Transactional;

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
    private final TripSeatRepo tripSeatRepo;
    private final RouteStopRepo routeStopRepo;

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

    @Transactional
    public List<RouteStopResponse> addStops(Long routeId, List<RouteStopRequest> stopRequests) {
        Route route = routeRepo.findById(routeId)
                .orElseThrow(() -> new RouteNotFoundException("Route not found with ID: " + routeId));

        // Clear existing stops
        route.getStops().clear();
        routeRepo.save(route);

        List<RouteStop> stops = stopRequests.stream().map(req -> {
            RouteStop stop = new RouteStop();
            stop.setRoute(route);
            stop.setStopName(req.stopName());
            stop.setStopType(req.stopType().toUpperCase());
            stop.setMinutesOffset(req.minutesOffset());
            stop.setSequence(req.sequence());
            return stop;
        }).toList();

        List<RouteStop> savedStops = routeStopRepo.saveAll(stops);

        return savedStops.stream().map(stop -> new RouteStopResponse(
                stop.getRouteStopId(),
                stop.getStopName(),
                stop.getStopType(),
                stop.getMinutesOffset(),
                stop.getSequence(),
                null
        )).toList();
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
        List<Seat> busSeats = seatRepo.findByBus(bus);

        List<TripSeat> tripSeats = busSeats.stream().map(seat -> {
            TripSeat tripSeat = new TripSeat();
            tripSeat.setTripSchedule(savedTrip);
            tripSeat.setSeat(seat);
            tripSeat.setStatus(SeatStatus.AVAILABLE);
            return tripSeat;
        }).toList();

        tripSeatRepo.saveAll(tripSeats);

        return dtoMapper.toResponse(savedTrip);
    }

    public List<BusResponse> getAllBuses () {
        // yes, this returns null for the seats, shouldn't matter
        return busRepo.findAll().stream().map((bus) -> dtoMapper.toResponse(bus, null)).toList();
    }

    public List<RouteResponse> getAllRoutes () {
        return routeRepo.findAll().stream().map(dtoMapper::toResponse).toList();
    }

    public List<TripResponse> getAllTrips () {
        return tripScheduleRepo.findAll().stream().map(dtoMapper::toResponse).toList();
    }

}
