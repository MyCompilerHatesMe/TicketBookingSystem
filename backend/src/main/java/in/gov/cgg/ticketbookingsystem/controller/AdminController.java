package in.gov.cgg.ticketbookingsystem.controller;

import in.gov.cgg.ticketbookingsystem.model.dto.request.BusRequest;
import in.gov.cgg.ticketbookingsystem.model.dto.request.RouteRequest;
import in.gov.cgg.ticketbookingsystem.model.dto.request.TripRequest;
import in.gov.cgg.ticketbookingsystem.model.dto.request.RouteStopRequest;
import in.gov.cgg.ticketbookingsystem.model.dto.response.BusResponse;
import in.gov.cgg.ticketbookingsystem.model.dto.response.RouteResponse;
import in.gov.cgg.ticketbookingsystem.model.dto.response.TripResponse;
import in.gov.cgg.ticketbookingsystem.model.dto.response.RouteStopResponse;
import in.gov.cgg.ticketbookingsystem.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService service;

    @PostMapping("/buses")
    public ResponseEntity<BusResponse> addBus(@Valid @RequestBody BusRequest busRequest) {
        BusResponse response = service.addBus(busRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/routes")
    public ResponseEntity<RouteResponse> addRoute(@Valid @RequestBody RouteRequest routeRequest) {
        RouteResponse response = service.addRoute(routeRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/routes/{routeId}/stops")
    public ResponseEntity<List<RouteStopResponse>> addStops(
            @PathVariable Long routeId,
            @Valid @RequestBody List<RouteStopRequest> stopRequests) {
        List<RouteStopResponse> response = service.addStops(routeId, stopRequests);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/trips")
    public ResponseEntity<TripResponse> addTrip(@Valid @RequestBody TripRequest tripRequest) {
        TripResponse response = service.addTrip(tripRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/buses")
    public ResponseEntity<List<BusResponse>> getAllBus() {
        return new ResponseEntity<>(service.getAllBuses(), HttpStatus.OK);
    }

    @GetMapping("/routes")
    public ResponseEntity<List<RouteResponse>> getAllRoutes() {
        return new ResponseEntity<>(service.getAllRoutes(), HttpStatus.OK);
    }

    @GetMapping("/trips")
    public ResponseEntity<List<TripResponse>> getAllTrips() {
        return new ResponseEntity<>(service.getAllTrips(), HttpStatus.OK);
    }
}