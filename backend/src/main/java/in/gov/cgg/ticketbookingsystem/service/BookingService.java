package in.gov.cgg.ticketbookingsystem.service;

import in.gov.cgg.ticketbookingsystem.exception.AccountExistsException;
import in.gov.cgg.ticketbookingsystem.exception.SeatAlreadyOccupiedException;
import in.gov.cgg.ticketbookingsystem.model.SeatStatus;
import in.gov.cgg.ticketbookingsystem.model.SimpleStatus;
import in.gov.cgg.ticketbookingsystem.model.dto.request.BookingRequest;
import in.gov.cgg.ticketbookingsystem.model.dto.response.BookingResponse;
import in.gov.cgg.ticketbookingsystem.model.operations.TripSchedule;
import in.gov.cgg.ticketbookingsystem.model.operations.TripSeat;
import in.gov.cgg.ticketbookingsystem.model.transactions.Booking;
import in.gov.cgg.ticketbookingsystem.model.users.UserGuest;
import in.gov.cgg.ticketbookingsystem.model.users.UserMaster;
import in.gov.cgg.ticketbookingsystem.model.users.AuthUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import in.gov.cgg.ticketbookingsystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepo bookingRepo;
    private final TripSeatRepo tripSeatRepo;
    private final TripScheduleRepo tripScheduleRepo;
    private final UserMasterRepo userMasterRepo;
    private final UserGuestRepo userGuestRepo;
    private final AuthUserRepo authUserRepo;

    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        UserMaster userMaster = null;
        UserGuest userGuest = null;

        if (Boolean.TRUE.equals(request.isGuest())) {
            if (request.guestInfo() == null) {
                throw new IllegalArgumentException("Guest details are missing.");
            }

            if (!Boolean.TRUE.equals(request.bypassAccountCheck())) {
                if (userMasterRepo.existsByEmail(request.guestInfo().email())) {
                    throw new AccountExistsException("An account already exists with this email address.");
                }
            }

            userGuest = userGuestRepo.findByEmail(request.guestInfo().email())
                    .orElseGet(() -> {
                        UserGuest newGuest = new UserGuest();
                        newGuest.setEmail(request.guestInfo().email());
                        newGuest.setNumber(request.guestInfo().number());
                        return userGuestRepo.save(newGuest);
                    });
        } else {
            if (request.userId() != null) {
                userMaster = userMasterRepo.findById(request.userId())
                        .orElseThrow(() -> new RuntimeException("User not found with ID: " + request.userId()));
            } else {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication == null || !authentication.isAuthenticated()) {
                    throw new RuntimeException("User is not authenticated");
                }
                String username = authentication.getName();
                AuthUser authUser = authUserRepo.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("Authenticated user not found in auth user repository: " + username));
                
                userMaster = authUser.getUserMaster();
                if (userMaster == null) {
                    userMaster = new UserMaster();
                    String email = username.contains("@") ? username : username + "@example.com";
                    userMaster.setEmail(email);
                    userMaster.setName(username);
                    userMaster.setCreatedOn(LocalDateTime.now());
                    userMaster.setAuthUser(authUser); // map owning side
                    userMaster = userMasterRepo.save(userMaster);
                    
                    authUser.setUserMaster(userMaster); // synchronize in-memory ref
                }
            }
        }

        List<TripSeat> tripSeats = tripSeatRepo.findSeatsForUpdate(request.tripId(), request.seatIds());

        if (tripSeats.size() != request.seatIds().size()) {
            throw new RuntimeException("One or more target seats do not exist on this trip schedule.");
        }

        LocalDateTime now = LocalDateTime.now();

        for (TripSeat tripSeat : tripSeats) {
            boolean isAvailable = tripSeat.getStatus() == SeatStatus.AVAILABLE;
            boolean isExpiredLock = tripSeat.getStatus() == SeatStatus.PENDING
                    && tripSeat.getLockExpiresAt() != null
                    && tripSeat.getLockExpiresAt().isBefore(now);

            if (!isAvailable && !isExpiredLock) {
                throw new SeatAlreadyOccupiedException("Seat " + tripSeat.getSeat().getSeatNumber() + " is currently unavailable.");
            }
        }

        TripSchedule trip = tripScheduleRepo.findById(request.tripId())
                .orElseThrow(() -> new RuntimeException("Trip schedule not found with ID: " + request.tripId()));

        BigDecimal totalAmount = trip.getFare().multiply(BigDecimal.valueOf(request.seatIds().size()));
        LocalDateTime expiryTime = now.plusMinutes(10);

        Booking booking = new Booking();
        booking.setUserMaster(userMaster);
        booking.setUserGuest(userGuest);
        booking.setTrip(trip);
        booking.setBookingTime(now);
        booking.setTotal_amount(totalAmount);
        booking.setExpiryTime(expiryTime);
        booking.setStatus(SimpleStatus.PENDING);

        Booking savedBooking = bookingRepo.save(booking);

        for (TripSeat tripSeat : tripSeats) {
            tripSeat.setStatus(SeatStatus.PENDING);
            tripSeat.setLockExpiresAt(expiryTime);
            tripSeat.setBooking(savedBooking);
        }
        tripSeatRepo.saveAll(tripSeats);

        return new BookingResponse(
                savedBooking.getId(),
                savedBooking.getUuid(),
                savedBooking.getTotal_amount(),
                savedBooking.getStatus(),
                savedBooking.getExpiryTime()
        );
    }
}
