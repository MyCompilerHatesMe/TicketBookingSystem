package in.gov.cgg.ticketbookingsystem.controller;

import in.gov.cgg.ticketbookingsystem.exception.AccountExistsException;
import in.gov.cgg.ticketbookingsystem.exception.SeatAlreadyOccupiedException;
import in.gov.cgg.ticketbookingsystem.model.dto.request.BookingRequest;
import in.gov.cgg.ticketbookingsystem.model.dto.response.BookingResponse;
import in.gov.cgg.ticketbookingsystem.model.dto.response.BookingHistoryResponse;
import in.gov.cgg.ticketbookingsystem.service.BookingService;
import lombok.RequiredArgsConstructor;
import java.util.List;
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

    @GetMapping("/history")
    public ResponseEntity<List<BookingHistoryResponse>> getBookingHistory(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String otp) {
        List<BookingHistoryResponse> response = bookingService.getBookingHistory(email, otp);
        return ResponseEntity.ok(response);
    }

}
