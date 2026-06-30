package in.gov.cgg.ticketbookingsystem.service;

import in.gov.cgg.ticketbookingsystem.exception.*;
import in.gov.cgg.ticketbookingsystem.model.SeatStatus;
import in.gov.cgg.ticketbookingsystem.model.SimpleStatus;
import in.gov.cgg.ticketbookingsystem.model.dto.request.BookingRequest;
import in.gov.cgg.ticketbookingsystem.model.dto.request.GuestInfo;
import in.gov.cgg.ticketbookingsystem.model.dto.response.BookingResponse;
import in.gov.cgg.ticketbookingsystem.model.dto.response.BookingHistoryResponse;
import in.gov.cgg.ticketbookingsystem.model.core.*;
import in.gov.cgg.ticketbookingsystem.model.operations.TripSchedule;
import in.gov.cgg.ticketbookingsystem.model.operations.TripSeat;
import in.gov.cgg.ticketbookingsystem.model.transactions.Booking;
import in.gov.cgg.ticketbookingsystem.model.users.AuthUser;
import in.gov.cgg.ticketbookingsystem.model.users.UserGuest;
import in.gov.cgg.ticketbookingsystem.model.users.UserMaster;
import in.gov.cgg.ticketbookingsystem.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.BadCredentialsException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepo bookingRepo;
    @Mock
    private TripSeatRepo tripSeatRepo;
    @Mock
    private TripScheduleRepo tripScheduleRepo;
    @Mock
    private UserMasterRepo userMasterRepo;
    @Mock
    private UserGuestRepo userGuestRepo;
    @Mock
    private AuthUserRepo authUserRepo;
    @Mock
    private RouteStopRepo routeStopRepo;

    @InjectMocks
    private BookingService bookingService;

    private TripSchedule sampleTrip;
    private Route sampleRoute;
    private List<Long> seatIds;
    private List<TripSeat> tripSeats;

    @BeforeEach
    void setUp() {
        sampleRoute = new Route();
        sampleRoute.setRouteId(10L);
        sampleRoute.setSourceCity("Hyderabad");
        sampleRoute.setDestinationCity("Bangalore");

        Bus bus = new Bus();
        bus.setBusId(1L);
        bus.setBusName("Airavat");
        bus.setBusNumber("KA01F1234");

        sampleTrip = new TripSchedule();
        sampleTrip.setTripId(100L);
        sampleTrip.setRoute(sampleRoute);
        sampleTrip.setBus(bus);
        sampleTrip.setFare(BigDecimal.valueOf(500));
        sampleTrip.setStartTime(LocalDateTime.now().plusHours(5));
        sampleTrip.setArrivalTime(LocalDateTime.now().plusHours(12));

        seatIds = List.of(201L, 202L);

        Seat seat1 = new Seat();
        seat1.setSeatId(201L);
        seat1.setSeatNumber("A1");

        Seat seat2 = new Seat();
        seat2.setSeatId(202L);
        seat2.setSeatNumber("A2");

        TripSeat ts1 = new TripSeat();
        ts1.setId(1001L);
        ts1.setSeat(seat1);
        ts1.setStatus(SeatStatus.AVAILABLE);

        TripSeat ts2 = new TripSeat();
        ts2.setId(1002L);
        ts2.setSeat(seat2);
        ts2.setStatus(SeatStatus.AVAILABLE);

        tripSeats = new ArrayList<>(List.of(ts1, ts2));
    }

    @Test
    void createBooking_asGuest_success() {
        GuestInfo guestInfo = new GuestInfo("guest@example.com", "9999999999");
        BookingRequest request = new BookingRequest(
                100L, seatIds, true, guestInfo, null, false, null, null
        );

        when(userMasterRepo.existsByEmail("guest@example.com")).thenReturn(false);
        
        UserGuest savedGuest = new UserGuest();
        savedGuest.setGuestId(50L);
        savedGuest.setEmail("guest@example.com");
        savedGuest.setNumber("9999999999");
        when(userGuestRepo.findByEmail("guest@example.com")).thenReturn(Optional.empty());
        when(userGuestRepo.save(any(UserGuest.class))).thenReturn(savedGuest);

        when(tripSeatRepo.findSeatsForUpdate(eq(100L), eq(seatIds))).thenReturn(tripSeats);
        when(tripScheduleRepo.findById(100L)).thenReturn(Optional.of(sampleTrip));

        UUID bookingUuid = UUID.randomUUID();
        Booking savedBooking = new Booking();
        savedBooking.setId(1000L);
        savedBooking.setUuid(bookingUuid);
        savedBooking.setTotal_amount(BigDecimal.valueOf(1000));
        savedBooking.setStatus(SimpleStatus.PENDING);
        savedBooking.setExpiryTime(LocalDateTime.now().plusMinutes(10));
        savedBooking.setTrip(sampleTrip);
        savedBooking.setTripSeats(tripSeats);

        when(bookingRepo.save(any(Booking.class))).thenReturn(savedBooking);

        BookingResponse response = bookingService.createBooking(request);

        assertNotNull(response);
        assertEquals(1000L, response.bookingId());
        assertEquals(bookingUuid, response.uuid());
        assertEquals(BigDecimal.valueOf(1000), response.totalAmount());
        assertEquals(SimpleStatus.PENDING, response.status());

        verify(userGuestRepo, times(1)).save(any(UserGuest.class));
        verify(tripSeatRepo, times(1)).saveAll(anyList());
    }

    @Test
    void createBooking_guestDetailsMissing_throwsException() {
        BookingRequest request = new BookingRequest(
                100L, seatIds, true, null, null, false, null, null
        );

        assertThrows(IllegalArgumentException.class, () -> bookingService.createBooking(request));
    }

    @Test
    void createBooking_guestAccountExists_throwsException() {
        GuestInfo guestInfo = new GuestInfo("user@example.com", "9999999999");
        BookingRequest request = new BookingRequest(
                100L, seatIds, true, guestInfo, null, false, null, null
        );

        when(userMasterRepo.existsByEmail("user@example.com")).thenReturn(true);

        assertThrows(AccountExistsException.class, () -> bookingService.createBooking(request));
    }

    @Test
    void createBooking_asUserWithId_success() {
        BookingRequest request = new BookingRequest(
                100L, seatIds, false, null, 1L, false, null, null
        );

        UserMaster userMaster = new UserMaster();
        userMaster.setUserId(1L);
        userMaster.setEmail("user@example.com");

        when(userMasterRepo.findById(1L)).thenReturn(Optional.of(userMaster));
        when(tripSeatRepo.findSeatsForUpdate(100L, seatIds)).thenReturn(tripSeats);
        when(tripScheduleRepo.findById(100L)).thenReturn(Optional.of(sampleTrip));

        Booking savedBooking = new Booking();
        savedBooking.setId(1000L);
        savedBooking.setTotal_amount(BigDecimal.valueOf(1000));
        savedBooking.setStatus(SimpleStatus.PENDING);
        savedBooking.setTrip(sampleTrip);
        savedBooking.setTripSeats(tripSeats);

        when(bookingRepo.save(any(Booking.class))).thenReturn(savedBooking);

        BookingResponse response = bookingService.createBooking(request);

        assertNotNull(response);
        assertEquals(BigDecimal.valueOf(1000), response.totalAmount());
        verify(userMasterRepo, times(1)).findById(1L);
    }

    @Test
    void createBooking_userNotFound_throwsException() {
        BookingRequest request = new BookingRequest(
                100L, seatIds, false, null, 999L, false, null, null
        );

        when(userMasterRepo.findById(999L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> bookingService.createBooking(request));
    }

    @Test
    void createBooking_seatsNotFound_throwsException() {
        BookingRequest request = new BookingRequest(
                100L, seatIds, false, null, 1L, false, null, null
        );

        UserMaster userMaster = new UserMaster();
        userMaster.setUserId(1L);
        when(userMasterRepo.findById(1L)).thenReturn(Optional.of(userMaster));

        // Returns only 1 seat instead of 2
        when(tripSeatRepo.findSeatsForUpdate(100L, seatIds)).thenReturn(List.of(tripSeats.get(0)));

        assertThrows(SeatNotFoundException.class, () -> bookingService.createBooking(request));
    }

    @Test
    void createBooking_seatAlreadyOccupied_throwsException() {
        BookingRequest request = new BookingRequest(
                100L, seatIds, false, null, 1L, false, null, null
        );

        UserMaster userMaster = new UserMaster();
        userMaster.setUserId(1L);
        when(userMasterRepo.findById(1L)).thenReturn(Optional.of(userMaster));

        // One seat is already BOOKED
        tripSeats.get(1).setStatus(SeatStatus.BOOKED);
        when(tripSeatRepo.findSeatsForUpdate(100L, seatIds)).thenReturn(tripSeats);

        assertThrows(SeatAlreadyOccupiedException.class, () -> bookingService.createBooking(request));
    }

    @Test
    void createBooking_withExpiredLock_allowsBooking() {
        BookingRequest request = new BookingRequest(
                100L, seatIds, false, null, 1L, false, null, null
        );

        UserMaster userMaster = new UserMaster();
        userMaster.setUserId(1L);
        when(userMasterRepo.findById(1L)).thenReturn(Optional.of(userMaster));

        // Seat has expired lock
        tripSeats.get(1).setStatus(SeatStatus.PENDING);
        tripSeats.get(1).setLockExpiresAt(LocalDateTime.now().minusMinutes(5));

        when(tripSeatRepo.findSeatsForUpdate(100L, seatIds)).thenReturn(tripSeats);
        when(tripScheduleRepo.findById(100L)).thenReturn(Optional.of(sampleTrip));

        Booking savedBooking = new Booking();
        savedBooking.setId(1000L);
        savedBooking.setTotal_amount(BigDecimal.valueOf(1000));
        savedBooking.setStatus(SimpleStatus.PENDING);
        savedBooking.setTrip(sampleTrip);
        savedBooking.setTripSeats(tripSeats);

        when(bookingRepo.save(any(Booking.class))).thenReturn(savedBooking);

        BookingResponse response = bookingService.createBooking(request);

        assertNotNull(response);
        assertEquals(SimpleStatus.PENDING, response.status());
    }

    @Test
    void createBooking_invalidBoardingStop_throwsException() {
        BookingRequest request = new BookingRequest(
                100L, seatIds, false, null, 1L, false, 801L, null
        );

        UserMaster userMaster = new UserMaster();
        userMaster.setUserId(1L);
        when(userMasterRepo.findById(1L)).thenReturn(Optional.of(userMaster));
        when(tripSeatRepo.findSeatsForUpdate(100L, seatIds)).thenReturn(tripSeats);
        when(tripScheduleRepo.findById(100L)).thenReturn(Optional.of(sampleTrip));

        RouteStop stop = new RouteStop();
        stop.setRouteStopId(801L);
        // Route doesn't match
        Route diffRoute = new Route();
        diffRoute.setRouteId(99L);
        stop.setRoute(diffRoute);

        when(routeStopRepo.findById(801L)).thenReturn(Optional.of(stop));

        assertThrows(IllegalArgumentException.class, () -> bookingService.createBooking(request));
    }

    @Test
    void createBooking_boardingStopNotBoardingType_throwsException() {
        BookingRequest request = new BookingRequest(
                100L, seatIds, false, null, 1L, false, 801L, null
        );

        UserMaster userMaster = new UserMaster();
        userMaster.setUserId(1L);
        when(userMasterRepo.findById(1L)).thenReturn(Optional.of(userMaster));
        when(tripSeatRepo.findSeatsForUpdate(100L, seatIds)).thenReturn(tripSeats);
        when(tripScheduleRepo.findById(100L)).thenReturn(Optional.of(sampleTrip));

        RouteStop stop = new RouteStop();
        stop.setRouteStopId(801L);
        stop.setRoute(sampleRoute);
        stop.setStopType("DROPPING"); // should be BOARDING or BOTH

        when(routeStopRepo.findById(801L)).thenReturn(Optional.of(stop));

        assertThrows(IllegalArgumentException.class, () -> bookingService.createBooking(request));
    }

    @Test
    void getBookingHistory_asLoggedInUser_success() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("testuser");

        AuthUser authUser = new AuthUser();
        authUser.setUsername("testuser");
        UserMaster userMaster = new UserMaster();
        userMaster.setUserId(10L);
        authUser.setUserMaster(userMaster);

        when(authUserRepo.findByUsername("testuser")).thenReturn(Optional.of(authUser));

        Booking booking = new Booking();
        booking.setId(2000L);
        UUID bookingUuid = UUID.randomUUID();
        booking.setUuid(bookingUuid);
        booking.setTotal_amount(BigDecimal.valueOf(500));
        booking.setStatus(SimpleStatus.SUCCESS);
        booking.setBookingTime(LocalDateTime.now());
        booking.setTrip(sampleTrip);
        booking.setTripSeats(tripSeats);

        when(bookingRepo.findByUserMasterUserId(10L)).thenReturn(List.of(booking));

        List<BookingHistoryResponse> history = bookingService.getBookingHistory(null, null);

        assertNotNull(history);
        assertEquals(1, history.size());
        assertEquals(2000L, history.get(0).bookingId());
        assertEquals("Hyderabad", history.get(0).sourceCity());

        // Cleanup SecurityContext
        SecurityContextHolder.clearContext();
    }

    @Test
    void getBookingHistory_asGuestValidOTP_success() {
        SecurityContextHolder.clearContext(); // Ensure anonymous

        UserGuest guest = new UserGuest();
        guest.setGuestId(50L);
        guest.setEmail("guest@example.com");

        when(userGuestRepo.findByEmail("guest@example.com")).thenReturn(Optional.of(guest));

        Booking booking = new Booking();
        booking.setId(3000L);
        UUID bookingUuid = UUID.randomUUID();
        booking.setUuid(bookingUuid);
        booking.setTotal_amount(BigDecimal.valueOf(500));
        booking.setStatus(SimpleStatus.SUCCESS);
        booking.setBookingTime(LocalDateTime.now());
        booking.setTrip(sampleTrip);
        booking.setTripSeats(tripSeats);

        when(bookingRepo.findByUserGuestGuestId(50L)).thenReturn(List.of(booking));

        List<BookingHistoryResponse> history = bookingService.getBookingHistory("guest@example.com", "000000");

        assertNotNull(history);
        assertEquals(1, history.size());
        assertEquals(3000L, history.get(0).bookingId());
    }

    @Test
    void getBookingHistory_asGuestInvalidOTP_throwsException() {
        SecurityContextHolder.clearContext();

        assertThrows(BadCredentialsException.class, () ->
                bookingService.getBookingHistory("guest@example.com", "111111"));
    }

    @Test
    void getBookingHistory_asGuestMissingParams_throwsException() {
        SecurityContextHolder.clearContext();

        assertThrows(IllegalArgumentException.class, () ->
                bookingService.getBookingHistory(null, "000000"));
    }
}
