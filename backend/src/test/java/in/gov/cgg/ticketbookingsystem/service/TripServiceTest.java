package in.gov.cgg.ticketbookingsystem.service;

import in.gov.cgg.ticketbookingsystem.model.SeatStatus;
import in.gov.cgg.ticketbookingsystem.model.core.Route;
import in.gov.cgg.ticketbookingsystem.model.core.Seat;
import in.gov.cgg.ticketbookingsystem.model.dto.response.TripResponse;
import in.gov.cgg.ticketbookingsystem.model.dto.response.TripSeatResponse;
import in.gov.cgg.ticketbookingsystem.model.operations.TripSchedule;
import in.gov.cgg.ticketbookingsystem.model.operations.TripSeat;
import in.gov.cgg.ticketbookingsystem.repository.TripScheduleRepo;
import in.gov.cgg.ticketbookingsystem.repository.TripSeatRepo;
import in.gov.cgg.ticketbookingsystem.utility.DtoMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TripServiceTest {

    @Mock
    private TripScheduleRepo tripScheduleRepo;

    @Mock
    private TripSeatRepo tripSeatRepo;

    @Mock
    private DtoMapper dtoMapper;

    @InjectMocks
    private TripService tripService;

    @Test
    void searchTrips_withDate_success() {
        LocalDate date = LocalDate.of(2026, 7, 1);
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        TripSchedule schedule = new TripSchedule();
        schedule.setTripId(100L);

        TripResponse expectedResponse = new TripResponse(
                100L, 1L, "KA01F1234", "Airavat", "AC_SLEEPER",
                10L, "Hyderabad", "Bangalore", 500,
                LocalDateTime.now(), LocalDateTime.now().plusHours(8), BigDecimal.valueOf(600),
                Collections.emptyList()
        );

        when(tripScheduleRepo.findTripsWithOptionalFilters("Hyderabad", "Bangalore", startOfDay, endOfDay))
                .thenReturn(List.of(schedule));
        when(dtoMapper.toResponse(schedule)).thenReturn(expectedResponse);

        List<TripResponse> results = tripService.searchTrips("Hyderabad", "Bangalore", date);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(100L, results.get(0).tripId());
        verify(tripScheduleRepo, times(1))
                .findTripsWithOptionalFilters("Hyderabad", "Bangalore", startOfDay, endOfDay);
    }

    @Test
    void searchTrips_withoutDate_success() {
        TripSchedule schedule = new TripSchedule();
        schedule.setTripId(101L);

        TripResponse expectedResponse = new TripResponse(
                101L, 1L, "KA01F1234", "Airavat", "AC_SLEEPER",
                10L, "Hyderabad", "Bangalore", 500,
                LocalDateTime.now(), LocalDateTime.now().plusHours(8), BigDecimal.valueOf(600),
                Collections.emptyList()
        );

        when(tripScheduleRepo.findTripsWithOptionalFilters("Hyderabad", "Bangalore", null, null))
                .thenReturn(List.of(schedule));
        when(dtoMapper.toResponse(schedule)).thenReturn(expectedResponse);

        List<TripResponse> results = tripService.searchTrips("Hyderabad", "Bangalore", null);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(101L, results.get(0).tripId());
        verify(tripScheduleRepo, times(1))
                .findTripsWithOptionalFilters("Hyderabad", "Bangalore", null, null);
    }

    @Test
    void getSeatsForTrip_success() {
        Seat seat = new Seat();
        seat.setSeatId(201L);
        seat.setSeatNumber("A1");

        TripSeat tripSeat = new TripSeat();
        tripSeat.setId(1001L);
        tripSeat.setSeat(seat);
        tripSeat.setStatus(SeatStatus.AVAILABLE);

        when(tripSeatRepo.findByTripScheduleTripId(100L)).thenReturn(List.of(tripSeat));

        List<TripSeatResponse> seats = tripService.getSeatsForTrip(100L);

        assertNotNull(seats);
        assertEquals(1, seats.size());
        assertEquals(201L, seats.get(0).seatId());
        assertEquals("A1", seats.get(0).seatNumber());
        assertEquals(SeatStatus.AVAILABLE, seats.get(0).status());
        verify(tripSeatRepo, times(1)).findByTripScheduleTripId(100L);
    }
}
