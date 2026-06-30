package in.gov.cgg.ticketbookingsystem.controller;

import in.gov.cgg.ticketbookingsystem.model.dto.response.TripResponse;
import in.gov.cgg.ticketbookingsystem.model.dto.response.TripSeatResponse;
import in.gov.cgg.ticketbookingsystem.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;
    private final in.gov.cgg.ticketbookingsystem.service.TravelPlannerService travelPlannerService;

    @GetMapping
    public ResponseEntity<List<TripResponse>> searchTrips(
            @RequestParam(required = false) String sourceCity,
            @RequestParam(required = false) String destinationCity,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<TripResponse> trips = tripService.searchTrips(sourceCity, destinationCity, date);
        return ResponseEntity.ok(trips);
    }

    @GetMapping("/{id}/seats")
    public ResponseEntity<List<TripSeatResponse>> getSeatsForTrip(@PathVariable Long id) {
        List<TripSeatResponse> seats = tripService.getSeatsForTrip(id);
        return ResponseEntity.ok(seats);
    }

    @PostMapping("/plan")
    public ResponseEntity<in.gov.cgg.ticketbookingsystem.model.dto.response.TravelPlanResponse> planTravel(
            @jakarta.validation.Valid @RequestBody in.gov.cgg.ticketbookingsystem.model.dto.request.TravelPlanRequest request) {
        return ResponseEntity.ok(travelPlannerService.planTravel(request));
    }
}
