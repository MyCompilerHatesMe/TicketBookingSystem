package in.gov.cgg.ticketbookingsystem.controller;

import in.gov.cgg.ticketbookingsystem.exception.AccountExistsException;
import in.gov.cgg.ticketbookingsystem.exception.SeatAlreadyOccupiedException;
import in.gov.cgg.ticketbookingsystem.model.dto.request.BookingRequest;
import in.gov.cgg.ticketbookingsystem.model.dto.response.BookingResponse;
import in.gov.cgg.ticketbookingsystem.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@RequestBody BookingRequest request) {
        BookingResponse response = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @ExceptionHandler(AccountExistsException.class)
    public ResponseEntity<Map<String, String>> handleAccountExists(AccountExistsException ex) {
        return ResponseEntity.status(HttpStatus.SEE_OTHER).body(Map.of(
                "error", "ACCOUNT_EXISTS",
                "message", ex.getMessage()
        ));
    }

    @ExceptionHandler(SeatAlreadyOccupiedException.class)
    public ResponseEntity<Map<String, String>> handleSeatOccupied(SeatAlreadyOccupiedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "error", "SEAT_OCCUPIED",
                "message", ex.getMessage()
        ));
    }
}
