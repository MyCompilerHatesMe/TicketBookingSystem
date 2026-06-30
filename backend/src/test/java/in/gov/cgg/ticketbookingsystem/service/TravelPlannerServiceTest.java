package in.gov.cgg.ticketbookingsystem.service;

import in.gov.cgg.ticketbookingsystem.model.dto.request.DestinationRequest;
import in.gov.cgg.ticketbookingsystem.model.dto.request.TravelPlanRequest;
import in.gov.cgg.ticketbookingsystem.model.dto.response.TravelPlanResponse;
import in.gov.cgg.ticketbookingsystem.model.dto.response.TripResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TravelPlannerServiceTest {

    @Mock
    private TripService tripService;

    @InjectMocks
    private TravelPlannerService travelPlannerService;

    @Test
    void planTravel_fixedOrder_success() {
        LocalDate startDate = LocalDate.of(2026, 7, 1);
        List<DestinationRequest> destinations = List.of(
                new DestinationRequest("B", LocalDate.of(2026, 7, 1)),
                new DestinationRequest("C", LocalDate.of(2026, 7, 2))
        );

        TravelPlanRequest request = new TravelPlanRequest("A", startDate, destinations, false);

        // Trip A -> B (arrives on 2026-07-01)
        TripResponse trip1 = new TripResponse(
                101L, 1L, "KA01F1234", "Airavat", "AC_SLEEPER",
                10L, "A", "B", 200,
                LocalDateTime.of(2026, 7, 1, 8, 0), LocalDateTime.of(2026, 7, 1, 12, 0),
                BigDecimal.valueOf(300), Collections.emptyList()
        );

        // Trip B -> C (arrives on 2026-07-02, starts after 2-hour buffer)
        TripResponse trip2 = new TripResponse(
                102L, 1L, "KA01F1234", "Airavat", "AC_SLEEPER",
                11L, "B", "C", 300,
                LocalDateTime.of(2026, 7, 2, 8, 0), LocalDateTime.of(2026, 7, 2, 14, 0),
                BigDecimal.valueOf(400), Collections.emptyList()
        );

        when(tripService.searchTrips(eq("A"), eq("B"), isNull())).thenReturn(List.of(trip1));
        when(tripService.searchTrips(eq("B"), eq("C"), isNull())).thenReturn(List.of(trip2));

        TravelPlanResponse response = travelPlannerService.planTravel(request);

        assertNotNull(response);
        assertNotNull(response.cheapestRoute());
        assertNotNull(response.shortestDistanceRoute());
        assertEquals(2, response.cheapestRoute().trips().size());
        assertEquals(BigDecimal.valueOf(700), response.cheapestRoute().totalFare());
        assertEquals(500, response.cheapestRoute().totalDistance());
    }

    @Test
    void planTravel_insufficientBuffer_noRouteFound() {
        LocalDate startDate = LocalDate.of(2026, 7, 1);
        List<DestinationRequest> destinations = List.of(
                new DestinationRequest("B", LocalDate.of(2026, 7, 1)),
                new DestinationRequest("C", LocalDate.of(2026, 7, 1))
        );

        TravelPlanRequest request = new TravelPlanRequest("A", startDate, destinations, false);

        // Trip A -> B arrives at 12:00
        TripResponse trip1 = new TripResponse(
                101L, 1L, "KA01F1234", "Airavat", "AC_SLEEPER",
                10L, "A", "B", 200,
                LocalDateTime.of(2026, 7, 1, 8, 0), LocalDateTime.of(2026, 7, 1, 12, 0),
                BigDecimal.valueOf(300), Collections.emptyList()
        );

        // Trip B -> C starts at 13:00 (only 1 hour buffer, needs at least 2 hours)
        TripResponse trip2 = new TripResponse(
                102L, 1L, "KA01F1234", "Airavat", "AC_SLEEPER",
                11L, "B", "C", 300,
                LocalDateTime.of(2026, 7, 1, 13, 0), LocalDateTime.of(2026, 7, 1, 18, 0),
                BigDecimal.valueOf(400), Collections.emptyList()
        );

        when(tripService.searchTrips(eq("A"), eq("B"), isNull())).thenReturn(List.of(trip1));
        when(tripService.searchTrips(eq("B"), eq("C"), isNull())).thenReturn(List.of(trip2));

        TravelPlanResponse response = travelPlannerService.planTravel(request);

        assertNotNull(response);
        assertNull(response.cheapestRoute());
        assertNull(response.shortestDistanceRoute());
    }

    @Test
    void planTravel_flexibleOrder_success() {
        LocalDate startDate = LocalDate.of(2026, 7, 1);
        // By setting arrivalDate to null, we avoid date-matching constraints on the hops
        List<DestinationRequest> destinations = new ArrayList<>(List.of(
                new DestinationRequest("B", null),
                new DestinationRequest("C", null)
        ));

        // Test with flexibleOrder = true
        TravelPlanRequest request = new TravelPlanRequest("A", startDate, destinations, true);

        // For sequence: A -> B -> C
        TripResponse tripAB = new TripResponse(101L, 1L, "KA01F1234", "Airavat", "AC_SLEEPER", 10L, "A", "B", 200,
                LocalDateTime.of(2026, 7, 1, 8, 0), LocalDateTime.of(2026, 7, 1, 12, 0), BigDecimal.valueOf(500), Collections.emptyList());
        TripResponse tripBC = new TripResponse(102L, 1L, "KA01F1234", "Airavat", "AC_SLEEPER", 11L, "B", "C", 300,
                LocalDateTime.of(2026, 7, 1, 15, 0), LocalDateTime.of(2026, 7, 1, 21, 0), BigDecimal.valueOf(500), Collections.emptyList());

        // For sequence: A -> C -> B
        TripResponse tripAC = new TripResponse(103L, 1L, "KA01F1234", "Airavat", "AC_SLEEPER", 12L, "A", "C", 150,
                LocalDateTime.of(2026, 7, 1, 8, 0), LocalDateTime.of(2026, 7, 1, 12, 0), BigDecimal.valueOf(200), Collections.emptyList());
        TripResponse tripCB = new TripResponse(104L, 1L, "KA01F1234", "Airavat", "AC_SLEEPER", 13L, "C", "B", 100,
                LocalDateTime.of(2026, 7, 1, 15, 0), LocalDateTime.of(2026, 7, 1, 21, 0), BigDecimal.valueOf(200), Collections.emptyList());

        when(tripService.searchTrips(eq("A"), eq("B"), isNull())).thenReturn(List.of(tripAB));
        when(tripService.searchTrips(eq("B"), eq("C"), isNull())).thenReturn(List.of(tripBC));
        when(tripService.searchTrips(eq("A"), eq("C"), isNull())).thenReturn(List.of(tripAC));
        when(tripService.searchTrips(eq("C"), eq("B"), isNull())).thenReturn(List.of(tripCB));

        TravelPlanResponse response = travelPlannerService.planTravel(request);

        assertNotNull(response);
        assertNotNull(response.cheapestRoute());
        // A -> C -> B total fare should be 400 (cheaper than A -> B -> C total fare 1000)
        assertEquals(BigDecimal.valueOf(400), response.cheapestRoute().totalFare());
        assertEquals(250, response.cheapestRoute().totalDistance());
    }
}
